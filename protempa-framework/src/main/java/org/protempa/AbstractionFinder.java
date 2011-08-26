package org.protempa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.collections.Iterators;
import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.log.Logging;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.WorkingMemory;
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.datastore.WorkingMemoryStoreCreator;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.And;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Class that actually does the abstraction finding.
 * 
 * @author Andrew Post
 */
final class AbstractionFinder implements Module {

    private final Map<String, StatefulSession> workingMemoryCache;
    // private final Map<String, Set<String>> propIdCache;
    private final DataSource dataSource;
    private final KnowledgeSource knowledgeSource;
    private final TermSource termSource;
    private final AlgorithmSource algorithmSource;
    // private final Map<String, List<String>> termToPropDefMap;
    private boolean clearNeeded;
    // private final Map<String, Map<Set<String>, Sequence<PrimitiveParameter>>>
    // sequences;
    private boolean closed;

    AbstractionFinder(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource, TermSource termSource,
            boolean cacheFoundAbstractParameters)
            throws KnowledgeSourceReadException {
        assert dataSource != null : "dataSource cannot be null";
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        assert algorithmSource != null : "algorithmSource cannot be null";
        this.dataSource = dataSource;
        this.knowledgeSource = knowledgeSource;
        this.termSource = termSource;
        this.algorithmSource = algorithmSource;

        this.dataSource
                .addSourceListener(new SourceListener<DataSourceUpdatedEvent>() {

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

        this.knowledgeSource
                .addSourceListener(new SourceListener<KnowledgeSourceUpdatedEvent>() {

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

        this.termSource
                .addSourceListener(new SourceListener<TermSourceUpdatedEvent>() {

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

        this.algorithmSource
                .addSourceListener(new SourceListener<AlgorithmSourceUpdatedEvent>() {

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
            this.workingMemoryCache = new HashMap<String, StatefulSession>();
        } else {
            this.workingMemoryCache = null;
        }
        // if (cacheFoundAbstractParameters) {
        // this.propIdCache = new HashMap<String, Set<String>>();
        // } else {
        // this.propIdCache = null;
        // }
        // this.sequences = new HashMap<String, Map<Set<String>,
        // Sequence<PrimitiveParameter>>>();
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

    void doFind(Set<String> keyIds, Set<String> propIds,
            Set<And<String>> termIds, Filter filters,
            QueryResultsHandler resultHandler, QuerySession qs)
            throws FinderException {
        if (this.closed) {
            throw new FinderException("Protempa already closed!");
        }
        try {
            resultHandler.init(this.knowledgeSource);
            // List<String> termPropIds =
            // getPropIdsFromTerms(explodeTerms(termIds));
            // List<String> allPropIds = new ArrayList<String>();
            // allPropIds.addAll(propIds);
            // allPropIds.addAll(termPropIds);

            if (workingMemoryCache != null) {
                doFindExecute(keyIds, propIds, filters, resultHandler, qs,
                        new StatefulExecutionStrategy());
            } else {
                doFindExecute(keyIds, propIds, filters, resultHandler, qs,
                        new StatelessExecutionStrategy());
            }
            resultHandler.finish();
        } catch (ProtempaException e) {
            String msg = "Query did not complete";
            throw new FinderException(msg, e);
        }
    }

    /**
     * Clears the working memory cache. Only needs to be called in caching mode.
     */
    @Override
    public void clear() {
        if (clearNeeded) {
            clearWorkingMemoryCache();
            // this.sequences.clear();
            clearNeeded = false;
        }
    }

    @Override
    public void close() {
        clear();
        this.closed = true;
    }

    private static void addToCache(QuerySession qs,
            List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations) {
        qs.addPropositionsToCache(propositions);
        for (Map.Entry<Proposition, List<Proposition>> me : forwardDerivations
                .entrySet()) {
            qs.addDerivationsToCache(me.getKey(), me.getValue());
        }
        for (Map.Entry<Proposition, List<Proposition>> me : backwardDerivations
                .entrySet()) {
            qs.addDerivationsToCache(me.getKey(), me.getValue());
        }
    }

    private static List<Proposition> extractRequestedPropositions(
            Iterator<Proposition> propositions, Set<String> propositionIds,
            Map<UniqueId, Proposition> refs) {
        List<Proposition> result = new ArrayList<Proposition>();
        while (propositions.hasNext()) {
            Proposition prop = propositions.next();
            refs.put(prop.getUniqueId(), prop);
            if (propositionIds.contains(prop.getId())) {
                result.add(prop);
            }
        }
        return result;
    }

    private List<String> getPropIdsFromTerms(
            Set<And<TermSubsumption>> termSubsumptionClauses)
            throws KnowledgeSourceReadException {
        List<String> result = new ArrayList<String>();

        for (And<TermSubsumption> subsumpClause : termSubsumptionClauses) {
            result.addAll(this.knowledgeSource
                    .getPropositionDefinitionsByTerm(subsumpClause));
        }

        return result;
    }

    private Set<And<TermSubsumption>> explodeTerms(Set<And<String>> termClauses)
            throws TermSourceReadException {
        Set<And<TermSubsumption>> result = new HashSet<And<TermSubsumption>>();

        for (And<String> termClause : termClauses) {
            And<TermSubsumption> subsumpClause = new And<TermSubsumption>();
            List<TermSubsumption> tss = new ArrayList<TermSubsumption>();
            for (String termId : termClause.getAnded()) {
                tss.add(TermSubsumption.fromTerms(this.termSource
                        .getTermSubsumption(termId)));
            }
            subsumpClause.setAnded(tss);
            result.add(subsumpClause);
        }

        return result;
    }

    private static byte[] detachWorkingMemory(StatefulSession workingMemory)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        byte[] wmSerialized;
        try {
            oos.writeObject(workingMemory);
            wmSerialized = baos.toByteArray();
        } finally {
            oos.close();
        }
        return wmSerialized;
    }

    private static StatefulSession reattachWorkingMemory(byte[] wmSerialized,
            RuleBase ruleBase) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(wmSerialized);
        StatefulSession workingMemory;
        try {
            workingMemory = reattachWorkingMemory(bais, ruleBase);
        } finally {
            bais.close();
        }
        return workingMemory;
    }

    private static StatefulSession reattachWorkingMemory(InputStream wmIn,
            RuleBase ruleBase) throws IOException, ClassNotFoundException {
        return ruleBase.newStatefulSession(wmIn, false);
    }

    private void clearWorkingMemoryCache() {
        if (workingMemoryCache != null) {
            for (Iterator<StatefulSession> itr = workingMemoryCache.values()
                    .iterator(); itr.hasNext();) {
                try {
                    itr.next().dispose();
                    itr.remove();
                } catch (Exception e) {
                    ProtempaUtil.logger().log(Level.SEVERE,
                            "Could not dispose stateful rule session.", e);
                }
            }
        }
    }

    private interface ExecutionStrategy {

        void initialize();

        Iterator<Proposition> execute(String keyIds,
                Set<String> propositionIds, List<?> objects,
                DataStore<String, WorkingMemory> wm) throws ProtempaException;

        void cleanup();

        void createRuleBase(Set<String> propIds, DerivationsBuilder listener,
                QuerySession qs) throws ProtempaException;
    }

    private abstract class AbstractExecutionStrategy implements
            ExecutionStrategy {

        protected RuleBase ruleBase;

        @Override
        public void createRuleBase(Set<String> propIds,
                DerivationsBuilder listener, QuerySession qs)
                throws ProtempaException {
            ValidateAlgorithmCheckedVisitor visitor = new ValidateAlgorithmCheckedVisitor(
                    algorithmSource);
            JBossRuleCreator ruleCreator = new JBossRuleCreator(
                    visitor.getAlgorithms(), listener);
            List<PropositionDefinition> propDefs = new ArrayList<PropositionDefinition>(
                    propIds.size());
            for (String propId : propIds) {
                PropositionDefinition propDef = knowledgeSource
                        .readPropositionDefinition(propId);
                if (propDef != null) {
                    propDefs.add(propDef);
                } else {
                    throw new FinderException("Invalid proposition id: "
                            + propId);
                }
            }
            if (propIds != null) {
                Set<PropositionDefinition> result = new HashSet<PropositionDefinition>();
                aggregateChildren(visitor, result, propDefs);
                ruleCreator.visit(result);
            }
            this.ruleBase = new JBossRuleBaseFactory(ruleCreator,
                    createRuleBaseConfiguration(ruleCreator)).newInstance();
            clearNeeded = true;
        }

        protected RuleBaseConfiguration createRuleBaseConfiguration(
                JBossRuleCreator ruleCreator)
                throws PropositionDefinitionInstantiationException {
            RuleBaseConfiguration config = new RuleBaseConfiguration();
            config.setShadowProxy(false);
            try {
                config.setConflictResolver(new PROTEMPAConflictResolver(
                        knowledgeSource, ruleCreator
                                .getRuleToAbstractionDefinitionMap()));
            } catch (KnowledgeSourceReadException ex) {
                throw new PropositionDefinitionInstantiationException(
                        "Could not instantiate proposition definitions", ex);
            }
            config.setAssertBehaviour(AssertBehaviour.EQUALITY);
            return config;
        }

        private class ValidateAlgorithmCheckedVisitor extends
                AbstractPropositionDefinitionCheckedVisitor {

            private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
            private final AlgorithmSource algorithmSource;

            ValidateAlgorithmCheckedVisitor(AlgorithmSource algorithmSource) {
                this.algorithms = new HashMap<LowLevelAbstractionDefinition, Algorithm>();
                this.algorithmSource = algorithmSource;
            }

            Map<LowLevelAbstractionDefinition, Algorithm> getAlgorithms() {
                return this.algorithms;
            }

            @Override
            public void visit(
                    LowLevelAbstractionDefinition lowLevelAbstractionDefinition)
                    throws ProtempaException {
                String algorithmId = lowLevelAbstractionDefinition
                        .getAlgorithmId();
                Algorithm algorithm = algorithmSource
                        .readAlgorithm(algorithmId);
                if (algorithm == null && algorithmId != null) {
                    throw new NoSuchAlgorithmException(
                            "Low level abstraction definition "
                                    + lowLevelAbstractionDefinition.getId()
                                    + " wants the algorithm " + algorithmId
                                    + ", but no such algorithm is available.");
                }
                this.algorithms.put(lowLevelAbstractionDefinition, algorithm);

            }

            @Override
            public void visit(EventDefinition eventDefinition)
                    throws ProtempaException {
            }

            @Override
            public void visit(
                    HighLevelAbstractionDefinition highLevelAbstractionDefinition)
                    throws ProtempaException {
            }

            @Override
            public void visit(
                    PrimitiveParameterDefinition primitiveParameterDefinition)
                    throws ProtempaException {
            }

            @Override
            public void visit(SliceDefinition sliceAbstractionDefinition)
                    throws ProtempaException {
            }

            @Override
            public void visit(PairDefinition pairAbstractionDefinition)
                    throws ProtempaException {
            }

            @Override
            public void visit(ConstantDefinition def) throws ProtempaException {
            }
        }

        /**
         * Collect all of the propositions for which we need to create rules.
         * 
         * @param algorithms
         *            an empty {@link Map} that will be populated with
         *            algorithms for each proposition definition for which a
         *            rule will be created.
         * @param result
         *            an empty {@link Set} that will be populated with the
         *            proposition definitions for which rules will be created.
         * @param propIds
         *            the proposition id {@link String}s to be found.
         * @throws org.protempa.ProtempaException
         *             if an error occurs reading the algorithm specified by a
         *             proposition definition.
         */
        private void aggregateChildren(
                ValidateAlgorithmCheckedVisitor validatorVisitor,
                Set<PropositionDefinition> result,
                List<PropositionDefinition> propDefs) throws ProtempaException {
            DirectChildrenVisitor dcVisitor = new DirectChildrenVisitor(
                    knowledgeSource);
            for (PropositionDefinition propDef : propDefs) {
                propDef.acceptChecked(validatorVisitor);
                propDef.acceptChecked(dcVisitor);
                result.add(propDef);
                aggregateChildren(validatorVisitor, result,
                        dcVisitor.getDirectChildren());
                dcVisitor.clear();
            }
        }
    }

    private class StatelessExecutionStrategy extends AbstractExecutionStrategy {

        private StatelessSession statelessSession;

        @Override
        public void initialize() {
            this.statelessSession = ruleBase.newStatelessSession();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<Proposition> execute(String keyId,
                Set<String> propositionIds, List<?> objects,
                DataStore<String, WorkingMemory> wm) throws ProtempaException {
            StatelessSessionResult result = this.statelessSession
                    .executeWithResults(objects);
            return result.iterateObjects();
        }

        @Override
        public void cleanup() {
            clear();
        }

    }

    private class StatefulExecutionStrategy extends AbstractExecutionStrategy {

        @Override
        public void initialize() {
        }

        private StatefulSession applyRules(String keyId, List<?> objects)
                throws ProtempaException {
            StatefulSession workingMemory = ruleBase.newStatefulSession(false);
            ProtempaUtil.logger().log(Level.FINEST,
                    "Adding {0} objects for key ID {1}",
                    new Object[] { objects.size(), keyId });
            for (Object obj : objects) {
                workingMemory.insert(obj);
            }
            workingMemory.fireAllRules();
            int wmCount = 0;
            for (Iterator itr = workingMemory.iterateObjects(); itr.hasNext();) {
                Object o = itr.next();
                wmCount++;
            }
            ProtempaUtil.logger().log(Level.FINEST,
                    "Iterated over {0} objects", new Object[] { wmCount });
            return workingMemory;
        }

        @Override
        public Iterator<Proposition> execute(String keyId,
                Set<String> propositionIds, List<?> objects,
                DataStore<String, WorkingMemory> wmStore)
                throws ProtempaException {
            StatefulSession workingMemory = applyRules(keyId, objects);
            ProtempaUtil.logger().log(Level.FINEST,
                    "Persisting working memory for key ID {0}", keyId);
            wmStore.put(keyId, workingMemory);
            workingMemory.dispose();
            ProtempaUtil.logger().log(Level.FINEST,
                    "Persisted working memory for key ID {0}", keyId);

            return null;
        }

        @Override
        public void cleanup() {
        }
    }

    void retrieveData(Set<String> keyIds, Set<String> propositionIds,
            Set<And<String>> termIds, Filter filters, QuerySession qs,
            String persistentStoreName) throws FinderException {
        DataStore<String, List<Proposition>> store = PropositionStoreCreator
                .<Proposition> getInstance().getPersistentStore(
                        persistentStoreName);
        int numWritten = 0;
        try {
            ObjectIterator oi = new ObjectIterator(keyIds, propositionIds,
                    filters, qs, false);

            ProtempaUtil.logger().log(Level.INFO, "Storing propositions");
            for (Iterator<ObjectEntry> itr = oi; itr.hasNext();) {
                ObjectEntry entry = itr.next();
                String keyId = entry.keyId;
                List<Proposition> props = entry.propositions;

                store.put(keyId, props);
                numWritten++;
            }
            ProtempaUtil.logger().log(Level.INFO,
                    "Wrote {0} records into store {1}",
                    new Object[] { numWritten, persistentStoreName });
        } catch (ProtempaException ex) {
            throw new FinderException(ex);
        } finally {
            store.shutdown();
        }
    }

    void processStoredResults(Set<String> keyIds, Set<String> propositionIds,
            QuerySession qs, String propositionStoreName,
            String workingMemoryStoreName) throws FinderException {

        Logger logger = ProtempaUtil.logger();

        DataStore<String, List<Proposition>> propStore = PropositionStoreCreator
                .<Proposition> getInstance().getPersistentStore(
                        propositionStoreName);
        DataStore<String, WorkingMemory> wmStore = WorkingMemoryStoreCreator
                .getInstance(null).getPersistentStore(workingMemoryStoreName);
        DataStore<String, DerivationsBuilder> dbStore = DerivationsBuilderStoreCreator
                .getInstance().getPersistentStore(workingMemoryStoreName);

        logger.log(Level.INFO, "Found {0} records in store {1}", new Object[] {
                propStore.size(), propositionStoreName });

        try {
            DerivationsBuilder derivationsBuilder = new DerivationsBuilder();
            StatefulExecutionStrategy strategy = new StatefulExecutionStrategy();
            strategy.createRuleBase(propositionIds, derivationsBuilder, qs);
            strategy.initialize();
            int count = 0;
            for (String keyId : keysToProcess(keyIds, propStore)) {
                // the important part here is that the working memory produced
                // by the rules engine is being persisted by
                // StatefulExecutionStrategy.execute()
                if (propStore.containsKey(keyId)) {
                    strategy.execute(keyId, propositionIds,
                            propStore.get(keyId), wmStore);
                    dbStore.put(keyId, derivationsBuilder);
                    derivationsBuilder.reset();
                }
                count++;
                if (count % 100 == 0) {
                    logNumProcessed(count, logger);
                }
            }

            strategy.cleanup();
        } catch (ProtempaException ex) {
            throw new FinderException(ex);
        } finally {
            propStore.shutdown();
            wmStore.shutdown();
            dbStore.shutdown();
        }
    }

    private Set<String> keysToProcess(Set<String> keyIds,
            DataStore<String, ?> propStore) {
        Set<String> result;
        if (keyIds != null && !keyIds.isEmpty()) {
            result = keyIds;
        } else {
            result = propStore.keySet();
        }
        return result;
    }

    private void outputResult(QuerySession qs,
            Iterator<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Set<String> propositionIds, QueryResultsHandler resultHandler,
            String keyId) throws FinderException {

        Logger logger = ProtempaUtil.logger();
        if (qs.isCachingEnabled()) {
            List<Proposition> props = Iterators.asList(propositions);
            addToCache(qs, Collections.unmodifiableList(props),
                    Collections.unmodifiableMap(forwardDerivations),
                    Collections.unmodifiableMap(backwardDerivations));
            propositions = props.iterator();
        }

        logger.log(Level.FINEST, "Processing key ID: {0}", keyId);
        Map<UniqueId, Proposition> refs = new HashMap<UniqueId, Proposition>();
        List<Proposition> filteredPropositions = extractRequestedPropositions(
                propositions, propositionIds, refs);
        logger.log(Level.FINEST, "Filtered propositions: {0}",
                filteredPropositions);

        resultHandler.handleQueryResult(keyId, filteredPropositions,
                forwardDerivations, backwardDerivations, refs);
    }

    void outputStoredResults(Set<String> keyIds, Set<String> propositionIds,
            QueryResultsHandler resultHandler, QuerySession qs,
            String workingMemoryStoreName) throws FinderException {
        Logger logger = ProtempaUtil.logger();
        DataStore<String, WorkingMemory> wmStore = null;
        DataStore<String, DerivationsBuilder> dbStore = DerivationsBuilderStoreCreator
                .getInstance().getPersistentStore(workingMemoryStoreName);

        try {
            StatefulExecutionStrategy strategy = new StatefulExecutionStrategy();
            strategy.createRuleBase(propositionIds, new DerivationsBuilder(),
                    qs);
            wmStore = WorkingMemoryStoreCreator.getInstance(strategy.ruleBase)
                    .getPersistentStore(workingMemoryStoreName);
            resultHandler.init(knowledgeSource);
            logger.log(Level.INFO, "Found {0} elements in the store",
                    wmStore.size());
            for (String keyId : keysToProcess(keyIds, wmStore)) {
                logger.log(Level.FINEST, "Determining output for key {0}",
                        keyId);
                if (wmStore.containsKey(keyId)) {
                    WorkingMemory wm = wmStore.get(keyId);
                    DerivationsBuilder derivationsBuilder = dbStore.get(keyId);

                    @SuppressWarnings("unchecked")
                    Iterator<Proposition> propositions = wm.iterateObjects();
                    outputResult(qs, propositions,
                            derivationsBuilder.toForwardDerivations(),
                            derivationsBuilder.toBackwardDerivations(),
                            propositionIds, resultHandler, keyId);
                }
            }
            resultHandler.finish();
        } catch (ProtempaException ex) {
            throw new FinderException(ex);
        } finally {
            dbStore.shutdown();
            wmStore.shutdown();
        }
    }

    void processAndOutputStoredResults(Set<String> keyIds,
            Set<String> propositionIds, QueryResultsHandler resultHandler,
            QuerySession qs, String propositionStoreName)
            throws FinderException {

        Logger logger = ProtempaUtil.logger();
        DataStore<String, List<Proposition>> propStore = PropositionStoreCreator
                .<Proposition> getInstance().getPersistentStore(
                        propositionStoreName);
        try {
            DerivationsBuilder derivationsBuilder = new DerivationsBuilder();
            StatelessExecutionStrategy strategy = new StatelessExecutionStrategy();
            logger.log(Level.FINEST, "Initializing rule base");
            strategy.createRuleBase(propositionIds, derivationsBuilder, qs);
            strategy.initialize();
            logger.log(Level.FINEST, "Rule base initialized");
            resultHandler.init(knowledgeSource);
            logger.log(Level.FINEST, "Result handler initialized");
            for (String keyId : keysToProcess(keyIds, propStore)) {
                if (propStore.containsKey(keyId)) {
                    Iterator<Proposition> propositions = strategy.execute(
                            keyId, propositionIds, propStore.get(keyId), null);
                    outputResult(qs, propositions,
                            derivationsBuilder.toForwardDerivations(),
                            derivationsBuilder.toBackwardDerivations(),
                            propositionIds, resultHandler, keyId);
                    derivationsBuilder.reset();
                }
            }
            resultHandler.finish();

        } catch (ProtempaException ex) {
            throw new FinderException(ex);
        } finally {
            propStore.shutdown();
        }
    }

    private void doFindExecute(Set<String> keyIds, Set<String> propositionIds,
            Filter filters, QueryResultsHandler resultHandler, QuerySession qs,
            ExecutionStrategy strategy) throws ProtempaException {
        Logger logger = ProtempaUtil.logger();
        logger.info("Retrieving data");
        ObjectIterator objectIterator = new ObjectIterator(keyIds,
                propositionIds, filters, qs, false);
        logger.info("Data retrieval complete");
        if (objectIterator.hasNext()) {
            logger.info("Processing data");
            DerivationsBuilder derivationsBuilder = new DerivationsBuilder();
            logger.log(Level.FINE, "Creating rule base");
            strategy.createRuleBase(propositionIds, derivationsBuilder, qs);
            logger.log(Level.FINE, "Rule base is created");
            strategy.initialize();
            logger.log(Level.FINE, "Now processing data");
            int numProcessed = 0;

            for (Iterator<ObjectEntry> itr = objectIterator; itr.hasNext();) {
                ObjectEntry entry = itr.next();
                String keyId = entry.keyId;
                List<Proposition> props = entry.propositions;
                logger.log(Level.FINER, "About to assert raw data {0}", props);
                Iterator<Proposition> propositions = strategy.execute(
                        entry.keyId, propositionIds, entry.propositions, null);
                processResults(qs, propositions,
                        derivationsBuilder.toForwardDerivations(),
                        derivationsBuilder.toBackwardDerivations(),
                        propositionIds, resultHandler, keyId);
                derivationsBuilder.reset();
                if (++numProcessed % 1000 == 0) {
                    logNumProcessed(numProcessed, logger);
                }
            }
            strategy.cleanup();
            logger.info("Processing data is complete");
        } else {
            logger.info("No data to process");
        }
    }

    private void logNumProcessed(int numProcessed, Logger logger)
            throws DataSourceReadException {
        if (logger.isLoggable(Level.FINE)) {
            String keyTypeSingDisplayName = this.dataSource
                    .getKeyTypeDisplayName();
            String keyTypePluralDisplayName = this.dataSource
                    .getKeyTypePluralDisplayName();
            Logging.logCount(logger, Level.FINE, numProcessed,
                    "Processed {0} {1}", "Processed {0} {1}",
                    new Object[] { keyTypeSingDisplayName },
                    new Object[] { keyTypePluralDisplayName });
        }
    }

    private void processResults(QuerySession qs,
            Iterator<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Set<String> propositionIds, QueryResultsHandler resultHandler,
            String keyId) throws FinderException {
        Logger logger = ProtempaUtil.logger();

        if (qs.isCachingEnabled()) {
            List<Proposition> props = Iterators.asList(propositions);
            addToCache(qs, Collections.unmodifiableList(props),
                    Collections.unmodifiableMap(forwardDerivations),
                    Collections.unmodifiableMap(backwardDerivations));
            propositions = props.iterator();
        }

        Map<UniqueId, Proposition> refs = new HashMap<UniqueId, Proposition>();
        logger.log(Level.FINER, "References: {0}", refs);
        List<Proposition> filteredPropositions = // a newly created list
        extractRequestedPropositions(propositions, propositionIds, refs);
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Proposition ids: {0}", propositionIds);
            logger.log(Level.FINER, "Filtered propositions: {0}",
                    filteredPropositions);
            logger.log(Level.FINER, "Forward derivations: {0}",
                    forwardDerivations);
            logger.log(Level.FINER, "Backward derivations: {0}",
                    backwardDerivations);
        }

        resultHandler.handleQueryResult(keyId, filteredPropositions,
                forwardDerivations, backwardDerivations, refs);
    }

    private static class ObjectEntry {

        String keyId;
        List<Proposition> propositions;
    }

    private class ObjectIterator implements Iterator<ObjectEntry> {

        private Map<String, List<Proposition>> propositions;
        private final Logger logger;
        private final Iterator<String> keySetItr;

        ObjectIterator(Set<String> keyIds, Set<String> propIds, Filter filters,
                QuerySession qs, boolean stateful)
                throws KnowledgeSourceReadException, DataSourceReadException {
            this.logger = ProtempaUtil.logger();

            this.logger.log(Level.INFO, "Starting data retrieval");

            Set<String> leafPropIds = knowledgeSource
                    .inDataSourcePropositionIds(propIds
                            .toArray(new String[propIds.size()]));
            this.logger.log(Level.FINE, "Proposition ids: {0}", leafPropIds);
            this.propositions = dataSource.readPropositions(keyIds,
                    leafPropIds, filters, qs);
            Set<String> keySet = this.propositions.keySet();

            this.keySetItr = keySet.iterator();

            this.logger.log(Level.INFO, "Data retrieval is complete");
            if (this.logger.isLoggable(Level.INFO)) {
                Logging.logCount(
                        this.logger,
                        Level.INFO,
                        this.propositions.size(),
                        "There is {0} {1} to process",
                        "There are {0} {1} to process",
                        new Object[] { dataSource.getKeyTypeDisplayName() },
                        new Object[] { dataSource.getKeyTypePluralDisplayName() });
            }
        }

        @Override
        public boolean hasNext() {
            return this.keySetItr.hasNext();
        }

        @Override
        public ObjectEntry next() {
            ObjectEntry result = new ObjectEntry();
            result.keyId = this.keySetItr.next();
            result.propositions = this.propositions.get(result.keyId);
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns a stateful working memory instance for the given key.
     * 
     * @param key
     *            a key <code>String</code>.
     * @return a <code>StatefulRuleSession</code>, or null if the given key is
     *         <code>null</code>.
     */
    private StatefulSession statefulWorkingMemory(RuleBase ruleBase, String key)
            throws ProtempaException {
        StatefulSession workingMemory = null;
        if (key != null) {
            // we have not cached a working memory for this key yet.
            if ((workingMemory = this.workingMemoryCache.get(key)) == null) {
                // TODO: change the last null parameter to an actual derivations
                // cache
                workingMemory = ruleBase.newStatefulSession(false);
                this.workingMemoryCache.put(key, workingMemory);
                // we have cached a working memory for this key.
            } // else {
            /*
             * There apparently is no way to assign an existing working memory
             * to a knowledge base without serializing it and passing the
             * serialized object to a new rule base... This could actually come
             * in handy for transparently saving the working memories out to
             * disk...
             */
            // try {
            // byte[] wmSerialized = detachWorkingMemory(workingMemory);
            // Set<String> propIdCacheForKey = this.propIdCache.get(key);
            // assert propIdCacheForKey != null :
            // "the proposition id cache was not set";
            /*
             * We construct the rule base of proposition definitions that have
             * not been looked for previously.
             */
            // TODO: change the last null parameter to an actual
            // derivations cache
            // workingMemory = reattachWorkingMemory(wmSerialized,
            // ruleBase);
            // this.workingMemoryCache.put(key, workingMemory);
            // } catch (IOException ex) {
            // throw new AssertionError(ex);
            // } catch (ClassNotFoundException cnfe) {
            // throw new AssertionError(cnfe);
            // }
            // }
        }
        return workingMemory;
    }
}
