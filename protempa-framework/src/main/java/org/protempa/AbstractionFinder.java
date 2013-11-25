/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa;

import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Iterators;
import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.log.Logging;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.datastore.WorkingMemoryStoreCreator;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.And;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.QueryBuilder;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerCloseException;
import org.protempa.query.handler.QueryResultsHandlerInitException;
import org.protempa.query.handler.QueryResultsHandlerProcessingException;
import org.protempa.query.handler.QueryResultsHandlerValidationFailedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that actually does the abstraction finding.
 *
 * @author Andrew Post
 */
final class AbstractionFinder {

    private final Map<String, StatefulSession> workingMemoryCache;
    private final DataSource dataSource;
    private final KnowledgeSource knowledgeSource;
    private final TermSource termSource;
    private final AlgorithmSource algorithmSource;
    // private final Map<String, List<String>> termToPropDefMap;
    private boolean clearNeeded;
    private boolean closed;

    AbstractionFinder(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource, TermSource termSource,
            boolean cacheFoundAbstractParameters)
            throws KnowledgeSourceReadException {
        assert dataSource != null : "dataSource cannot be null";
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        assert algorithmSource != null : "algorithmSource cannot be null";
        assert termSource != null : "termSource cannot be null";

        this.dataSource = dataSource;
        this.knowledgeSource = knowledgeSource;
        this.termSource = termSource;
        this.algorithmSource = algorithmSource;

        this.dataSource.addSourceListener(
                new SourceListener<DataSourceUpdatedEvent>() {
            @Override
            public void sourceUpdated(DataSourceUpdatedEvent event) {
            }

            @Override
            public void closedUnexpectedly(
                    SourceClosedUnexpectedlyEvent e) {
                throw new UnsupportedOperationException(
                        "Not supported yet.");
            }
        });

        this.knowledgeSource.addSourceListener(
                new SourceListener<KnowledgeSourceUpdatedEvent>() {
            @Override
            public void sourceUpdated(KnowledgeSourceUpdatedEvent event) {
            }

            @Override
            public void closedUnexpectedly(
                    SourceClosedUnexpectedlyEvent e) {
                throw new UnsupportedOperationException(
                        "Not supported yet.");
            }
        });

        this.termSource.addSourceListener(
                new SourceListener<TermSourceUpdatedEvent>() {
            @Override
            public void sourceUpdated(TermSourceUpdatedEvent event) {
            }

            @Override
            public void closedUnexpectedly(
                    SourceClosedUnexpectedlyEvent e) {
                throw new UnsupportedOperationException(
                        "Not supported yet");
            }
        });

        this.algorithmSource.addSourceListener(
                new SourceListener<AlgorithmSourceUpdatedEvent>() {
            @Override
            public void sourceUpdated(AlgorithmSourceUpdatedEvent event) {
            }

            @Override
            public void closedUnexpectedly(
                    SourceClosedUnexpectedlyEvent e) {
                throw new UnsupportedOperationException(
                        "Not supported yet.");
            }
        });

        if (cacheFoundAbstractParameters) {
            this.workingMemoryCache = new HashMap<>();
        } else {
            this.workingMemoryCache = null;
        }
    }

    DataSource getDataSource() {
        return this.dataSource;
    }

    KnowledgeSource getKnowledgeSource() {
        return this.knowledgeSource;
    }

    AlgorithmSource getAlgorithmSource() {
        return this.algorithmSource;
    }

    TermSource getTermSource() {
        return this.termSource;
    }

    Set<String> getKnownKeys() {
        if (workingMemoryCache != null) {
            return Collections.unmodifiableSet(workingMemoryCache.keySet());
        } else {
            return Collections.emptySet();
        }
    }

    private static enum ExecutorStrategy {

        STATELESS, STATEFUL
    }

    private abstract class Executor {

        private final Logger logger;
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
            private ExecutorCounter counter;

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

            abstract void doProcess(String keyId, Set<String> propIds)
                    throws FinderException;
        }

        abstract class DataStreamingEventProcessor {

            private final DataStreamingEventIterator<Proposition> itr;
            private ExecutorCounter counter;

            public DataStreamingEventProcessor(
                    DataStreamingEventIterator<Proposition> itr) {
                assert itr != null : "itr cannot be null";
                this.itr = itr;
                this.counter = new ExecutorCounter();
            }

            final void process() throws DataSourceReadException,
                    FinderException {
                String queryId = query.getId();
                log(Level.INFO, "Processing data for query {0}", queryId);
                try {
                    for (; this.itr.hasNext();) {
                        doProcess(this.itr.next(), propIds);
                        this.counter.incr();
                        derivationsBuilder.reset();
                    }
                    log(Level.INFO, "Done processing data for query {0}",
                            queryId);
                } finally {
                    this.itr.close();
                    log(Level.INFO, "Done retrieving data for query {0}",
                            queryId);
                }
            }

            final int getCount() {
                return this.counter.getCount();
            }

            abstract void doProcess(DataStreamingEvent next,
                    Set<String> propIds) throws FinderException;
        }

        Executor(Query query, QuerySession querySession)
                throws FinderException {
            this(query, querySession, null);
        }

        Executor(Query query, QuerySession querySession,
                ExecutorStrategy strategy)
                throws FinderException {
            assert query != null : "query cannot be null";

            this.logger = ProtempaUtil.logger();

            if (AbstractionFinder.this.closed) {
                throw new FinderException(query.getId(),
                        new ProtempaAlreadyClosedException());
            }

            this.keyIds = Arrays.asSet(query.getKeyIds());
            this.propIds = Arrays.asSet(query.getPropositionIds());
            this.termIds = Arrays.asSet(query.getTermIds());
            this.filters = query.getFilters();
            this.propDefs = query.getPropositionDefinitions();
            if (propDefs != null && propDefs.length > 0) {
                ks = new KnowledgeSourceImplWrapper(
                        knowledgeSource, propDefs);
            } else {
                ks = knowledgeSource;
            }
            this.query = query;
            this.qs = querySession;
            this.derivationsBuilder = new DerivationsBuilder();
            this.strategy = strategy;
        }

        protected final Query getQuery() {
            return query;
        }

        protected final Set<String> getPropIds() {
            return propIds;
        }

        protected final void addAllPropIds(String[] propIds) {
            org.arp.javautil.arrays.Arrays.addAll(this.propIds, propIds);
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
            return this.logger.isLoggable(level);
        }

        final void log(Level level, String msg, Object[] params) {
            this.logger.log(level, msg, params);
        }

        final void log(Level level, String msg, Object param) {
            this.logger.log(level, msg, param);
        }

        final void log(Level level, String msg) {
            this.logger.log(level, msg);
        }

        final void logCount(Level level, int count, String singularMsg,
                String pluralMsg, Object[] singularParams,
                Object[] pluralParams) {
            Logging.logCount(this.logger, level, count, singularMsg, pluralMsg,
                    singularParams, pluralParams);
        }

        void execute() throws FinderException {
            if (isLoggable(Level.FINE)) {
                log(Level.FINE,
                        "Propositions to be queried for query {0} are {1}",
                        new Object[]{
                    query.getId(),
                    StringUtils.join(this.propIds, ", ")
                });
            }

            ExecutionStrategy executionStrategy;
            if (strategy == null) {
                executionStrategy = null;
            } else {
                switch (strategy) {
                    case STATELESS:
                        executionStrategy = newStatelessStrategy();
                        break;
                    case STATEFUL:
                        executionStrategy = newStatefulStrategy();
                        break;
                    default:
                        throw new AssertionError("Invalid execution strategy: "
                                + strategy);
                }
            }

            try {
                doExecute(this.keyIds, this.derivationsBuilder,
                        executionStrategy);
            } catch (ProtempaException e) {
                throw new FinderException(query.getId(), e);
            } finally {
                if (executionStrategy != null) {
                    executionStrategy.cleanup();
                }
            }
        }

        final DataStreamingEventIterator<Proposition> newDataIterator()
                throws KnowledgeSourceReadException, DataSourceReadException {
            log(Level.INFO, "Retrieving data for query {0}", query.getId());
            Set<String> inDataSourcePropIds = getKnowledgeSource()
                    .inDataSourcePropositionIds(
                    this.propIds.toArray(new String[this.propIds.size()]));
            if (isLoggable(Level.FINER)) {
                log(Level.FINER, "Asking data source for {0} for query {1}",
                        new Object[]{
                    StringUtils.join(inDataSourcePropIds, ", "),
                    query.getId()
                });
            }
            DataStreamingEventIterator<Proposition> itr =
                    getDataSource().readPropositions(this.keyIds,
                    inDataSourcePropIds, this.filters, getQuerySession());
            return itr;
        }

        final Iterator<String> keysToProcess(Set<String> keyIds,
                DataStore<String, ?> propStore) {
            Set<String> result;
            if (keyIds != null && !keyIds.isEmpty()) {
                result = keyIds;
            } else {
                result = propStore.keySet();
            }
            return result.iterator();
        }

        final void logNumProcessed(int numProcessed)
                throws DataSourceReadException {
            if (isLoggable(Level.FINE)) {
                String keyTypeSingDisplayName =
                        getDataSource().getKeyTypeDisplayName();
                String keyTypePluralDisplayName =
                        getDataSource().getKeyTypePluralDisplayName();
                String queryId = query.getId();
                logCount(Level.FINE, numProcessed,
                        "Processed {0} {1} for query {2}",
                        "Processed {0} {1} for query {2}",
                        new Object[]{keyTypeSingDisplayName, queryId},
                        new Object[]{keyTypePluralDisplayName, queryId});
            }
        }

        protected abstract void doExecute(Set<String> keyIds,
                final DerivationsBuilder derivationsBuilder,
                final ExecutionStrategy strategy) throws ProtempaException;

        private StatelessExecutionStrategy newStatelessStrategy()
                throws FinderException {
            StatelessExecutionStrategy result =
                    new StatelessExecutionStrategy(
                    AbstractionFinder.this, getKnowledgeSource(),
                    getAlgorithmSource());

            createRuleBase(result);
            result.initialize();
            return result;
        }

        private StatefulExecutionStrategy newStatefulStrategy()
                throws FinderException {
            StatefulExecutionStrategy result =
                    new StatefulExecutionStrategy(getKnowledgeSource(),
                    getAlgorithmSource());

            createRuleBase(result);
            result.initialize();
            return result;
        }

        private void createRuleBase(ExecutionStrategy result) throws FinderException {
            log(Level.FINEST, "Initializing rule base for query {0}",
                    query.getId());
            result.createRuleBase(this.propIds, derivationsBuilder, qs);
            AbstractionFinder.this.clearNeeded = true;
            log(Level.FINEST, "Rule base initialized for query {0}",
                    query.getId());
        }
    }

    private abstract class ExecutorWithResultsHandler extends Executor {

        private final QueryResultsHandler resultsHandler;

        public ExecutorWithResultsHandler(Query query,
                QueryResultsHandler resultsHandler, QuerySession querySession,
                ExecutorStrategy strategy)
                throws FinderException {
            super(query, querySession, strategy);
            assert resultsHandler != null : "resultsHandler cannot be null";
            this.resultsHandler = resultsHandler;
        }

        QueryResultsHandler getResultsHandler() {
            return resultsHandler;
        }

        @Override
        void execute() throws FinderException {
            String queryId = getQuery().getId();
            boolean resultsHandlerClosed = false;
            try {
                log(Level.FINE,
                        "Initializing query results handler for query {0}",
                        queryId);
                resultsHandler.init(getKnowledgeSource(), getQuery());
                log(Level.FINE,
                        "Done initalizing query results handler for query {0}",
                        queryId);
                log(Level.FINE, "Validating query results handler for query {0}",
                        queryId);
                resultsHandler.validate();
                log(Level.FINE,
                        "Query results handler validated successfully for query {0}",
                        queryId);

                addAllPropIds(getResultsHandler().getPropositionIdsNeeded());

                resultsHandler.start();

                super.execute();

                resultsHandler.finish();
                resultsHandlerClosed = true;
                resultsHandler.close();
            } catch (QueryResultsHandlerProcessingException | QueryResultsHandlerValidationFailedException | KnowledgeSourceReadException | QueryResultsHandlerInitException | QueryResultsHandlerCloseException ex) {
                throw new FinderException(queryId, ex);
            } finally {
                if (!resultsHandlerClosed) {
                    try {
                       resultsHandler.close();
                    } catch (QueryResultsHandlerCloseException ex) {
                    }
                }
            }
        }

        final void processResults(Iterator<Proposition> propositions,
                DerivationsBuilder derivationsBuilder, String keyId)
                throws FinderException {
            if (derivationsBuilder == null) {
                derivationsBuilder = getDerivationsBuilder();
            }
            Map<Proposition, List<Proposition>> forwardDerivations =
                    derivationsBuilder.toForwardDerivations();
            Map<Proposition, List<Proposition>> backwardDerivations =
                    derivationsBuilder.toBackwardDerivations();
            Set<String> propositionIds = getPropIds();
            QuerySession qs = getQuerySession();
            if (qs.isCachingEnabled()) {
                List<Proposition> props = Iterators.asList(propositions);
                addToCache(qs, Collections.unmodifiableList(props),
                        Collections.unmodifiableMap(forwardDerivations),
                        Collections.unmodifiableMap(backwardDerivations));
                propositions = props.iterator();
            }

            Map<UniqueId, Proposition> refs =
                    new HashMap<>();
            if (isLoggable(Level.FINER)) {
                log(Level.FINER, "References for query {0}: {1}",
                        new Object[]{getQuery().getId(), refs});
            }
            List<Proposition> filteredPropositions = // a newly created list
                    extractRequestedPropositions(propositions,
                    propositionIds, refs);
            if (isLoggable(Level.FINER)) {
                String queryId = getQuery().getId();
                log(Level.FINER, "Proposition ids for query {0}: {1}",
                        new Object[]{queryId, propositionIds});
                log(Level.FINER, "Filtered propositions for query {0}: {1}",
                        new Object[]{queryId, filteredPropositions});
                log(Level.FINER, "Forward derivations for query {0}: {1}",
                        new Object[]{queryId, forwardDerivations});
                log(Level.FINER, "Backward derivations for query {0}: {1}",
                        new Object[]{queryId, backwardDerivations});
            }
            try {
                this.resultsHandler.handleQueryResult(keyId,
                        filteredPropositions,
                        forwardDerivations, backwardDerivations, refs);
            } catch (QueryResultsHandlerProcessingException ex) {
                throw new FinderException(getQuery().getId(), ex);
            }
        }

        final void processResults(
                Iterator<Proposition> propositions,
                String keyId) throws FinderException {
            processResults(propositions, null, keyId);
        }
    }

    void doFind(Query query, QueryResultsHandler resultsHandler,
            QuerySession qs)
            throws FinderException {
        assert resultsHandler != null : "resultsHandler cannot be null";
        ExecutorStrategy strategy;
        if (workingMemoryCache != null) {
            strategy = ExecutorStrategy.STATEFUL;
        } else {
            strategy = ExecutorStrategy.STATELESS;
        }
        new ExecutorWithResultsHandler(query, resultsHandler, qs, strategy) {
            @Override
            protected void doExecute(Set<String> keyIds,
                    final DerivationsBuilder derivationsBuilder,
                    final ExecutionStrategy strategy)
                    throws ProtempaException {
                // List<String> termPropIds =
                // getPropIdsFromTerms(explodeTerms(termIds));
                // List<String> allPropIds = new ArrayList<String>();
                // allPropIds.addAll(propIds);
                // allPropIds.addAll(termPropIds);

                new DataStreamingEventProcessor(newDataIterator()) {
                    @Override
                    void doProcess(DataStreamingEvent next,
                            Set<String> propositionIds)
                            throws FinderException {
                        String keyId = next.getKeyId();
                        Iterator<Proposition> resultsItr = strategy.execute(
                                keyId, propositionIds, next.getData(),
                                null);
                        processResults(resultsItr, keyId);
                    }
                }.process();
            }
        }.execute();
    }

    Query buildQuery(QueryBuilder queryBuilder) throws QueryBuildException {
        return queryBuilder.build(this.knowledgeSource, this.algorithmSource);
    }

    /**
     * Clears the working memory cache. Only needs to be called in caching mode.
     */
    public void clear() {
        if (clearNeeded) {
            clearWorkingMemoryCache();
            clearNeeded = false;
        }
    }

    public void close() {
        clear();
        this.closed = true;
    }

    private static void addToCache(QuerySession qs,
            List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations) {
        qs.addPropositionsToCache(propositions);
        for (Map.Entry<Proposition, List<Proposition>> me
                : forwardDerivations.entrySet()) {
            qs.addDerivationsToCache(me.getKey(), me.getValue());
        }
        for (Map.Entry<Proposition, List<Proposition>> me
                : backwardDerivations.entrySet()) {
            qs.addDerivationsToCache(me.getKey(), me.getValue());
        }
    }

    private static List<Proposition> extractRequestedPropositions(
            Iterator<Proposition> propositions, Set<String> propositionIds,
            Map<UniqueId, Proposition> refs) {
        List<Proposition> result = new ArrayList<>();
        while (propositions.hasNext()) {
            Proposition prop = propositions.next();
            refs.put(prop.getUniqueId(), prop);
            if (propositionIds.contains(prop.getId())) {
                result.add(prop);
            }
        }
        return result;
    }

//    private List<String> getPropIdsFromTerms(
//            Set<And<TermSubsumption>> termSubsumptionClauses)
//            throws KnowledgeSourceReadException {
//        List<String> result = new ArrayList<String>();
//
//        for (And<TermSubsumption> subsumpClause : termSubsumptionClauses) {
//            result.addAll(this.knowledgeSource
//                    .getPropositionDefinitionsByTerm(subsumpClause));
//        }
//
//        return result;
//    }
//    private Set<And<TermSubsumption>> explodeTerms(Set<And<String>> termClauses)
//            throws TermSourceReadException {
//        Set<And<TermSubsumption>> result = new HashSet<And<TermSubsumption>>();
//
//        for (And<String> termClause : termClauses) {
//            And<TermSubsumption> subsumpClause = new And<TermSubsumption>();
//            List<TermSubsumption> tss = new ArrayList<TermSubsumption>();
//            for (String termId : termClause.getAnded()) {
//                tss.add(TermSubsumption.fromTerms(this.termSource
//                        .getTermSubsumption(termId)));
//            }
//            subsumpClause.setAnded(tss);
//            result.add(subsumpClause);
//        }
//
//        return result;
//    }
    private void clearWorkingMemoryCache() {
        if (workingMemoryCache != null) {
            for (Iterator<StatefulSession> itr =
                    workingMemoryCache.values().iterator(); itr.hasNext();) {
                try {
                    itr.next().dispose();
                    itr.remove();
                } catch (Exception e) {
                    ProtempaUtil.logger().log(Level.SEVERE,
                            "Could not dispose stateful rule session", e);
                }
            }
        }
    }

    void retrieveAndStoreData(Query query, QuerySession qs,
            final String persistentStoreEnvironment) throws FinderException {
        assert query != null : "query cannot be null";
        new Executor(query, qs) {
            @Override
            protected void doExecute(Set<String> keyIds,
                    final DerivationsBuilder derivationsBuilder,
                    final ExecutionStrategy executionStrategy)
                    throws ProtempaException {
                final DataStore<String, List<Proposition>> store =
                        new PropositionStoreCreator(
                        persistentStoreEnvironment).getPersistentStore();
                try {
                    DataStreamingEventProcessor processor =
                            new DataStreamingEventProcessor(newDataIterator()) {
                        @Override
                        void doProcess(DataStreamingEvent next,
                                Set<String> propIds)
                                throws FinderException {
                            store.put(next.getKeyId(), next.getData());
                        }
                    };
                    processor.process();
                    if (isLoggable(Level.INFO)) {
                        log(Level.INFO,
                                "Wrote {0} records into store {1} for query {2}",
                                new Object[]{processor.getCount(),
                            persistentStoreEnvironment,
                            getQuery().getId()});
                    }
                } finally {
                    store.shutdown();
                }
            }
        }.execute();
    }

    void processStoredResults(Query query, QuerySession qs,
            final String propositionStoreEnvironment,
            final String workingMemoryStoreEnvironment) throws FinderException {
        assert query != null : "query cannot be null";
        new Executor(query, qs, ExecutorStrategy.STATEFUL) {
            @Override
            protected void doExecute(Set<String> keyIds,
                    final DerivationsBuilder derivationsBuilder,
                    final ExecutionStrategy strategy)
                    throws ProtempaException {
                final DataStore<String, List<Proposition>> propStore =
                        new PropositionStoreCreator(
                        propositionStoreEnvironment).getPersistentStore();
                if (isLoggable(Level.INFO)) {
                    log(Level.INFO,
                            "Found {0} records in store {1} for query {2}",
                            new Object[]{
                        propStore.size(),
                        propositionStoreEnvironment,
                        getQuery().getId()});
                }
                final DataStore<String, WorkingMemory> wmStore =
                        new WorkingMemoryStoreCreator(null,
                        workingMemoryStoreEnvironment).getPersistentStore();
                final DataStore<String, DerivationsBuilder> dbStore =
                        new DerivationsBuilderStoreCreator(
                        workingMemoryStoreEnvironment).getPersistentStore();
                try {
                    new KeyIdProcessor(keysToProcess(keyIds, propStore)) {
                        @Override
                        void doProcess(String keyId, Set<String> propIds)
                                throws FinderException {
                            // the important part here is that the working memory produced
                            // by the rules engine is being persisted by
                            // StatefulExecutionStrategy.execute()
                            if (propStore.containsKey(keyId)) {
                                strategy.execute(keyId, propIds,
                                        propStore.get(keyId), wmStore);
                                dbStore.put(keyId, derivationsBuilder);
                            }
                        }
                    }.process();
                } finally {
                    propStore.shutdown();
                    wmStore.shutdown();
                    dbStore.shutdown();
                }
            }
        }.execute();
    }

    void outputStoredResults(Query query,
            QueryResultsHandler resultsHandler, QuerySession qs,
            final String workingMemoryStoreEnvironment) throws FinderException {
        assert query != null : "query cannot be null";
        new ExecutorWithResultsHandler(query, resultsHandler, qs,
                ExecutorStrategy.STATEFUL) {
            @Override
            protected void doExecute(Set<String> keyIds,
                    final DerivationsBuilder ignored,
                    final ExecutionStrategy strategy)
                    throws ProtempaException {
                final DataStore<String, DerivationsBuilder> dbStore =
                        new DerivationsBuilderStoreCreator(
                        workingMemoryStoreEnvironment).getPersistentStore();
                DataStore<String, WorkingMemory> wmStore = null;
                try {
                    wmStore = new WorkingMemoryStoreCreator(
                            strategy.getRuleBase(),
                            workingMemoryStoreEnvironment)
                            .getPersistentStore();

                    final DataStore<String, WorkingMemory> fwmStore = wmStore;

                    new KeyIdProcessor(keysToProcess(keyIds, wmStore)) {
                        @Override
                        void doProcess(String keyId, Set<String> propIds)
                                throws FinderException {
                            if (isLoggable(Level.FINEST)) {
                                log(Level.FINEST,
                                        "Determining output for key {0} for query {1}",
                                        new Object[]{keyId, getQuery().getId()});
                            }
                            if (fwmStore.containsKey(keyId)) {
                                WorkingMemory wm = fwmStore.get(keyId);
                                DerivationsBuilder derivationsBuilder = dbStore.get(keyId);

                                @SuppressWarnings("unchecked")
                                Iterator<Proposition> propositions =
                                        wm.iterateObjects();
                                processResults(propositions,
                                        derivationsBuilder, keyId);
                            }
                        }
                    }.process();
                } finally {
                    dbStore.shutdown();
                    wmStore.shutdown();
                }
            }
        }.execute();
    }

    void processAndOutputStoredResults(Query query,
            QueryResultsHandler resultsHandler,
            QuerySession qs, final String propositionStoreEnvironment)
            throws FinderException {
        new ExecutorWithResultsHandler(query, resultsHandler, qs,
                ExecutorStrategy.STATELESS) {
            @Override
            protected void doExecute(Set<String> keyIds,
                    final DerivationsBuilder derivationsBuilder,
                    final ExecutionStrategy strategy)
                    throws ProtempaException {
                final DataStore<String, List<Proposition>> propStore =
                        new PropositionStoreCreator(
                        propositionStoreEnvironment).getPersistentStore();
                try {
                    new KeyIdProcessor(keysToProcess(keyIds, propStore)) {
                        @Override
                        void doProcess(String keyId, Set<String> propIds)
                                throws FinderException {
                            if (propStore.containsKey(keyId)) {
                                Iterator<Proposition> propositions =
                                        strategy.execute(keyId, propIds,
                                        propStore.get(keyId), null);
                                processResults(propositions, keyId);
                                derivationsBuilder.reset();
                            }
                        }
                    }.process();
                } finally {
                    propStore.shutdown();
                }
            }
        }.execute();
    }
}
