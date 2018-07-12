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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.log.Logging;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.dest.Destination;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerCloseException;
import org.protempa.dest.QueryResultsHandlerInitException;
import org.protempa.dest.QueryResultsHandlerProcessingException;
import org.protempa.dest.QueryResultsHandlerValidationFailedException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
final class Executor implements AutoCloseable {

    private static final Logger LOGGER = ProtempaUtil.logger();
    private final Set<String> keyIds;
    private final Set<String> propIds;
    private final Filter filters;
    private final PropositionDefinition[] propDefs;
    private final KnowledgeSource ks;
    private final Query query;
    private DerivationsBuilder derivationsBuilder;
    private Collection<PropositionDefinition> propositionDefinitionCache;
    private final AbstractionFinder abstractionFinder;
    private ExecutionStrategy executionStrategy = null;
    private final ExecutorCounter counter = new ExecutorCounter();
    private final List<QueryException> exceptions;
    private final Destination destination;
    private QueryResultsHandler resultsHandler;
    private boolean failed;
    private final MessageFormat logMessageFormat;
    private Thread handleQueryResultThread;
    private boolean canceled;

    Executor(Query query, Destination resultsHandlerFactory, AbstractionFinder abstractionFinder) throws QueryException {
        this.abstractionFinder = abstractionFinder;
        assert query != null : "query cannot be null";
        assert resultsHandlerFactory != null : "resultsHandlerFactory cannot be null";
        assert abstractionFinder != null : "abstractionFinder cannot be null";
        if (abstractionFinder.isClosed()) {
            throw new QueryException(query.getName(), new ProtempaAlreadyClosedException());
        }
        this.keyIds = Arrays.asSet(query.getKeyIds());
        this.propIds = Arrays.asSet(query.getPropositionIds());
        this.filters = query.getFilters();
        this.propDefs = query.getPropositionDefinitions();
        if (propDefs != null && propDefs.length > 0) {
            ks = new KnowledgeSourceImplWrapper(abstractionFinder.getKnowledgeSource(), propDefs);
        } else {
            ks = abstractionFinder.getKnowledgeSource();
        }
        this.query = query;
        this.derivationsBuilder = new DerivationsBuilder();
        this.destination = resultsHandlerFactory;
        this.exceptions = Collections.synchronizedList(new ArrayList<>());
        this.logMessageFormat = new MessageFormat("Query " + this.query.getName() + ": {0}");
    }

    void init() throws QueryException {
        try {
            createQueryResultsHandler();

            if (isLoggable(Level.FINE)) {
                log(Level.FINE, "Propositions to be queried are {0}", StringUtils.join(this.propIds, ", "));
            }
            extractPropositionDefinitionCache();

            try {
                if (hasSomethingToAbstract(query) || this.query.getDatabasePath() != null) {
                    selectExecutionStrategy();
                    initializeExecutionStrategy();
                }
            } catch (QueryValidationException ex) {
                throw new QueryException(query.getName(), ex);
            }

            startQueryResultsHandler();
        } catch (KnowledgeSourceReadException | QueryResultsHandlerValidationFailedException | QueryResultsHandlerInitException | QueryResultsHandlerProcessingException | Error | RuntimeException ex) {
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
            Thread retrieveDataThread;
            Thread doProcessThread;
            synchronized (this) {
                if (this.canceled) {
                    return;
                }
                log(Level.INFO, "Processing data");
                DataStreamingEvent doProcessPoisonPill = new DataStreamingEvent("poison", Collections.emptyList());
                QueueObject hqrPoisonPill = new QueueObject();
                BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue = new ArrayBlockingQueue<>(1000);
                BlockingQueue<QueueObject> hqrQueue = new ArrayBlockingQueue<>(1000);
                retrieveDataThread = new RetrieveDataThread(doProcessQueue, doProcessPoisonPill);
                doProcessThread = new DoProcessThread(doProcessQueue, hqrQueue, doProcessPoisonPill, hqrPoisonPill, retrieveDataThread);
                this.handleQueryResultThread = new HandleQueryResultThread(hqrQueue, hqrPoisonPill, doProcessThread);
                retrieveDataThread.start();
                doProcessThread.start();
                this.handleQueryResultThread.start();
            }

            try {
                retrieveDataThread.join();
                log(Level.INFO, "Done retrieving data");
            } catch (InterruptedException ex) {
                log(Level.FINER, "Protempa producer thread join interrupted", ex);
            }
            try {
                doProcessThread.join();
                log(Level.INFO, "Done processing data");
            } catch (InterruptedException ex) {
                log(Level.FINER, "Protempa consumer thread join interrupted", ex);
            }
            try {
                this.handleQueryResultThread.join();
                log(Level.INFO, "Done outputting results");
            } catch (InterruptedException ex) {
                log(Level.FINER, "Protempa consumer thread join interrupted", ex);
            }

            if (!exceptions.isEmpty()) {
                throw exceptions.get(0);
            }
        } catch (QueryException ex) {
            this.failed = true;
            throw ex;
        }
    }

    @Override
    public void close() throws CloseException {
        try {
            if (executionStrategy != null) {
                executionStrategy.shutdown();
            }
            // Might be null if init() fails.
            if (this.resultsHandler != null) {
                if (!this.failed) {
                    this.resultsHandler.finish();
                }
                this.resultsHandler.close();
                this.resultsHandler = null;
            }
        } catch (QueryResultsHandlerProcessingException
                | QueryResultsHandlerCloseException
                | ExecutionStrategyShutdownException ex) {
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

    int getCount() {
        return counter.getCount();
    }

    Query getQuery() {
        return query;
    }

    Set<String> getPropIds() {
        return this.propIds;
    }

    Filter getFilters() {
        return this.filters;
    }

    Set<String> getKeyIds() {
        return this.keyIds;
    }

    DerivationsBuilder getDerivationsBuilder() {
        return this.derivationsBuilder;
    }

    KnowledgeSource getKnowledgeSource() {
        return ks;
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

    void logCount(Level level, int count, String singularMsg, String pluralMsg, Object[] singularParams, Object[] pluralParams) {
        if (isLoggable(level)) {
            Logging.logCount(LOGGER, level, count, this.logMessageFormat.format(new Object[]{singularMsg}), this.logMessageFormat.format(new Object[]{pluralMsg}), singularParams, pluralParams);
        }
    }

    private class ExecutorCounter {

        private int count;

        ExecutorCounter() {
        }

        void incr() throws QueryException {
            if (++this.count % 1000 == 0) {
                logNumProcessed(this.count);
            }
        }

        int getCount() {
            return this.count;
        }

        private void logNumProcessed(int numProcessed) throws QueryException {
            if (isLoggable(Level.FINE)) {
                try {
                    String keyTypeSingDisplayName = abstractionFinder.getDataSource().getKeyTypeDisplayName();
                    String keyTypePluralDisplayName = abstractionFinder.getDataSource().getKeyTypePluralDisplayName();
                    logCount(Level.FINE, numProcessed, "Processed {0} {1}", "Processed {0} {1}", new Object[]{keyTypeSingDisplayName}, new Object[]{keyTypePluralDisplayName});
                } catch (DataSourceReadException ex) {
                    throw new QueryException(Executor.this.query.getName(), ex);
                }
            }
        }
    }

    private class RetrieveDataThread extends Thread {

        private final BlockingQueue<DataStreamingEvent<Proposition>> queue;
        private final DataStreamingEvent<Proposition> poisonPill;
        private final DataStreamingEventIterator<Proposition> itr;

        RetrieveDataThread(BlockingQueue<DataStreamingEvent<Proposition>> queue, DataStreamingEvent<Proposition> poisonPill) throws QueryException {
            super("protempa.executor.RetrieveDataThread");
            this.queue = queue;
            this.poisonPill = poisonPill;
            this.itr = newDataIterator();
        }

        @Override
        public void run() {
            log(Level.FINER, "Start retrieve data thread");
            boolean itrClosed = false;
            try {
                while (!isInterrupted() && itr.hasNext()) {
                    queue.put(itr.next());
                }
                itr.close();
                queue.put(poisonPill);
                itrClosed = true;
            } catch (DataSourceReadException ex) {
                exceptions.add(new QueryException(Executor.this.query.getName(), ex));
                try {
                    queue.put(poisonPill);
                } catch (InterruptedException ignore) {
                    log(Level.SEVERE, "Failed to send stop message to the do process thread; the query may be hung", ignore);
                }
            } catch (Error | RuntimeException ex) {
                exceptions.add(new QueryException(Executor.this.query.getName(), ex));
                try {
                    queue.put(poisonPill);
                } catch (InterruptedException ignore) {
                    log(Level.SEVERE, "Failed to send stop message to the do process thread; the query may be hung", ignore);
                }
            } catch (InterruptedException ex) { // by DoProcessThread
                log(Level.FINER, "Retrieve data thread interrupted", ex);
            } finally {
                if (!itrClosed) {
                    try {
                        itr.close();
                    } catch (DataSourceReadException ignore) {
                    }
                }
            }
            log(Level.FINER, "End retrieve data thread");
        }
    }

    private class DoProcessThread extends Thread {

        private final BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue;
        private final BlockingQueue<QueueObject> hqrQueue;
        private final QueueObject hqrPoisonPill;
        private final DataStreamingEvent<Proposition> doProcessPoisonPill;
        private final Thread producer;

        DoProcessThread(BlockingQueue<DataStreamingEvent<Proposition>> doProcessQueue, BlockingQueue<QueueObject> hqrQueue, DataStreamingEvent<Proposition> doProcessPoisonPill, QueueObject hqrPoisonPill, Thread producer) {
            super("protempa.executor.DoProcessThread");
            this.doProcessQueue = doProcessQueue;
            this.hqrQueue = hqrQueue;
            this.doProcessPoisonPill = doProcessPoisonPill;
            this.producer = producer;
            this.hqrPoisonPill = hqrPoisonPill;
        }

        @Override
        public void run() {
            log(Level.FINER, "Start do process thread");
            try {
                DataStreamingEvent<Proposition> dse;
                while (!isInterrupted() && ((dse = doProcessQueue.take()) != doProcessPoisonPill)) {
                    String keyId = dse.getKeyId();
                    try {
                        Iterator<Proposition> resultsItr;
                        List<Proposition> data = dse.getData();
                        if (executionStrategy != null) {
                            resultsItr = executionStrategy.execute(keyId, data);
                        } else {
                            resultsItr = data.iterator();
                        }
                        Map<Proposition, List<Proposition>> forwardDerivations = derivationsBuilder.toForwardDerivations();
                        Map<Proposition, List<Proposition>> backwardDerivations = derivationsBuilder.toBackwardDerivations();
                        int inputSize = data.size();
                        Map<UniqueId, Proposition> refs = org.arp.javautil.collections.Collections.newHashMap(inputSize);
                        List<Proposition> filteredPropositions = extractRequestedPropositions(resultsItr, refs, inputSize);
                        if (isLoggable(Level.FINEST)) {
                            log(Level.FINEST, "Proposition ids: {0}", propIds);
                            log(Level.FINEST, "Filtered propositions: {0}", filteredPropositions);
                            log(Level.FINEST, "Forward derivations: {0}", forwardDerivations);
                            log(Level.FINEST, "Backward derivations: {0}", backwardDerivations);
                            log(Level.FINEST, "References: {0}", refs);
                        }
                        this.hqrQueue.put(new QueueObject(keyId, filteredPropositions, forwardDerivations, backwardDerivations, refs));
                        log(Level.FINER, "Results put on query result handler queue");
                        counter.incr();
                        derivationsBuilder.reset();
                    } finally {
                        if (executionStrategy != null) {
                            executionStrategy.closeCurrentWorkingMemory();
                        }
                    }
                }
                this.hqrQueue.put(this.hqrPoisonPill);
            } catch (QueryException ex) {
                log(Level.FINER, "Do process thread threw ExecutorExecuteException", ex);
                exceptions.add(ex);
                producer.interrupt();
                try {
                    hqrQueue.put(hqrPoisonPill);
                } catch (InterruptedException ignore) {
                    log(Level.SEVERE, "Failed to stop the query results handler queue; the query may be hung", ignore);
                }
            } catch (InterruptedException ex) { // by the HQR thread
                log(Level.FINER, "Do process thread interrupted", ex);
                producer.interrupt();
            } catch (Error | RuntimeException t) {
                log(Level.SEVERE, "Do process thread threw exception; the query may be hung", t);
                throw t;
            }
            log(Level.FINER, "End do process thread");
        }

        private List<Proposition> extractRequestedPropositions(
                Iterator<Proposition> propositions,
                Map<UniqueId, Proposition> refs, int inputSize) {
            int outputSize = inputSize + Math.round(inputSize * 0.20f);
            List<Proposition> result = new ArrayList<>(outputSize);
            while (!isInterrupted() && propositions.hasNext()) {
                Proposition prop = propositions.next();
                refs.put(prop.getUniqueId(), prop);
                result.add(prop);
            }
            return result;
        }

    }

    private class HandleQueryResultThread extends Thread {

        private final BlockingQueue<QueueObject> queue;
        private final Thread producerThread;
        private final QueueObject poisonPill;

        HandleQueryResultThread(BlockingQueue<QueueObject> queue, QueueObject poisonPill, Thread producerThread) {
            super("protempa.executor.HandleQueryResultThread");
            this.queue = queue;
            this.producerThread = producerThread;
            this.poisonPill = poisonPill;
        }

        @Override
        public void run() {
            log(Level.FINER, "Start handle query results thread");
            QueueObject qo;
            try {
                while ((qo = queue.take()) != poisonPill) {
                    log(Level.FINER, "Handling some results");
                    try {
                        resultsHandler.handleQueryResult(qo.keyId, qo.propositions, qo.forwardDerivations, qo.backwardDerivations, qo.refs);
                    } catch (QueryResultsHandlerProcessingException ex) {
                        log(Level.FINER, "Handle query results threw QueryResultsHandlerProcessingException", ex);
                        exceptions.add(new QueryException(Executor.this.query.getName(), ex));
                        producerThread.interrupt();
                        break;
                    } catch (Error | RuntimeException t) {
                        log(Level.FINER, "Handle query results threw exception", t);
                        exceptions.add(new QueryException(Executor.this.query.getName(), new QueryResultsHandlerProcessingException(t)));
                        producerThread.interrupt();
                        break;
                    }
                    log(Level.FINER, "Results passed to query result handler");
                }
            } catch (InterruptedException ex) {
                log(Level.FINER, "Handle query results thread interrupted", ex);
                producerThread.interrupt();
            }
            log(Level.FINER, "End handle query results thread");
        }

    };

    private DataStreamingEventIterator<Proposition> newDataIterator() throws QueryException {
        log(Level.INFO, "Retrieving data");
        Set<String> inDataSourcePropIds = new HashSet<>();
        for (PropositionDefinition pd : this.propositionDefinitionCache) {
            if (pd.getInDataSource()) {
                inDataSourcePropIds.add(pd.getId());
            }
        }
        if (isLoggable(Level.FINER)) {
            log(Level.FINER, "Asking data source for {0}", StringUtils.join(inDataSourcePropIds, ", "));
        }
        DataStreamingEventIterator<Proposition> itr;
        try {
            itr = this.abstractionFinder.getDataSource().readPropositions(this.keyIds, inDataSourcePropIds, this.filters, this.resultsHandler);
        } catch (DataSourceReadException ex) {
            throw new QueryException(this.query.getName(), ex);
        }
        return itr;
    }

    private boolean hasSomethingToAbstract(Query query) throws QueryValidationException {
        try {
            KnowledgeSource ks = this.abstractionFinder.getKnowledgeSource();
            if (!ks.readAbstractionDefinitions(query.getPropositionIds()).isEmpty()
                    || !ks.readContextDefinitions(query.getPropositionIds()).isEmpty()) {
                return true;
            }
            for (PropositionDefinition propDef : query.getPropositionDefinitions()) {
                if (propDef instanceof AbstractionDefinition || propDef instanceof ContextDefinition) {
                    return true;
                }
            }
            return false;
        } catch (KnowledgeSourceReadException ex) {
            throw new QueryValidationException("Invalid proposition id(s) " + StringUtils.join(query.getPropositionIds(), ", "), ex);
        }
    }

    private void selectExecutionStrategy() {
        if (this.query.getDatabasePath() != null) {
            log(Level.FINER, "Chosen stateful execution strategy");
            this.executionStrategy = new StatefulExecutionStrategy(
                    this.abstractionFinder.getAlgorithmSource(),
                    this.query);
        } else {
            log(Level.FINER, "Chosen stateless execution strategy");
            this.executionStrategy = new StatelessExecutionStrategy(
                    this.abstractionFinder.getAlgorithmSource());
        }
    }

    private void initializeExecutionStrategy() throws QueryException {
        try {
            this.executionStrategy.initialize(this.propositionDefinitionCache, this.derivationsBuilder);
        } catch (ExecutionStrategyInitializationException ex) {
            throw new QueryException(query.getName(), ex);
        }
    }

    private void extractPropositionDefinitionCache() throws KnowledgeSourceReadException {
        this.propositionDefinitionCache = this.ks.collectPropDefDescendantsUsingAllNarrower(false, this.propIds.toArray(new String[this.propIds.size()]));

        if (isLoggable(Level.FINE)) {
            Set<String> allNarrowerDescendantsPropIds = new HashSet<>();
            for (PropositionDefinition pd : this.propositionDefinitionCache) {
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
        this.resultsHandler = this.destination.getQueryResultsHandler(getQuery(), this.abstractionFinder.getDataSource(), getKnowledgeSource(), this.abstractionFinder.getEventListeners());
        log(Level.FINE, "Got query results handler {0}", this.resultsHandler.getId());
        log(Level.FINE, "Validating query results handler");
        this.resultsHandler.validate();
        log(Level.FINE, "Query results handler validated successfully");
    }

}
