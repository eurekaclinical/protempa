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
import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.log.Logging;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.proposition.Proposition;
import org.protempa.query.And;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
abstract class Executor implements AutoCloseable {

    private static final Logger LOGGER = ProtempaUtil.logger();
    private final Set<String> keyIds;
    private final Set<String> propIds;
    private final Set<And<String>> termIds;
    private final Filter filters;
    private final PropositionDefinition[] propDefs;
    private final KnowledgeSource ks;
    private final Query query;
    private final QuerySession qs;
    private DerivationsBuilder derivationsBuilder;
    private final ExecutorStrategy strategy;
    private Collection<PropositionDefinition> allNarrowerDescendants;
    private final AbstractionFinder abstractionFinder;

    Executor(Query query, QuerySession querySession, AbstractionFinder abstractionFinder) throws FinderException {
        this(query, querySession, null, abstractionFinder);
    }

    Executor(Query query, QuerySession querySession, ExecutorStrategy strategy, AbstractionFinder abstractionFinder) throws FinderException {
        this.abstractionFinder = abstractionFinder;
        assert query != null : "query cannot be null";
        if (abstractionFinder.isClosed()) {
            throw new FinderException(query.getId(), new ProtempaAlreadyClosedException());
        }
        this.keyIds = Arrays.asSet(query.getKeyIds());
        this.propIds = Arrays.asSet(query.getPropositionIds());
        this.termIds = Arrays.asSet(query.getTermIds());
        this.filters = query.getFilters();
        this.propDefs = query.getPropositionDefinitions();
        if (propDefs != null && propDefs.length > 0) {
            ks = new KnowledgeSourceImplWrapper(abstractionFinder.getKnowledgeSource(), propDefs);
        } else {
            ks = abstractionFinder.getKnowledgeSource();
        }
        this.query = query;
        this.qs = querySession;
        this.derivationsBuilder = new DerivationsBuilder();
        this.strategy = strategy;
    }

    private class ExecutorCounter {

        private int count;

        ExecutorCounter() {
        }

        void incr() throws DataSourceReadException {
            if (++this.count % 1000 == 0) {
                logNumProcessed(this.count);
            }
        }

        int getCount() {
            return this.count;
        }
    }

    abstract class KeyIdProcessor {

        private final Iterator<String> itr;
        private final ExecutorCounter counter;

        public KeyIdProcessor(Iterator<String> itr) {
            assert itr != null : "itr cannot be null";
            this.itr = itr;
            this.counter = new ExecutorCounter();
        }

        void process() throws DataSourceReadException, FinderException {
            for (; this.itr.hasNext();) {
                doProcess(this.itr.next(), propIds);
                this.counter.incr();
                derivationsBuilder.reset();
            }
        }

        abstract void doProcess(String keyId, Set<String> propIds) throws FinderException;
    }

    abstract class DataStreamingEventProcessor {

        private final DataStreamingEventIterator<Proposition> itr;
        private final ExecutorCounter counter;
        private final List<FinderException> finderExceptions;
        private final List<DataSourceReadException> dataSourceReadExceptions;

        public DataStreamingEventProcessor(DataStreamingEventIterator<Proposition> itr) {
            assert itr != null : "itr cannot be null";
            this.itr = itr;
            this.counter = new ExecutorCounter();
            this.finderExceptions = new ArrayList<>();
            this.dataSourceReadExceptions = new ArrayList<>();
        }

        final void process() throws DataSourceReadException, FinderException {
            final String queryId = query.getId();
            log(Level.INFO, "Processing data for query {0}", queryId);
            final DataStreamingEvent poisonPill = new DataStreamingEvent("poison", Collections.emptyList());
            final BlockingQueue<DataStreamingEvent> queue = new ArrayBlockingQueue<>(1000);
            final Thread producer = new Thread() {

                @Override
                public void run() {
                    boolean itrClosed = false;
                    try {
                        while (!isInterrupted() && itr.hasNext()) {
                            queue.put(itr.next());
                        }
                        itr.close();
                        queue.put(poisonPill);
                        itrClosed = true;
                    } catch (DataSourceReadException ex) {
                        dataSourceReadExceptions.add(ex);
                        try {
                            queue.put(poisonPill);
                        } catch (InterruptedException ignore) {}
                    } catch (InterruptedException ex) {
                        ProtempaUtil.logger().log(Level.FINER, "Protempa producer thread interrupted", ex);
                        try {
                            queue.put(poisonPill);
                        } catch (InterruptedException ignore) {}
                    } finally {
                        if (!itrClosed) {
                            try {
                                itr.close();
                            } catch (DataSourceReadException ignore) {
                            }
                        }
                    }
                }
            };

            final Thread consumer = new Thread() {

                @Override
                public void run() {
                    try {
                        DataStreamingEvent dse;
                        while (!isInterrupted() && ((dse = queue.take()) != poisonPill)) {
                            doProcess(dse, propIds);
                            counter.incr();
                            derivationsBuilder.reset();
                        }
                        log(Level.INFO, "Done processing data for query {0}", queryId);
                    } catch (FinderException ex) {
                        finderExceptions.add(ex);
                    } catch (InterruptedException ex) {
                        producer.interrupt();
                    } catch (DataSourceReadException ex) {
                        dataSourceReadExceptions.add(ex);
                    } finally {
                        log(Level.INFO, "Done retrieving data for query {0}", queryId);
                    }
                }

            };
            
            producer.start();
            consumer.start();
            try {
                producer.join();
            } catch (InterruptedException ex) {
                ProtempaUtil.logger().log(Level.FINER, "Protempa producer thread interrupted", ex);
            }
            try {
                consumer.join();
            } catch (InterruptedException ex) {
                ProtempaUtil.logger().log(Level.FINER, "Protempa consumer thread interrupted", ex);
            }
            
            if (!this.dataSourceReadExceptions.isEmpty()) {
                throw this.dataSourceReadExceptions.get(0);
            }
            if (!this.finderExceptions.isEmpty()) {
                throw this.finderExceptions.get(0);
            }
        }

        final int getCount() {
            return this.counter.getCount();
        }

        abstract void doProcess(DataStreamingEvent next, Set<String> propIds) throws FinderException;
    }

    protected final Query getQuery() {
        return query;
    }

    protected final Set<String> getPropIds() {
        return propIds;
    }

    protected final Collection<PropositionDefinition> getAllNarrowerDescendants() {
        return allNarrowerDescendants;
    }

    protected final Filter getFilters() {
        return this.filters;
    }

    protected final Set<String> getKeyIds() {
        return this.keyIds;
    }

    protected final void addAllPropIds(String[] propIds) {
        org.arp.javautil.arrays.Arrays.addAll(this.propIds, propIds);
        this.allNarrowerDescendants = null;
    }

    protected final DerivationsBuilder getDerivationsBuilder() {
        return this.derivationsBuilder;
    }

    final KnowledgeSource getKnowledgeSource() {
        return ks;
    }

    final QuerySession getQuerySession() {
        return qs;
    }

    final boolean isLoggable(Level level) {
        return LOGGER.isLoggable(level);
    }

    final void log(Level level, String msg, Object[] params) {
        LOGGER.log(level, msg, params);
    }

    final void log(Level level, String msg, Object param) {
        LOGGER.log(level, msg, param);
    }

    final void log(Level level, String msg) {
        LOGGER.log(level, msg);
    }

    final void logCount(Level level, int count, String singularMsg, String pluralMsg, Object[] singularParams, Object[] pluralParams) {
        Logging.logCount(LOGGER, level, count, singularMsg, pluralMsg, singularParams, pluralParams);
    }

    void init() throws FinderException {
        try {
            allNarrowerDescendants = ks.collectPropDefDescendantsUsingAllNarrower(false, propIds.toArray(new String[propIds.size()]));
            if (isLoggable(Level.FINE)) {
                Set<String> allNarrowerDescendantsPropIds = new HashSet<>();
                for (PropositionDefinition pd : this.allNarrowerDescendants) {
                    allNarrowerDescendantsPropIds.add(pd.getId());
                }
                log(Level.FINE, "Proposition details: {0}", StringUtils.join(allNarrowerDescendantsPropIds, ", "));
            }
        } catch (KnowledgeSourceReadException ex) {
            throw new FinderException(query.getId(), ex);
        }
    }

    void execute() throws FinderException {
        if (isLoggable(Level.FINE)) {
            log(Level.FINE, "Propositions to be queried for query {0} are {1}", new Object[]{query.getId(), StringUtils.join(this.propIds, ", ")});
        }
        ExecutionStrategy executionStrategy = null;
        try {
            if (strategy != null) {
                switch (strategy) {
                    case STATELESS:
                        executionStrategy = newStatelessStrategy();
                        break;
                    case STATEFUL:
                        executionStrategy = newStatefulStrategy();
                        break;
                    default:
                        throw new AssertionError("Invalid execution strategy: " + strategy);
                }
            }
            doExecute(this.keyIds, this.derivationsBuilder, executionStrategy);
        } catch (ProtempaException e) {
            throw new FinderException(query.getId(), e);
        } finally {
            if (executionStrategy != null) {
                executionStrategy.cleanup();
            }
        }
    }

    @Override
    public void close() throws FinderException {
    }

    DataStreamingEventIterator<Proposition> newDataIterator() throws KnowledgeSourceReadException, DataSourceReadException {
        log(Level.INFO, "Retrieving data for query {0}", query.getId());
        Set<String> inDataSourcePropIds = new HashSet<>();
        for (PropositionDefinition pd : allNarrowerDescendants) {
            if (pd.getInDataSource()) {
                inDataSourcePropIds.add(pd.getId());
            }
        }
        if (isLoggable(Level.FINER)) {
            log(Level.FINER, "Asking data source for {0} for query {1}", new Object[]{StringUtils.join(inDataSourcePropIds, ", "), query.getId()});
        }
        DataStreamingEventIterator<Proposition> itr = abstractionFinder.getDataSource().readPropositions(this.keyIds, inDataSourcePropIds, this.filters, getQuerySession(), null);
        return itr;
    }

    final Iterator<String> keysToProcess(Set<String> keyIds, DataStore<String, ?> propStore) {
        Set<String> result;
        if (keyIds != null && !keyIds.isEmpty()) {
            result = keyIds;
        } else {
            result = propStore.keySet();
        }
        return result.iterator();
    }

    final void logNumProcessed(int numProcessed) throws DataSourceReadException {
        if (isLoggable(Level.FINE)) {
            String keyTypeSingDisplayName = abstractionFinder.getDataSource().getKeyTypeDisplayName();
            String keyTypePluralDisplayName = abstractionFinder.getDataSource().getKeyTypePluralDisplayName();
            String queryId = query.getId();
            logCount(Level.FINE, numProcessed, "Processed {0} {1} for query {2}", "Processed {0} {1} for query {2}", new Object[]{keyTypeSingDisplayName, queryId}, new Object[]{keyTypePluralDisplayName, queryId});
        }
    }

    protected abstract void doExecute(Set<String> keyIds, final DerivationsBuilder derivationsBuilder, final ExecutionStrategy strategy) throws ProtempaException;

    private StatelessExecutionStrategy newStatelessStrategy() throws FinderException {
        StatelessExecutionStrategy result = new StatelessExecutionStrategy(abstractionFinder, abstractionFinder.getAlgorithmSource());
        createRuleBase(result);
        result.initialize();
        return result;
    }

    private StatefulExecutionStrategy newStatefulStrategy() throws FinderException {
        StatefulExecutionStrategy result = new StatefulExecutionStrategy(abstractionFinder.getAlgorithmSource());
        createRuleBase(result);
        result.initialize();
        return result;
    }

    private void createRuleBase(ExecutionStrategy result) throws FinderException {
        log(Level.FINEST, "Initializing rule base for query {0}", query.getId());
        result.createRuleBase(allNarrowerDescendants, derivationsBuilder, qs);
        abstractionFinder.clear();
        log(Level.FINEST, "Rule base initialized for query {0}", query.getId());
    }

}
