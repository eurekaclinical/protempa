package org.protempa;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.dest.Destination;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerCloseException;
import org.protempa.dest.QueryResultsHandlerInitException;
import org.protempa.dest.QueryResultsHandlerProcessingException;
import org.protempa.dest.QueryResultsHandlerValidationFailedException;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.QueryMode;

/**
 *
 * @author Andrew Post
 */
final class Executor implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(Executor.class.getName());
    private final Set<String> propIds;
    private final Filter filters;
    private final PropositionDefinition[] propDefs;
    private final KnowledgeSource ks;
    private final Query query;
    private PropositionDefinitionCache propositionDefinitionCache;
    private final AbstractionFinder abstractionFinder;
    private final Destination destination;
    private QueryResultsHandler resultsHandler;
    private boolean failed;
    private final MessageFormat logMessageFormat;
    private HandleQueryResultThread handleQueryResultThread;
    private boolean canceled;
    private QueryException exception;

    Executor(Query query, Destination resultsHandlerFactory, AbstractionFinder abstractionFinder) throws QueryException {
        this.abstractionFinder = abstractionFinder;
        assert query != null : "query cannot be null";
        assert resultsHandlerFactory != null : "resultsHandlerFactory cannot be null";
        assert abstractionFinder != null : "abstractionFinder cannot be null";
        if (abstractionFinder.isClosed()) {
            throw new QueryException(query.getName(), new ProtempaAlreadyClosedException());
        }
        this.propIds = Arrays.asSet(query.getPropositionIds());
        this.filters = query.getFilters();
        this.propDefs = query.getPropositionDefinitions();
        if (propDefs != null && propDefs.length > 0) {
            ks = new KnowledgeSourceImplWrapper(abstractionFinder.getKnowledgeSource(), propDefs);
        } else {
            ks = abstractionFinder.getKnowledgeSource();
        }
        this.query = query;
        this.destination = resultsHandlerFactory;
        this.logMessageFormat = ProtempaUtil.getLogMessageFormat(this.query);
    }

    void init() throws QueryException {
        try {
            createQueryResultsHandler();

            if (isLoggable(Level.FINE)) {
                log(Level.FINE, "Propositions to be queried are {0}", StringUtils.join(this.propIds, ", "));
            }
            extractPropositionDefinitionCache();
        } catch (KnowledgeSourceReadException | QueryResultsHandlerValidationFailedException | QueryResultsHandlerInitException | Error | RuntimeException ex) {
            this.failed = true;
            throw new QueryException(this.query.getName(), ex);
        }
    }

    void cancel() {
        synchronized (this) {
            if (this.handleQueryResultThread != null) {
                this.handleQueryResultThread.interrupt();
            }
            this.canceled = true;
        }
        log(Level.INFO, "Canceled");
    }

    void execute() throws QueryException {
        try {
            RetrieveDataThread retrieveDataThread;
            DoProcessThread doProcessThread;
            synchronized (this) {
                if (this.canceled) {
                    return;
                }
                log(Level.INFO, "Processing data");
                BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue
                        = new ArrayBlockingQueue<>(1000);
                QueueObject hqrPoisonPill = new QueueObject();
                BlockingQueue<QueueObject> hqrQueue = new ArrayBlockingQueue<>(1000);
                QueryMode queryMode = this.query.getQueryMode();
                if (Arrays.contains(QueryMode.etlModes(), queryMode)) {
                    DataStreamingEvent doProcessPoisonPill
                            = new DataStreamingEvent("poison", Collections.emptyList());
                    retrieveDataThread = new RetrieveDataThread(doProcessQueue,
                            doProcessPoisonPill, this.query,
                            this.abstractionFinder.getDataSource(),
                            this.propositionDefinitionCache,
                            this.filters, this.resultsHandler);
                    doProcessThread = new DoRegularProcessThread(doProcessQueue, hqrQueue,
                            doProcessPoisonPill, hqrPoisonPill, this.query,
                            retrieveDataThread, this.abstractionFinder.getAlgorithmSource(),
                            this.abstractionFinder.getKnowledgeSource(),
                            this.propositionDefinitionCache);
                } else {
                    retrieveDataThread = null;
                    doProcessThread = new DoReprocessThread(hqrQueue,
                            hqrPoisonPill, this.query,
                            this.abstractionFinder.getAlgorithmSource(),
                            this.abstractionFinder.getKnowledgeSource(),
                            this.propositionDefinitionCache);

                }
                this.handleQueryResultThread
                        = new HandleQueryResultThread(hqrQueue, hqrPoisonPill,
                                doProcessThread, this.query, this.resultsHandler);
                try {
                    startQueryResultsHandler();
                } catch (QueryResultsHandlerProcessingException ex) {
                    throw new QueryException(this.query.getName(), ex);
                }
                if (retrieveDataThread != null) {
                    retrieveDataThread.start();
                }
                doProcessThread.start();
                this.handleQueryResultThread.start();
            }

            if (retrieveDataThread != null) {
                try {
                    retrieveDataThread.join();

                    for (QueryException e : retrieveDataThread.getExceptions()) {
                        if (this.exception == null) {
                            this.exception = e;
                        } else {
                            this.exception.addSuppressed(e);
                        }
                    }
                    log(Level.INFO, "Done retrieving data");
                } catch (InterruptedException ex) {
                    log(Level.FINER, "Protempa producer thread join interrupted", ex);
                }
            }
            try {
                doProcessThread.join();
                for (Iterator<QueryException> itr = doProcessThread.getExceptions().iterator(); itr.hasNext();) {
                    QueryException e = itr.next();
                    if (this.exception == null) {
                        this.exception = e;
                    } else {
                        this.exception.addSuppressed(e);
                    }
                }
                log(Level.INFO, "Done processing data");
            } catch (InterruptedException ex) {
                log(Level.FINER, "Protempa consumer thread join interrupted", ex);
            }
            try {
                this.handleQueryResultThread.join();
                for (QueryException e : handleQueryResultThread.getExceptions()) {
                    if (this.exception == null) {
                        this.exception = e;
                    } else {
                        this.exception.addSuppressed(e);
                    }
                }
                log(Level.INFO, "Done outputting results");
            } catch (InterruptedException ex) {
                log(Level.FINER, "Protempa consumer thread join interrupted", ex);
            }

            if (exception != null) {
                throw exception;
            }
        } catch (QueryException ex) {
            this.failed = true;
            throw ex;
        }
    }

    @Override
    public void close() throws CloseException {
        try {
            // Might be null if init() fails.
            if (this.resultsHandler != null) {
                if (!this.failed) {
                    this.resultsHandler.finish();
                }
                this.resultsHandler.close();
                this.resultsHandler = null;
            }
        } catch (QueryResultsHandlerProcessingException
                | QueryResultsHandlerCloseException ex) {
            throw new CloseException(ex);
        } finally {
            if (this.resultsHandler != null) {
                try {
                    this.resultsHandler.close();
                } catch (QueryResultsHandlerCloseException ignore) {

                }
            }
        }
    }

    boolean isLoggable(Level level) {
        return LOGGER.isLoggable(level);
    }

    void log(Level level, String msg, Object[] params) {
        if (isLoggable(level)) {
            LOGGER.log(level, this.logMessageFormat.format(new Object[]{msg}), params);
        }
    }

    void log(Level level, String msg, Object param) {
        if (isLoggable(level)) {
            LOGGER.log(level, this.logMessageFormat.format(new Object[]{msg}), param);
        }
    }

    void log(Level level, String msg, Throwable throwable) {
        if (isLoggable(level)) {
            LOGGER.log(level, this.logMessageFormat.format(new Object[]{msg}), throwable);
        }
    }

    void log(Level level, String msg) {
        if (isLoggable(level)) {
            LOGGER.log(level, this.logMessageFormat.format(new Object[]{msg}));
        }
    }

    private void extractPropositionDefinitionCache() throws KnowledgeSourceReadException {
        this.propositionDefinitionCache = new PropositionDefinitionCache(this.ks.collectPropDefDescendantsUsingAllNarrower(false, this.propIds.toArray(new String[this.propIds.size()])));

        if (isLoggable(Level.FINE)) {
            Set<String> allNarrowerDescendantsPropIds = new HashSet<>();
            for (PropositionDefinition pd : this.propositionDefinitionCache.getAll()) {
                allNarrowerDescendantsPropIds.add(pd.getId());
            }
            log(Level.FINE, "Proposition details: {0}", StringUtils.join(allNarrowerDescendantsPropIds, ", "));
        }
    }

    private void startQueryResultsHandler() throws QueryResultsHandlerProcessingException {
        log(Level.FINE, "Calling query results handler start...");
        this.resultsHandler.start(this.propositionDefinitionCache);
        log(Level.FINE, "Query results handler started");
        log(Level.FINE, "Query results handler waiting for results...");
    }

    private void createQueryResultsHandler() throws QueryResultsHandlerValidationFailedException, QueryResultsHandlerInitException {
        log(Level.FINE, "Initializing query results handler...");
        this.resultsHandler = this.destination.getQueryResultsHandler(this.query, this.abstractionFinder.getDataSource(), this.ks, this.abstractionFinder.getEventListeners());
        log(Level.FINE, "Got query results handler {0}", this.resultsHandler.getId());
        log(Level.FINE, "Validating query results handler");
        this.resultsHandler.validate();
        log(Level.FINE, "Query results handler validated successfully");
    }

}
