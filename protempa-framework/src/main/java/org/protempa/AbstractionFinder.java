package org.protempa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.query.And;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Class that actually does the abstraction finding.
 * 
 * @author Andrew Post
 */
final class AbstractionFinder implements Module {

    private final Map<String, StatefulSession> workingMemoryCache;
    //private final Map<String, Set<String>> propIdCache;
    private final DataSource dataSource;
    private final KnowledgeSource knowledgeSource;
    private final TermSource termSource;
    private final AlgorithmSource algorithmSource;
    // private final Map<String, List<String>> termToPropDefMap;
    private boolean clearNeeded;
    private final Map<String, Map<Set<String>, Sequence<PrimitiveParameter>>> sequences;
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

        this.dataSource.addSourceListener(new SourceListener<DataSourceUpdatedEvent>() {

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

        this.knowledgeSource.addSourceListener(new SourceListener<KnowledgeSourceUpdatedEvent>() {

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

        this.termSource.addSourceListener(new SourceListener<TermSourceUpdatedEvent>() {

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

        this.algorithmSource.addSourceListener(new SourceListener<AlgorithmSourceUpdatedEvent>() {

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
//        if (cacheFoundAbstractParameters) {
//            this.propIdCache = new HashMap<String, Set<String>>();
//        } else {
//            this.propIdCache = null;
//        }
        this.sequences = new HashMap<String, Map<Set<String>, Sequence<PrimitiveParameter>>>();
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
//            List<String> termPropIds = getPropIdsFromTerms(explodeTerms(termIds));
//            List<String> allPropIds = new ArrayList<String>();
//            allPropIds.addAll(propIds);
//            allPropIds.addAll(termPropIds);

            if (workingMemoryCache != null) {
                doFindExecute(keyIds, propIds, filters, resultHandler, qs,
                        new StatefulExecutionStrategy());
            } else {
                doFindExecute(keyIds, propIds, filters, resultHandler, qs,
                        new StatelessExecutionStrategy());
            }
            resultHandler.finish();
        } catch (ProtempaException e) {
            String msg = "Query could not complete";
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
            this.sequences.clear();
            clearNeeded = false;
        }
    }

    @Override
    public void close() {
        clear();
        this.closed = true;
    }

    private static List<Sequence<PrimitiveParameter>> createSequenceList(
            Map<Set<String>, Sequence<PrimitiveParameter>> seqKey,
            List<PrimitiveParameter> primParams) {
        List<Sequence<PrimitiveParameter>> sequenceList =
                new ArrayList<Sequence<PrimitiveParameter>>();
        for (Map.Entry<Set<String>, Sequence<PrimitiveParameter>> entry :
                seqKey.entrySet()) {
            Set<String> paramIds = entry.getKey();
            Sequence<PrimitiveParameter> seq = entry.getValue();
            if (seq.isEmpty()) {
                for (PrimitiveParameter parameter : primParams) {
                    if (paramIds.contains(parameter.getId())) {
                        seq.add(parameter);
                    }
                }
            }
            sequenceList.add(seq);
        }
        return sequenceList;
    }

    private void addToCache(QuerySession qs, List<Proposition> propositions, Map<Proposition, List<Proposition>> derivations) {
        qs.addPropositionsToCache(propositions);
        for (Map.Entry<Proposition, List<Proposition>> me : derivations.entrySet()) {
            qs.addDerivationsToCache(me.getKey(), me.getValue());
        }
    }

    private List<Proposition> extractRequestedPropositions(List<Proposition> propositions, Set<String> propositionIds) {
        List<Proposition> result = new ArrayList<Proposition>();
        for (Proposition prop : propositions) {
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
            result.addAll(this.knowledgeSource.getPropositionDefinitionsByTerm(subsumpClause));
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
                tss.add(TermSubsumption.fromTerms(this.termSource.getTermSubsumption(termId)));
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
            for (Iterator<StatefulSession> itr = workingMemoryCache.values().iterator(); itr.hasNext();) {
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

        List<Proposition> execute(
                String keyIds, Set<String> propositionIds, List<?> objects)
                throws ProtempaException;

        void cleanup();

        void createRuleBase(Set<String> propIds,
                DerivationsBuilder listener, QuerySession qs)
                throws ProtempaException;
    }

    private abstract class AbstractExecutionStrategy
            implements ExecutionStrategy {

        protected RuleBase ruleBase;

        @Override
        public void createRuleBase(Set<String> propIds,
                DerivationsBuilder listener,
                QuerySession qs) throws ProtempaException {
            ValidateAlgorithmCheckedVisitor visitor =
                    new ValidateAlgorithmCheckedVisitor(algorithmSource);
            JBossRuleCreator ruleCreator = new JBossRuleCreator(
                    visitor.getAlgorithms(), listener);
            List<PropositionDefinition> propDefs =
                    new ArrayList<PropositionDefinition>(propIds.size());
            for (String propId : propIds) {
                PropositionDefinition propDef =
                        knowledgeSource.readPropositionDefinition(propId);
                if (propDef != null) {
                    propDefs.add(propDef);
                } else {
                    throw new FinderException("Invalid proposition id: " + propId);
                }
            }
            if (propIds != null) {
                Set<PropositionDefinition> result =
                        new HashSet<PropositionDefinition>();
                aggregateChildren(visitor, result, propDefs);
                ruleCreator.visit(result);
            }
            this.ruleBase =
                    new JBossRuleBaseFactory(ruleCreator, createRuleBaseConfiguration(ruleCreator)).newInstance();
            clearNeeded = true;
        }

        protected RuleBaseConfiguration createRuleBaseConfiguration(JBossRuleCreator ruleCreator) throws PropositionDefinitionInstantiationException {
            RuleBaseConfiguration config = new RuleBaseConfiguration();
            config.setShadowProxy(false);
            try {
                config.setConflictResolver(new PROTEMPAConflictResolver(
                        knowledgeSource,
                        ruleCreator.getRuleToAbstractionDefinitionMap()));
            } catch (KnowledgeSourceReadException ex) {
                throw new PropositionDefinitionInstantiationException(
                        "Could not instantiate proposition definitions", ex);
            }
            config.setAssertBehaviour(AssertBehaviour.EQUALITY);
            return config;
        }

        private class ValidateAlgorithmCheckedVisitor
                extends AbstractPropositionDefinitionCheckedVisitor {

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
                String algorithmId = lowLevelAbstractionDefinition.getAlgorithmId();
                Algorithm algorithm = algorithmSource.readAlgorithm(algorithmId);
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
         *            an empty {@link Map} that will be populated with algorithms
         *            for each proposition definition for which a rule will be
         *            created.
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
                Set<PropositionDefinition> result, List<PropositionDefinition> propDefs)
                throws ProtempaException {
            DirectChildrenVisitor dcVisitor = new DirectChildrenVisitor(knowledgeSource);
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

    private class StatelessExecutionStrategy
            extends AbstractExecutionStrategy {

        private StatelessSession statelessSession;

        @Override
        public void initialize() {
            this.statelessSession = ruleBase.newStatelessSession();
        }

        @Override
        public List<Proposition> execute(
                String keyId, Set<String> propositionIds, List<?> objects)
                throws ProtempaException {
            StatelessSessionResult result =
                    this.statelessSession.executeWithResults(objects);
            return unpack(result.iterateObjects());
        }

        @Override
        public void cleanup() {
            clear();
        }

        @Override
        protected RuleBaseConfiguration createRuleBaseConfiguration(
                JBossRuleCreator ruleCreator) throws PropositionDefinitionInstantiationException {
            RuleBaseConfiguration result =
                    super.createRuleBaseConfiguration(ruleCreator);
            result.setSequential(true);
            return result;
        }


    }

    private class StatefulExecutionStrategy
            extends AbstractExecutionStrategy {

        @Override
        public void initialize() {
        }

        @Override
        public List<Proposition> execute(
                String keyId, Set<String> propositionIds, List<?> objects)
                throws ProtempaException {
            StatefulSession workingMemory =
                    statefulWorkingMemory(ruleBase, keyId);
            for (Object obj : objects) {
                workingMemory.insert(obj);
            }
            workingMemory.fireAllRules();
            List<Proposition> resultList = unpack(
                    workingMemory.iterateObjects());
            return resultList;
        }

        @Override
        public void cleanup() {
        }
    }

    private void doFindExecute(Set<String> keyIds,
            Set<String> propositionIds, Filter filters,
            QueryResultsHandler resultHandler, QuerySession qs,
            ExecutionStrategy strategy) throws ProtempaException {
        Logger logger = ProtempaUtil.logger();

        DerivationsBuilder derivationsBuilder = new DerivationsBuilder();
        logger.log(Level.FINE, "Creating rule base");
        strategy.createRuleBase(propositionIds, derivationsBuilder, qs);
        logger.log(Level.FINE, "Rule base is created");

        logger.log(Level.FINE, "Now retrieving data");
        strategy.initialize();
        ObjectIterator objectIterator = new ObjectIterator(keyIds,
                propositionIds, filters, qs, false);
        logger.log(Level.FINE, "Now processing data");
        for (Iterator<ObjectEntry> itr = objectIterator; itr.hasNext();) {
            ObjectEntry entry = itr.next();
            logger.log(Level.FINER, "About to assert raw data {0}",
                    entry.propositions);
            List<Proposition> propositions = strategy.execute(
                    entry.keyId, propositionIds, entry.propositions);
            logger.log(Level.FINER, "Retrieved propositions: {0}",
                    propositions);
            processResults(qs, propositions,
                    derivationsBuilder.toDerivations(),
                    propositionIds, resultHandler, entry.keyId);
            derivationsBuilder.reset();
        }
        strategy.cleanup();
        logger.log(Level.FINE, "Processing is complete");
    }

    private void processResults(QuerySession qs,
            List<Proposition> propositions,
            Map<Proposition, List<Proposition>> derivations,
            Set<String> propositionIds, QueryResultsHandler resultHandler,
            String keyId) throws FinderException {
        Logger logger = ProtempaUtil.logger();

        if (qs.isCachingEnabled()) {
            addToCache(qs, Collections.unmodifiableList(propositions),
                    derivations);
        }

        Map<UniqueIdentifier, Proposition> refs =
                createReferences(propositions);
        logger.log(Level.FINER, "References: {0}", refs);
        List<Proposition> filteredPropositions = //a newly created list
                extractRequestedPropositions(propositions, propositionIds);
        logger.log(Level.FINER, "Proposition ids: {0}", propositionIds);
        logger.log(Level.FINER, "Filtered propositions: {0}",
                filteredPropositions);

        resultHandler.handleQueryResult(keyId, filteredPropositions,
                derivations, refs);
    }

    private Map<UniqueIdentifier, Proposition> createReferences(
            List<Proposition> propositions) {
        Map<UniqueIdentifier, Proposition> refs =
                new HashMap<UniqueIdentifier, Proposition>();
        for (Proposition proposition : propositions) {
            refs.put(proposition.getUniqueIdentifier(), proposition);
        }
        return refs;
    }

    /**
     * Copies the results of rules engine processing into a
     * list of propositions.
     */
    private static List<Proposition> unpack(Iterator<?> objects) {
        List<Proposition> result = new ArrayList<Proposition>(500);
        for (; objects.hasNext();) {
            Object obj = objects.next();
            if (obj instanceof Sequence<?>) {
                result.addAll((Sequence<?>) obj);
            } else {
                result.add((Proposition) obj);
            }
        }

        return result;
    }

    private static class ObjectEntry {

        String keyId;
        List<Object> propositions;
    }

    private static class SeparatePropositionDefinitionVisitor extends AbstractPropositionDefinitionVisitor {

        final List<EventDefinition> eventDefinitions;
        final List<AbstractionDefinition> abstractionDefinitions;
        final List<PrimitiveParameterDefinition> primitiveParameterDefinitions;
        final List<ConstantDefinition> constantDefinitions;

        SeparatePropositionDefinitionVisitor() {
            this.eventDefinitions = new ArrayList<EventDefinition>();
            this.abstractionDefinitions = new ArrayList<AbstractionDefinition>();
            this.primitiveParameterDefinitions =
                    new ArrayList<PrimitiveParameterDefinition>();
            this.constantDefinitions = new ArrayList<ConstantDefinition>();
        }

        @Override
        public void visit(EventDefinition eventDefinition) {
            this.eventDefinitions.add(eventDefinition);
        }

        @Override
        public void visit(HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
            this.abstractionDefinitions.add(highLevelAbstractionDefinition);
        }

        @Override
        public void visit(LowLevelAbstractionDefinition lowLevelAbstractionDefinition) {
            this.abstractionDefinitions.add(lowLevelAbstractionDefinition);
        }

        @Override
        public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) {
            this.primitiveParameterDefinitions.add(primitiveParameterDefinition);
        }

        @Override
        public void visit(SliceDefinition sliceAbstractionDefinition) {
            this.abstractionDefinitions.add(sliceAbstractionDefinition);
        }

        @Override
        public void visit(ConstantDefinition constantDefinition) {
            this.constantDefinitions.add(constantDefinition);
        }

        @Override
        public void visit(PairDefinition pairDefinition) {
            this.abstractionDefinitions.add(pairDefinition);
        }
    }

    private class ObjectIterator implements Iterator<ObjectEntry> {

        private Map<String, List<Event>> events;
        private Map<String, List<Constant>> constants;
        private Map<String, List<PrimitiveParameter>> primitiveParameters;
        private Iterator<String> keyIdItr;
        private final Set<String> propositionIds;
        private final boolean stateful;
        private final Logger logger;
        private Set<Set<String>> sequenceResultCache;

        ObjectIterator(Set<String> keyIds,
                Set<String> propIds, Filter filters, QuerySession qs,
                boolean stateful)
                throws KnowledgeSourceReadException, DataSourceReadException {
            this.logger = ProtempaUtil.logger();

            this.propositionIds = propIds;
            this.stateful = stateful;

            this.logger.log(Level.FINE, "Starting data retrieval");

            Set<PropositionDefinition> leaves =
                    knowledgeSource.leafPropositionDefinitions(propIds);

            SeparatePropositionDefinitionVisitor visitor =
                    new SeparatePropositionDefinitionVisitor();
            visitor.visit(leaves);

            this.events = retrieveEvents(visitor.eventDefinitions, keyIds, filters, qs);
            this.constants = retrieveConstants(visitor.constantDefinitions, keyIds,
                    filters, qs);
            this.primitiveParameters = retrievePrimitiveParameters(
                    visitor.primitiveParameterDefinitions, keyIds, filters, qs, stateful);
            this.keyIdItr = aggregateKeyIds();

            this.logger.log(Level.FINE, "Data retrieval is complete");
        }

        @Override
        public boolean hasNext() {
            return this.keyIdItr.hasNext();
        }

        @Override
        public ObjectEntry next() {
            ObjectEntry result = new ObjectEntry();
            result.keyId = this.keyIdItr.next();
            result.propositions = new ArrayList<Object>(500);
            try {
                List<PrimitiveParameter> primParams =
                        this.primitiveParameters.get(result.keyId);
                List<Sequence<PrimitiveParameter>> sequences =
                        createSequencesFromPrimitiveParameters(result.keyId,
                        primParams,
                        this.propositionIds, this.stateful);

                this.logger.log(Level.FINEST, "SEQUENCES: {0}", sequences);
                result.propositions.addAll(this.events.get(result.keyId));
                result.propositions.addAll(this.constants.get(result.keyId));
                result.propositions.addAll(sequences);
                result.propositions.addAll(primParams);
            } catch (KnowledgeSourceReadException ex) {
                throw new IllegalStateException(ex);
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private Iterator<String> aggregateKeyIds() {
            Set<String> keyIds = new HashSet<String>();
            keyIds.addAll(this.events.keySet());
            keyIds.addAll(this.constants.keySet());
            keyIds.addAll(this.primitiveParameters.keySet());
            return keyIds.iterator();
        }

        private Map<String, List<PrimitiveParameter>> retrievePrimitiveParameters(
                Collection<PrimitiveParameterDefinition> primParamDefs,
                Set<String> keyIds, Filter filters,
                QuerySession qs, boolean stateful)
                throws KnowledgeSourceReadException, DataSourceReadException {
            Map<String, List<PrimitiveParameter>> result;
            Set<String> propIds = new HashSet<String>();
            for (PropositionDefinition propDef : primParamDefs) {
                propIds.add(propDef.getId());
            }
            this.logger.log(Level.FINE, "Primitive parameter ids: {0}",
                    propIds);
            result = dataSource.getPrimitiveParameters(keyIds,
                    propIds, filters, qs);
            if (this.logger.isLoggable(Level.FINEST)) {
                for (Map.Entry<String, List<PrimitiveParameter>> e :
                        result.entrySet()) {
                    this.logger.log(Level.FINEST,
                            "RETURNED PRIMITIVE PARAMETERS: {0}", e);
                }
            }
            return result;

        }

        private Map<String, List<Constant>> retrieveConstants(
                Collection<ConstantDefinition> constantDefinitions,
                Set<String> keyIds, Filter filters, QuerySession qs)
                throws KnowledgeSourceReadException, DataSourceReadException {
            Map<String, List<Constant>> result;
            Set<String> propIds = new HashSet<String>();
            for (PropositionDefinition propDef : constantDefinitions) {
                propIds.add(propDef.getId());
            }
            this.logger.log(Level.FINE, "Constant ids: {0}", propIds);
            result = dataSource.getConstantPropositions(keyIds,
                    propIds, filters, qs);
            return result;
        }

        private Map<String, List<Event>> retrieveEvents(
                Collection<EventDefinition> eventDefinitions,
                Set<String> keyIds, Filter filters, QuerySession qs)
                throws DataSourceReadException, KnowledgeSourceReadException {
            Map<String, List<Event>> result;
            Set<String> propIds = new HashSet<String>();
            for (PropositionDefinition propDef : eventDefinitions) {
                propIds.add(propDef.getId());
            }
            this.logger.log(Level.FINE, "Event ids: {0}", propIds);
            result = dataSource.getEvents(keyIds, propIds,
                    filters, qs);
            return result;
        }

        private List<Sequence<PrimitiveParameter>> createSequencesFromPrimitiveParameters(
                String keyId, List<PrimitiveParameter> primParams,
                Set<String> propIds, boolean stateful)
                throws KnowledgeSourceReadException {
            Map<Set<String>, Sequence<PrimitiveParameter>> seqKey =
                    getOrCreateEmptySequenceKeyMap(keyId, propIds,
                    stateful);
            List<Sequence<PrimitiveParameter>> paramSeqs =
                    createSequenceList(seqKey, primParams);
            return paramSeqs;
        }

        private Map<Set<String>, Sequence<PrimitiveParameter>> getOrCreateEmptySequenceKeyMap(String keyId,
                Set<String> propIds,
                boolean stateful) throws KnowledgeSourceReadException {
            Map<Set<String>, Sequence<PrimitiveParameter>> seqKeyMap =
                    sequences.get(keyId);
            if (seqKeyMap == null) {
                seqKeyMap =
                        new HashMap<Set<String>, Sequence<PrimitiveParameter>>();
            }
            if (this.sequenceResultCache == null) {
                this.sequenceResultCache = extractSequenceParamIds(propIds);
            }
            for (Set<String> paramIds : this.sequenceResultCache) {
                if (!seqKeyMap.containsKey(paramIds)) {
                    seqKeyMap.put(paramIds,
                            new Sequence<PrimitiveParameter>(paramIds));
                }
            }
            if (stateful) {
                sequences.put(keyId, seqKeyMap);
            }
            return seqKeyMap;
        }

        private void extractSequenceParamIdsHelper(String[] propIds,
                Set<Set<String>> sequenceParamIds)
                throws KnowledgeSourceReadException {
            for (String propId : propIds) {
                AbstractionDefinition def =
                        knowledgeSource.readAbstractionDefinition(propId);
                if (def != null) {
                    if (def instanceof LowLevelAbstractionDefinition) {
                        Set<String> abstractedFrom =
                                knowledgeSource.leafPrimitiveParameterIds(
                                propId);
                        assert !abstractedFrom.isEmpty() :
                                "abstractedFrom should not be empty";
                        sequenceParamIds.add(abstractedFrom);
                    } else {
                        extractSequenceParamIdsHelper(def.getDirectChildren(),
                                sequenceParamIds);
                    }
                }
            }
        }

        private Set<Set<String>> extractSequenceParamIds(Set<String> propIds)
                throws KnowledgeSourceReadException {
            Set<Set<String>> result = new HashSet<Set<String>>();
            extractSequenceParamIdsHelper(
                    propIds.toArray(new String[propIds.size()]), result);
            return result;

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
    private StatefulSession statefulWorkingMemory(RuleBase ruleBase, String key) throws ProtempaException {
        StatefulSession workingMemory = null;
        if (key != null) {
            //we have not cached a working memory for this key yet.
            if ((workingMemory = this.workingMemoryCache.get(key)) == null) {
                // TODO: change the last null parameter to an actual derivations
                // cache
                workingMemory = ruleBase.newStatefulSession(false);
                this.workingMemoryCache.put(key, workingMemory);
                //we have cached a working memory for this key.
            } //else {
                /*
             * There apparently is no way to assign an existing working
             * memory to a knowledge base without serializing it and passing
             * the serialized object to a new rule base... This could
             * actually come in handy for transparently saving the working
             * memories out to disk...
             */
            //try {
            //byte[] wmSerialized = detachWorkingMemory(workingMemory);
            //Set<String> propIdCacheForKey = this.propIdCache.get(key);
            //assert propIdCacheForKey != null : "the proposition id cache was not set";
                    /*
             * We construct the rule base of proposition definitions
             * that have not been looked for previously.
             */
            // TODO: change the last null parameter to an actual
            // derivations cache
//                    workingMemory = reattachWorkingMemory(wmSerialized,
//                            ruleBase);
            //this.workingMemoryCache.put(key, workingMemory);
//                } catch (IOException ex) {
//                    throw new AssertionError(ex);
//                } catch (ClassNotFoundException cnfe) {
//                    throw new AssertionError(cnfe);
//                }
            //}
        }
        return workingMemory;
    }
}
