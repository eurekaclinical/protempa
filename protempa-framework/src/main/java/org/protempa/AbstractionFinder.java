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

import org.drools.FactException;
import org.drools.ObjectFilter;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.PropositionUtil;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

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
    private final AlgorithmSource algorithmSource;
    
    private boolean clearNeeded;
    private final Map<String, Map<Set<String>,
            Sequence<PrimitiveParameter>>> sequences;
    private boolean closed;

    AbstractionFinder(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource,
            boolean cacheFoundAbstractParameters) {
        assert dataSource != null : "dataSource cannot be null";
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        assert algorithmSource != null : "algorithmSource cannot be null";
        this.dataSource = dataSource;
        this.knowledgeSource = knowledgeSource;
        this.algorithmSource = algorithmSource;

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

    Set<String> getKnownKeys() {
        if (workingMemoryCache != null) {
            return Collections.unmodifiableSet(workingMemoryCache.keySet());
        } else {
            return Collections.emptySet();
        }
    }

    /**
     *
     * @param key
     * @param propIds
     * @param minValidDate
     * @param maxValidDate
     * @return
     */
    @SuppressWarnings("unchecked")
    List<Proposition> doFind(String key, Set<String> propIds,
            Long minValidDate, Long maxValidDate) throws FinderException {
        if (this.closed)
            throw new FinderException("Protempa already closed!");
        try {
            if (workingMemoryCache != null)
                return doFindStateful(key, propIds, minValidDate,
                        maxValidDate);
            else
                return doFindStateless(key, propIds, minValidDate,
                        maxValidDate);
        } catch (ProtempaException e) {
            String msg = "An error occurred processing case " + key;
            throw new FinderException(msg, e);
        }
    }
    
    Map<String, List<Proposition>> doFind(Set<String> keys, Set<String> propIds,
    		Long minValidDate, Long maxValidDate) throws FinderException {
    	if (this.closed) {
    		throw new FinderException("Protempa already closed!");
    	}
    	try {
    		if (workingMemoryCache != null) {
    			return doFindStateful(keys, propIds, minValidDate,
    					maxValidDate);
    		} else {
    			return doFindStateless(keys, propIds, minValidDate,
    					maxValidDate);
    		}
    	} catch (ProtempaException e) {
    		String msg = "An error occurred processing a set of cases";
    		throw new FinderException(msg, e);
    	}
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    List<Proposition> doFindIn(Set<String> propositionIds,
            List<? extends Proposition> inPropositions) throws FinderException {
        if (inPropositions != null) {
            try {
                List result = resultList(statelessWorkingMemory(
                        propositionIds).executeWithResults(inPropositions)
                        .iterateObjects(
                        new ProtempaObjectFilter(propositionIds)), null, null);
                if (result.size() > 1) {
                    Collections.sort(result,
                            PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);
                }
                return result;
            } catch (ProtempaException e) {
                String msg = "An error occured in doFindIn";
                throw new FinderException(msg, e);
            }
        }
        return new ArrayList<Proposition>(0);
    }

    /**
     * Clears the working memory cache. Only needs to be called in caching mode.
     */
    public void clear() {
        if (clearNeeded) {
            clearWorkingMemoryCache();
            this.sequences.clear();
            clearNeeded = false;
        }
    }

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
            RuleBase ruleBase)
            throws IOException, ClassNotFoundException {
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
            for (Iterator<StatefulSession> itr =
                    workingMemoryCache.values().iterator(); itr.hasNext();) {
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

        public boolean accept(Object workingMemoryObject) {
            if (paramIds.isEmpty()) {
                return true;
            }
            if (workingMemoryObject instanceof Proposition) {
                return this.paramIds.contains(
                        ((Proposition) workingMemoryObject).getId()) ? true
                        : false;
            } else {
                Sequence<?> s = (Sequence<?>) workingMemoryObject;
                return s.getPropositionIds().size() == 1
                        && this.paramIds.containsAll(s.getPropositionIds())
                        ? true : false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Proposition> doFindStateless(String key,
            Set<String> propositionIds,
            Long minValid, Long maxValid) throws ProtempaException {
        // Long minValidDate = ...
        // Long maxValidDate = ... calculation of what raw data we need to
        // process
        List objects = objectsToAssert(key, propositionIds, null, null, false);
        List<Proposition> result =
                resultList(statelessWorkingMemory(
                propositionIds).executeWithResults(objects).iterateObjects(
                new ProtempaObjectFilter(propositionIds)), minValid, maxValid);
        clear();
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, List<Proposition>> doFindStateless(Set<String> keys,
    		Set<String> propositionIds,
    		Long minValid, Long maxValid) throws ProtempaException {
    	Map<String, List<Proposition>> result = new HashMap<String, List<Proposition>>();
    	for (Map.Entry<String, List<Object>> entry : 
    		objectsToAssert(keys, propositionIds, null, null, false).entrySet()) {
    		List objects = new ArrayList(entry.getValue());
    		List<Proposition> propositions =
    			resultList(statelessWorkingMemory(
    					propositionIds).executeWithResults(objects).iterateObjects(
    							new ProtempaObjectFilter(propositionIds)), minValid, maxValid);
    		result.put(entry.getKey(), propositions);
    	}
    	clear();
    	return result;
    }
    @SuppressWarnings("unchecked")
    private static List<Proposition> resultList(Iterator objects,
            Long minValid, Long maxValid) {
        List<TemporalProposition> temporalProps =
                new ArrayList<TemporalProposition>();
        List<Proposition> result = new ArrayList<Proposition>();
        for (; objects.hasNext();) {
            Object obj = objects.next();
            if (obj instanceof Sequence) {
                temporalProps.addAll((Sequence) obj);
            } else if (obj instanceof TemporalProposition) {
                temporalProps.add((TemporalProposition) obj);
            } else {
                result.add((Proposition) obj);
            }
        }
        Collections.sort(temporalProps,
                PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);

        result.addAll(
                PropositionUtil.getView(temporalProps, minValid, maxValid));
        return result;
    }

    private List<Object> objectsToAssert(String keyId,
            Set<String> propositionIds, Long minValidDate, Long maxValidDate,
            boolean stateful) throws
            DataSourceReadException, KnowledgeSourceReadException {
        // Add events.
        Set<String> eventIds = knowledgeSource.leafEventIds(propositionIds);
        List<Object> objects = new ArrayList<Object>();
        if (!eventIds.isEmpty() || propositionIds == null
                || propositionIds.isEmpty()) {
            objects.addAll(createEvents(dataSource, keyId, eventIds,
                    minValidDate, maxValidDate));
        }
        // Add parameters. Requires special handling, because Sequence objects
        // do not override equals. Can we eliminate the sequence cache?
        objects.addAll(
            createSequencesFromPrimitiveParameters(
                keyId, dataSource.getPrimitiveParametersAsc(keyId,
                knowledgeSource.primitiveParameterIds(propositionIds),
                minValidDate, maxValidDate), propositionIds,
                stateful));

        return objects;
    }
    
    private Map<String, List<Object>> objectsToAssert(Set<String> keyIds,
    		Set<String> propositionIds, Long minValidDate, Long maxValidDate,
    		boolean stateful) throws
    		DataSourceReadException, KnowledgeSourceReadException {
    	// Add events
    	Set<String> eventIds = knowledgeSource.leafEventIds(propositionIds);
    	Map<String, List<Object>> objects = new HashMap<String, List<Object>>();
    	if (!eventIds.isEmpty() || propositionIds == null 
    			|| propositionIds.isEmpty()) {
    		Map<String, List<Event>> tempObjects = 
    		createEvents(dataSource, keyIds, eventIds,
    					minValidDate, maxValidDate);
    		for (Map.Entry<String, List<Event>> entry : tempObjects.entrySet()) {
    			objects.put(entry.getKey(), new ArrayList<Object>(entry.getValue()));
    		}
    	}
    	
    	// Add parameteres. Requires special handling, because Sequence objects
    	// do not override equals. Can we eliminate sequence cache?
    	for (Map.Entry<String, List<Sequence<PrimitiveParameter>>> entry :
    		createSequencesFromPrimitiveParameters(
    				keyIds, dataSource.getPrimitiveParametersAsc(keyIds,
    						knowledgeSource.primitiveParameterIds(propositionIds),
    						minValidDate, maxValidDate), propositionIds,
    						stateful).entrySet()) {
    		objects.put(entry.getKey(), new ArrayList<Object>(entry.getValue()));
    	}
    	return objects;
    }

    @SuppressWarnings("unchecked")
    private List doFindStateful(String keyId, Set<String> propositionIds,
            Long minValidDate, Long maxValidDate) throws ProtempaException {
        if (keyId != null) {
            try {
                StatefulSession workingMemory = statefulWorkingMemory(keyId,
                        propositionIds);
                // Long minValidDate = ...
                // Long maxValidDate = ... calculation of what raw data we need to
                // process
                for (Object obj : objectsToAssert(keyId, propositionIds,
                        null, null, true)) {
                    workingMemory.insert(obj);
                }
                workingMemory.fireAllRules();
                return resultList(workingMemory.iterateObjects(
                        new ProtempaObjectFilter(propositionIds)),
                        minValidDate, maxValidDate);
            } catch (FactException fe) {
                assert false;
            }
        }
        return new ArrayList(0);
    }

    private Map<String, List<Proposition>> doFindStateful(Set<String> keyIds, Set<String> propositionIds,
    		Long minValidDate, Long maxValidDate) throws ProtempaException {
    	if (keyIds != null && !keyIds.isEmpty()) {
    		Map<String, List<Proposition>> results = new HashMap<String, List<Proposition>>();
    		Map<String, List<Object>> objects = objectsToAssert(keyIds, propositionIds, minValidDate, maxValidDate, true);
    		for (Map.Entry<String, List<Object>> entry : objects.entrySet()) {
    			try {
    				StatefulSession workingMemory = statefulWorkingMemory(entry.getKey(), propositionIds);
    				for (Object obj : entry.getValue()) {
    					workingMemory.insert(obj);
    				}
    				workingMemory.fireAllRules();
    				results.put(entry.getKey(), resultList(workingMemory.iterateObjects(
    						new ProtempaObjectFilter(propositionIds)), 
    						minValidDate, maxValidDate));
    			} catch (FactException fe) {
    				assert false;
    			}
    		}
    	}
    	return new HashMap<String, List<Proposition>>();
    }
    
    private List<Event> createEvents(DataSource dataSource,
            String keyId, Set<String> eventIds, Long minValidDate,
            Long maxValidDate) throws
            DataSourceReadException {
        return dataSource.getEventsAsc(keyId, eventIds, minValidDate,
                maxValidDate);
    }
    
    private Map<String, List<Event>> createEvents(DataSource dataSource,
    		Set<String> keyIds, Set<String> eventIds, Long minValidDate,
    		Long maxValidDate) throws DataSourceReadException {
    	return dataSource.getEventsAsc(keyIds, eventIds, minValidDate, maxValidDate);
    }

    /*
     * FIXME The primitive parameters are sorted already, yet we resort them
     * in Sequence.
     */
    private List<Sequence<PrimitiveParameter>>
            createSequencesFromPrimitiveParameters(
            String keyId, List<PrimitiveParameter> primParams,
            Set<String> propositionIds, boolean stateful) {
        ArrayList<Sequence<PrimitiveParameter>> result =
                new ArrayList<Sequence<PrimitiveParameter>>();
        if (primParams != null && !primParams.isEmpty()) {
            Map<Set<String>, Sequence<PrimitiveParameter>> seqKey =
                    this.sequences.get(keyId);
            if (seqKey == null) {
                seqKey = new HashMap<Set<String>,
                        Sequence<PrimitiveParameter>>();
            }
            for (Set<String> paramIds :
                extractSequenceParamIds(propositionIds)) {
                if (!seqKey.containsKey(paramIds)) {
                    seqKey.put(paramIds, new Sequence<PrimitiveParameter>(
                            paramIds));
                }
            }
            if (stateful) {
                this.sequences.put(keyId, seqKey);
            }

            for (Map.Entry<Set<String>, Sequence<PrimitiveParameter>> entry :
                seqKey.entrySet()) {
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
    
    private Map<String, List<Sequence<PrimitiveParameter>>>
    		createSequencesFromPrimitiveParameters(
    		Set<String> keyIds, Map<String, List<PrimitiveParameter>> primParams,
    		Set<String> propositionIds, boolean stateful) {
    	Map<String, List<Sequence<PrimitiveParameter>>> result =
    		new HashMap<String, List<Sequence<PrimitiveParameter>>>();
    	if (primParams != null && !primParams.isEmpty()) {
    		for (String keyId : keyIds) {
    			Map<Set<String>, Sequence<PrimitiveParameter>> seqKey =
    				this.sequences.get(keyId);
    			if (seqKey == null) {
    				seqKey = new HashMap<Set<String>, Sequence<PrimitiveParameter>>();
    			}
    			for (Set<String> paramIds : extractSequenceParamIds(propositionIds)) {
    				if (!seqKey.containsKey(paramIds)) {
    					seqKey.put(paramIds, new Sequence<PrimitiveParameter>(paramIds));
    				}
    			}
    			if (stateful) {
    				this.sequences.put(keyId, seqKey);
    			}
    			
    			List<Sequence<PrimitiveParameter>> paramSeqs =
    				new ArrayList<Sequence<PrimitiveParameter>>();
    			for (Map.Entry<Set<String>, Sequence<PrimitiveParameter>> entry :
    				seqKey.entrySet()) {
    				Set<String> key = entry.getKey();
    				Sequence<PrimitiveParameter> seq = entry.getValue();
    				if (seq.isEmpty()) {
    					for (PrimitiveParameter parameter : primParams.get(keyId)) {
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
            Set<Set<String>> sequenceParamIds) {
        for (String propId : propIds) {
            AbstractionDefinition def =
                    knowledgeSource.readAbstractionDefinition(propId);
            if (def != null) {
                if (def instanceof LowLevelAbstractionDefinition
                        || def instanceof AggregationDefinition) {
                    sequenceParamIds.add(
                            this.knowledgeSource.primitiveParameterIds(propId));
                } else {
                    extractSequenceParamIdsHelper(def.getAbstractedFrom(),
                            sequenceParamIds);
                }
            } else {
                sequenceParamIds.add(Collections.singleton(propId));
            }
        }
    }

    private Set<Set<String>> extractSequenceParamIds(Set<String> propIds) {
        Set<Set<String>> result = new HashSet<Set<String>>();
        extractSequenceParamIdsHelper(propIds, result);
        return result;
    }

    /**
     * Gets a stateless rule session, creating one if needed.
     *
     * @return a <code>StatelessRuleSession</code>.
     */
    private StatelessSession statelessWorkingMemory(Set<String> propIds)
            throws ProtempaException {
        return constructRuleBase(propIds, null).newStatelessSession();
    }

    /**
     * Collect all of the propositions for which we need to create rules.
     * 
     * @param algorithms an empty {@link Map} that will be populated with
     * algorithms for each low-level abstraction definition for which a rule
     * will be created.
     * @param propDefs an empry {@link Set} that will be populated with
     * the proposition definitions for which rules will be created.
     * @param propIds the proposition id {@link String}s to be found.
     * @throws org.protempa.ProtempaException if an error occurs
     * reading the algorithm specified by a low-level abstraction definition.
     */
    private void aggregateChildren(ValidateAlgorithmCheckedVisitor visitor,
            Set<PropositionDefinition> propDefs,
            String[] propIds) throws ProtempaException {
        for (String propId : propIds) {
            EventDefinition ed =
                    this.knowledgeSource.readEventDefinition(propId);
            if (ed != null) {
                String[] edDirectChildren = ed.getDirectChildren();
                if (edDirectChildren.length > 0) {
                    propDefs.add(ed);
                    aggregateChildren(visitor, propDefs, edDirectChildren);
                }
            } else {
                AbstractionDefinition ad =
                        this.knowledgeSource.readAbstractionDefinition(propId);
                if (ad != null) {
                    ad.acceptChecked(visitor);
                    propDefs.add(ad);
                    aggregateChildren(visitor, propDefs,
                            ad.getDirectChildren());
                }
            }
        }
    }

    private class ValidateAlgorithmCheckedVisitor
            extends AbstractPropositionDefinitionCheckedVisitor {

        private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;

        ValidateAlgorithmCheckedVisitor() {
            this.algorithms = new HashMap<LowLevelAbstractionDefinition,
                    Algorithm>();
        }

        Map<LowLevelAbstractionDefinition, Algorithm> getAlgorithms() {
            return this.algorithms;
        }

        @Override
        public void visit(LowLevelAbstractionDefinition
                lowLevelAbstractionDefinition) throws ProtempaException {
            String algorithmId = lowLevelAbstractionDefinition.getAlgorithmId();
            Algorithm algorithm =
                    algorithmSource.readAlgorithm(algorithmId);
            if (algorithm == null && algorithmId != null)
                throw new NoSuchAlgorithmException(
                        "Low level abstraction definition "
                        + lowLevelAbstractionDefinition.getId()
                        + " wants the algorithm "
                        + algorithmId + ", but no such algorithm is available."
                        );
            this.algorithms.put(lowLevelAbstractionDefinition, algorithm);

        }

        @Override
        public void visit(EventDefinition eventDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(HighLevelAbstractionDefinition 
                highLevelAbstractionDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(PrimitiveParameterDefinition 
                primitiveParameterDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(SliceDefinition sliceAbstractionDefinition)
                throws ProtempaException {

        }


    }

    private RuleBase constructRuleBase(Set<String> propIds,
            Set<String> oldPropIds)
            throws ProtempaException {
            ValidateAlgorithmCheckedVisitor visitor =
                    new ValidateAlgorithmCheckedVisitor();
            
            Set<String> propIdsToFilter = new HashSet<String>();
            if (oldPropIds != null) {
                Set<PropositionDefinition> propDefsToFilter =
                        new HashSet<PropositionDefinition>();
                aggregateChildren(visitor, propDefsToFilter,
                        oldPropIds.toArray(new String[oldPropIds.size()]));
                for (PropositionDefinition propDef : propDefsToFilter) {
                    propIdsToFilter.add(propDef.getId());
                }
            }
            JBossRuleCreator ruleCreator = new JBossRuleCreator(
                    visitor.getAlgorithms(), knowledgeSource);
            if (propIds != null) {
                Set<PropositionDefinition> propDefs =
                        new HashSet<PropositionDefinition>();
                aggregateChildren(visitor, propDefs,
                        propIds.toArray(new String[propIds.size()]));
                for (Iterator<PropositionDefinition> itr = propDefs.iterator();
                    itr.hasNext();) {
                    PropositionDefinition def = itr.next();
                    if (propIdsToFilter.contains(def.getId())) {
                        itr.remove();
                    }
                }
                ruleCreator.visit(propDefs);
            }
           RuleBase ruleBase =
                   new JBossRuleBaseFactory(ruleCreator).newInstance();
           this.clearNeeded = true;
           return ruleBase;
    }

    /**
     * Returns a stateful working memory instance for the given key.
     *
     * @param key
     *            a key <code>String</code>.
     * @return a <code>StatefulRuleSession</code>, or null if the given key
     *         is <code>null</code>.
     */
    private StatefulSession statefulWorkingMemory(String key,
            Set<String> propIds) throws ProtempaException {
        StatefulSession workingMemory = null;
        if (key != null) {
            if ((workingMemory = this.workingMemoryCache.get(key)) == null) {
                workingMemory = constructRuleBase(propIds, null)
                        .newStatefulSession(false);
                this.workingMemoryCache.put(key, workingMemory);
                this.propIdCache.put(key, new HashSet<String>());
            } else {
                /*
                 * There apparently is no way to assign an existing working
                 * memory to a knowledge base without serializing it and
                 * passing the serialized object to a new rule base...
                 * This could actually come in handy for transparently
                 * saving the working memories out to disk...
                 */
                try {
                    byte[] wmSerialized = detachWorkingMemory(workingMemory);
                    Set<String> propIdCacheForKey = this.propIdCache.get(key);
                    assert propIdCacheForKey != null :
                        "the proposition id cache was not set";
                    /*
                     * We construct the rule base of proposition definitions
                     * that have not been looked for previously.
                     */
                    RuleBase ruleBase = constructRuleBase(propIds,
                            propIdCacheForKey);
                    if (propIds != null)
                        propIdCacheForKey.addAll(propIds);
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
