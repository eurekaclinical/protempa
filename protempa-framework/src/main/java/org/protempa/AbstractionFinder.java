package org.protempa;

import org.protempa.dsb.filter.Filter;
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

import org.drools.FactException;
import org.drools.ObjectFilter;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Class that actually does the abstraction finding.
 * 
 * @author Andrew Post
 */
final class AbstractionFinder implements Module {

    private final Map<String, StatefulSession> workingMemoryCache;
    private final Map<String, Set<String>> propIdCache;
    private final DataSource dataSource;
    private final KnowledgeSource knowledgeSource;
    private final TermSource termSource;
    private final AlgorithmSource algorithmSource;
    private final Map<String, List<String>> termToPropDefMap;
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

        try {
            this.termToPropDefMap = knowledgeSource.mapTermsToPropDefinitions();
        } catch (KnowledgeSourceReadException ex) {
            throw new KnowledgeSourceReadException(
                    "Unable to map term IDs to proposition definition IDs", ex);
        }

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
        if (cacheFoundAbstractParameters) {
            this.propIdCache = new HashMap<String, Set<String>>();
        } else {
            this.propIdCache = null;
        }
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

    void doFind(Set<String> keyIds, Set<String> propIds, Filter filters,
            QueryResultsHandler resultHandler, QuerySession qs)
            throws FinderException {
        if (this.closed) {
            throw new FinderException("Protempa already closed!");
        }
        try {
            resultHandler.init(this.knowledgeSource);
            if (workingMemoryCache != null) {
                doFindStateful(keyIds, propIds, filters, resultHandler, qs);
            } else {
                doFindStateless(keyIds, propIds, filters, resultHandler, qs);
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

    private static final class ProtempaObjectFilter implements ObjectFilter {

        private static final long serialVersionUID = 3649738847744966643L;
        private final Set<String> paramIds;

        ProtempaObjectFilter(Set<String> paramIds) {
            if (paramIds != null) {
                this.paramIds = paramIds;
            } else {
                this.paramIds = Collections.emptySet();
            }
        }

        @Override
        public boolean accept(Object workingMemoryObject) {
            if (paramIds.isEmpty()) {
                return true;
            }
            if (workingMemoryObject instanceof Proposition) {
                return this.paramIds
                        .contains(((Proposition) workingMemoryObject).getId()) ? true
                        : false;
            } else {
                Sequence<?> s = (Sequence<?>) workingMemoryObject;
                return s.getPropositionIds().size() == 1
                        && this.paramIds.containsAll(s.getPropositionIds()) ? true
                        : false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doFindStateless(Set<String> keyIds,
            Set<String> propositionIds, Filter filters,
            QueryResultsHandler resultHandler, QuerySession qs)
            throws ProtempaException {
        for (Map.Entry<String, List<Object>> entry : objectsToAssert(keyIds,
                propositionIds, filters, qs, false).entrySet()) {
            Map<Proposition, List<Proposition>> derivations = new HashMap<Proposition, List<Proposition>>(); // need
            // to
            // populate
            List objects = new ArrayList(entry.getValue());
            List<Proposition> propositions = resultList(statelessWorkingMemory(
                    propositionIds, derivations).executeWithResults(objects)
                    .iterateObjects(new ProtempaObjectFilter(propositionIds)));

            if (qs.isCachingEnabled()) {
                qs.addPropositionsToCache(propositions);
                for (Map.Entry<Proposition, List<Proposition>> me : derivations
                        .entrySet()) {
                    qs.addDerivationsToCache(me.getKey(), me.getValue());
                }
            }
            Map<UniqueIdentifier, Proposition> refs = createReferencesMap(
                    objects, propositions);
            resultHandler.handleQueryResult(entry.getKey(), propositions,
                    derivations, refs);
        }
        clear();
    }

    private Map<UniqueIdentifier, Proposition> createReferencesMap(
            List propositionsOrSequences, List<Proposition> propositions) {
        Map<UniqueIdentifier, Proposition> refs = new HashMap<UniqueIdentifier, Proposition>();
        for (Object o : propositionsOrSequences) {
            if (o instanceof Sequence) {
                Sequence seq = (Sequence) o;
                for (Object obj : seq) {
                    Proposition prop = (Proposition) obj;
                    refs.put(prop.getUniqueIdentifier(), prop);
                }
            } else {
                Proposition prop = (Proposition) o;
                refs.put(prop.getUniqueIdentifier(), prop);
            }
        }
        for (Proposition proposition : propositions) {
            refs.put(proposition.getUniqueIdentifier(), proposition);
        }
        return refs;
    }

    @SuppressWarnings("unchecked")
    private static List<Proposition> resultList(Iterator objects) {
        List<Proposition> result = new ArrayList<Proposition>();
        for (; objects.hasNext();) {
            Object obj = objects.next();
            if (obj instanceof Sequence) {
                result.addAll((Sequence) obj);
            } else {
                result.add((Proposition) obj);
            }
        }

        return result;
    }

    private Map<String, List<Object>> objectsToAssert(Set<String> keyIds,
            Set<String> propositionIds, Filter filters, QuerySession qs,
            boolean stateful) throws DataSourceReadException,
            KnowledgeSourceReadException {
        Map<String, List<Object>> objects =
                new HashMap<String, List<Object>>();

        // Add events
        Set<String> eventIds = knowledgeSource.leafEventIds(propositionIds);
        if (!eventIds.isEmpty() || propositionIds == null
                || propositionIds.isEmpty()) {
            Map<String, List<Event>> tempObjects =
                    createEvents(dataSource, keyIds, eventIds, filters, qs);
            for (Map.Entry<String, List<Event>> entry :
                    tempObjects.entrySet()) {
                org.arp.javautil.collections.Collections.putListAll(objects,
                        entry.getKey(), entry.getValue());
            }
        }

        // Add constants
        Set<String> constantIds =
                knowledgeSource.leafConstantIds(propositionIds);
        if (!constantIds.isEmpty() || propositionIds == null
                || propositionIds.isEmpty()) {
            Map<String, List<Constant>> tempObjects =
                    createConstantPropositions(dataSource, keyIds, constantIds,
                    filters, qs);
            for (Map.Entry<String, List<Constant>> entry :
                    tempObjects.entrySet()) {
                org.arp.javautil.collections.Collections.putListAll(objects,
                        entry.getKey(), entry.getValue());
            }
        }

        // Add parameters. Requires special handling, because Sequence objects
        // do not override equals. Can we eliminate sequence cache?
        for (Map.Entry<String, List<Sequence<PrimitiveParameter>>> entry :
                createSequencesFromPrimitiveParameters(
                keyIds, dataSource.getPrimitiveParametersAsc(keyIds,
                knowledgeSource.primitiveParameterIds(propositionIds),
                filters, qs), propositionIds,
                stateful).entrySet()) {
            org.arp.javautil.collections.Collections.putListAll(objects,
                        entry.getKey(), entry.getValue());
        }

        return objects;
    }

    private void doFindStateful(Set<String> keyIds, Set<String> propositionIds,
            Filter filters, QueryResultsHandler resultHandler, QuerySession qs)
            throws ProtempaException {
        if (keyIds != null && !keyIds.isEmpty()) {
            Map<String, List<Object>> objects = objectsToAssert(keyIds,
                    propositionIds, filters, qs, true);
            for (Map.Entry<String, List<Object>> entry : objects.entrySet()) {
                try {
                    // TODO: need to populate
                    Map<Proposition, List<Proposition>> derivationsMap = new HashMap<Proposition, List<Proposition>>();

                    StatefulSession workingMemory = statefulWorkingMemory(entry
                            .getKey(), propositionIds);
                    List<Object> objs = entry.getValue();
                    for (Object obj : objs) {
                        workingMemory.insert(obj);
                    }
                    workingMemory.fireAllRules();
                    List<Proposition> resultList = resultList(workingMemory
                            .iterateObjects(new ProtempaObjectFilter(
                                    propositionIds)));
                    if (qs.isCachingEnabled()) {
                        qs.addPropositionsToCache(resultList);
                        for (Map.Entry<Proposition, List<Proposition>> me : derivationsMap
                                .entrySet()) {
                            for (Proposition derived : me.getValue())
                                qs.addDerivationToCache(me.getKey(), derived);
                        }
                    }
                    Map<UniqueIdentifier, Proposition> refs = createReferencesMap(
                            objs, resultList);
                    resultHandler.handleQueryResult(entry.getKey(), resultList,
                            derivationsMap, refs);
                } catch (FactException fe) {
                    assert false;
                }
            }
        }
        // return new HashMap<String, List<Proposition>>();
    }

    private Map<String, List<Event>> createEvents(DataSource dataSource,
            Set<String> keyIds, Set<String> eventIds, Filter filters,
            QuerySession qs) throws DataSourceReadException {
        return dataSource.getEventsAsc(keyIds, eventIds, filters, qs);
    }

    private Map<String, List<Constant>> createConstantPropositions(
            DataSource dataSource,
            Set<String> keyIds, Set<String> constantIds,
            Filter filters, QuerySession qs)
            throws DataSourceReadException {
        return dataSource.getConstantPropositions(keyIds, constantIds, filters,
                qs);
    }

    /*
     * FIXME The primitive parameters are sorted already, yet we resort them in
     * Sequence.
     */
    private List<Sequence<PrimitiveParameter>> createSequencesFromPrimitiveParameters(
            String keyId, List<PrimitiveParameter> primParams,
            Set<String> propositionIds, boolean stateful)
            throws KnowledgeSourceReadException {
        ArrayList<Sequence<PrimitiveParameter>> result = new ArrayList<Sequence<PrimitiveParameter>>();
        if (primParams != null && !primParams.isEmpty()) {
            Map<Set<String>, Sequence<PrimitiveParameter>> seqKey = this.sequences
                    .get(keyId);
            if (seqKey == null) {
                seqKey = new HashMap<Set<String>, Sequence<PrimitiveParameter>>();
            }
            for (Set<String> paramIds : extractSequenceParamIds(propositionIds)) {
                if (!seqKey.containsKey(paramIds)) {
                    seqKey.put(paramIds, new Sequence<PrimitiveParameter>(
                            paramIds));
                }
            }
            if (stateful) {
                this.sequences.put(keyId, seqKey);
            }

            for (Map.Entry<Set<String>, Sequence<PrimitiveParameter>> entry : seqKey
                    .entrySet()) {
                Set<String> key = entry.getKey();
                Sequence<PrimitiveParameter> seq = entry.getValue();
                if (seq.isEmpty()) {
                    for (PrimitiveParameter parameter : primParams) {
                        if (key.contains(parameter.getId())) {
                            seq.add(parameter);
                        }
                    }
                }
                result.add(seq);
            }
        }
        return result;
    }

    private Map<String, List<Sequence<PrimitiveParameter>>> createSequencesFromPrimitiveParameters(
            Set<String> keyIds,
            Map<String, List<PrimitiveParameter>> primParams,
            Set<String> propositionIds, boolean stateful)
            throws KnowledgeSourceReadException {
        Map<String, List<Sequence<PrimitiveParameter>>> result = new HashMap<String, List<Sequence<PrimitiveParameter>>>();
        if (primParams != null && !primParams.isEmpty()) {
            for (String keyId : keyIds) {
                Map<Set<String>, Sequence<PrimitiveParameter>> seqKey = this.sequences
                        .get(keyId);
                if (seqKey == null) {
                    seqKey = new HashMap<Set<String>, Sequence<PrimitiveParameter>>();
                }
                for (Set<String> paramIds : extractSequenceParamIds(propositionIds)) {
                    if (!seqKey.containsKey(paramIds)) {
                        seqKey.put(paramIds, new Sequence<PrimitiveParameter>(
                                paramIds));
                    }
                }
                if (stateful) {
                    this.sequences.put(keyId, seqKey);
                }

                List<Sequence<PrimitiveParameter>> paramSeqs = new ArrayList<Sequence<PrimitiveParameter>>();
                for (Map.Entry<Set<String>, Sequence<PrimitiveParameter>> entry : seqKey
                        .entrySet()) {
                    Set<String> key = entry.getKey();
                    Sequence<PrimitiveParameter> seq = entry.getValue();
                    if (seq.isEmpty()) {
                        for (PrimitiveParameter parameter : primParams
                                .get(keyId)) {
                            if (key.contains(parameter.getId())) {
                                seq.add(parameter);
                            }
                        }
                    }
                    paramSeqs.add(seq);
                }
                result.put(keyId, paramSeqs);
            }
        }
        return result;
    }

    private void extractSequenceParamIdsHelper(Set<String> propIds,
            Set<Set<String>> sequenceParamIds)
            throws KnowledgeSourceReadException {
        for (String propId : propIds) {
            AbstractionDefinition def = knowledgeSource
                    .readAbstractionDefinition(propId);
            if (def != null) {
                if (def instanceof LowLevelAbstractionDefinition) {
                    sequenceParamIds.add(this.knowledgeSource
                            .primitiveParameterIds(propId));
                } else {
                    extractSequenceParamIdsHelper(def.getAbstractedFrom(),
                            sequenceParamIds);
                }
            } else {
                sequenceParamIds.add(Collections.singleton(propId));
            }
        }
    }

    private Set<Set<String>> extractSequenceParamIds(Set<String> propIds)
            throws KnowledgeSourceReadException {
        Set<Set<String>> result = new HashSet<Set<String>>();
        extractSequenceParamIdsHelper(propIds, result);
        return result;
    }

    /**
     * Gets a stateless rule session, creating one if needed.
     * 
     * @return a <code>StatelessRuleSession</code>.
     */
    private StatelessSession statelessWorkingMemory(Set<String> propIds,
            Map<Proposition, List<Proposition>> derivations)
            throws ProtempaException {
        return constructRuleBase(propIds, null, derivations)
                .newStatelessSession();
    }

    /**
     * Collect all of the propositions for which we need to create rules.
     * 
     * @param algorithms
     *            an empty {@link Map} that will be populated with algorithms
     *            for each proposition definition for which a rule will be
     *            created.
     * @param propDefs
     *            an empty {@link Set} that will be populated with the
     *            proposition definitions for which rules will be created.
     * @param propIds
     *            the proposition id {@link String}s to be found.
     * @throws org.protempa.ProtempaException
     *             if an error occurs reading the algorithm specified by a
     *             proposition definition.
     */
    private void aggregateChildren(ValidateAlgorithmCheckedVisitor visitor,
            Set<PropositionDefinition> propDefs, String[] propIds)
            throws ProtempaException {
        for (String propId : propIds) {
            EventDefinition ed = this.knowledgeSource
                    .readEventDefinition(propId);
            if (ed != null) {
                String[] edDirectChildren = ed.getDirectChildren();
                if (edDirectChildren.length > 0) {
                    propDefs.add(ed);
                    aggregateChildren(visitor, propDefs, edDirectChildren);
                }
            } else {
                AbstractionDefinition ad = this.knowledgeSource
                        .readAbstractionDefinition(propId);
                if (ad != null) {
                    ad.acceptChecked(visitor);
                    propDefs.add(ad);
                    aggregateChildren(visitor, propDefs, ad.getDirectChildren());
                } else {
                    ConstantDefinition cd = this.knowledgeSource
                            .readConstantDefinition(propId);
                    if (cd != null) {
                        cd.acceptChecked(visitor);
                        propDefs.add(cd);
                        aggregateChildren(visitor, propDefs, cd
                                .getDirectChildren());
                    }
                }
            }
        }
    }

    private class ValidateAlgorithmCheckedVisitor extends
            AbstractPropositionDefinitionCheckedVisitor {

        private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;

        ValidateAlgorithmCheckedVisitor() {
            this.algorithms = new HashMap<LowLevelAbstractionDefinition, Algorithm>();
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
        public void visit(ConstantDefinition def) throws ProtempaException {
        }
    }

    private RuleBase constructRuleBase(Set<String> propIds,
            Set<String> oldPropIds,
            Map<Proposition, List<Proposition>> derivations)
            throws ProtempaException {
        ValidateAlgorithmCheckedVisitor visitor = new ValidateAlgorithmCheckedVisitor();

        Set<String> propIdsToFilter = new HashSet<String>();
        if (oldPropIds != null) {
            Set<PropositionDefinition> propDefsToFilter = new HashSet<PropositionDefinition>();
            aggregateChildren(visitor, propDefsToFilter, oldPropIds
                    .toArray(new String[oldPropIds.size()]));
            for (PropositionDefinition propDef : propDefsToFilter) {
                propIdsToFilter.add(propDef.getId());
            }
        }
        JBossRuleCreator ruleCreator = new JBossRuleCreator(visitor
                .getAlgorithms(), knowledgeSource, derivations);
        if (propIds != null) {
            Set<PropositionDefinition> propDefs = new HashSet<PropositionDefinition>();
            aggregateChildren(visitor, propDefs, propIds
                    .toArray(new String[propIds.size()]));
            for (Iterator<PropositionDefinition> itr = propDefs.iterator(); itr
                    .hasNext();) {
                PropositionDefinition def = itr.next();
                if (propIdsToFilter.contains(def.getId())) {
                    itr.remove();
                }
            }
            ruleCreator.visit(propDefs);
        }
        RuleBase ruleBase = new JBossRuleBaseFactory(ruleCreator).newInstance();
        this.clearNeeded = true;
        return ruleBase;
    }

    /**
     * Returns a stateful working memory instance for the given key.
     * 
     * @param key
     *            a key <code>String</code>.
     * @return a <code>StatefulRuleSession</code>, or null if the given key is
     *         <code>null</code>.
     */
    private StatefulSession statefulWorkingMemory(String key,
            Set<String> propIds) throws ProtempaException {
        StatefulSession workingMemory = null;
        if (key != null) {
            if ((workingMemory = this.workingMemoryCache.get(key)) == null) {
                // TODO: change the last null parameter to an actual derivations
                // cache
                workingMemory = constructRuleBase(propIds, null, null)
                        .newStatefulSession(false);
                this.workingMemoryCache.put(key, workingMemory);
                this.propIdCache.put(key, new HashSet<String>());
            } else {
                /*
                 * There apparently is no way to assign an existing working
                 * memory to a knowledge base without serializing it and passing
                 * the serialized object to a new rule base... This could
                 * actually come in handy for transparently saving the working
                 * memories out to disk...
                 */
                try {
                    byte[] wmSerialized = detachWorkingMemory(workingMemory);
                    Set<String> propIdCacheForKey = this.propIdCache.get(key);
                    assert propIdCacheForKey != null : "the proposition id cache was not set";
                    /*
                     * We construct the rule base of proposition definitions
                     * that have not been looked for previously.
                     */
                    // TODO: change the last null parameter to an actual
                    // derivations cache
                    RuleBase ruleBase = constructRuleBase(propIds,
                            propIdCacheForKey, null);
                    if (propIds != null) {
                        propIdCacheForKey.addAll(propIds);
                    }
                    workingMemory = reattachWorkingMemory(wmSerialized,
                            ruleBase);
                    this.workingMemoryCache.put(key, workingMemory);
                } catch (IOException ex) {
                    throw new AssertionError(ex);
                } catch (ClassNotFoundException cnfe) {
                    throw new AssertionError(cnfe);
                }
            }
        }
        return workingMemory;
    }
}
