package org.protempa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.BackendNewInstanceException;


/**
 * A read-only "interface" to an externally-maintained knowledge base. The user
 * must specify a backend in the constructor from where information about
 * primitive parameters and abstract parameters can be obtained.
 * 
 * @author Andrew Post
 */
public final class KnowledgeSource
        extends AbstractSource<KnowledgeSourceUpdatedEvent, KnowledgeSourceBackendUpdatedEvent> {

    /**
     * PROTEMPA knowledge object model.
     */
    private KnowledgeBase protempaKnowledgeBase;
    private final BackendManager<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource, KnowledgeSourceBackend> backendManager;
    private final Map<Set<String>, Set<String>> leafEventIdCache;
    private final Map<Set<String>, Set<String>> leafConstantIdCache;
    private final Map<Set<String>, Set<String>> primParamIdCache;
    private final Set<String> notFoundAbstractionDefinitionRequests;
    private final Set<String> notFoundEventDefinitionRequests;
    private final Set<String> notFoundPrimitiveParameterDefinitionRequests;
    private final Set<String> notFoundConstantDefinitionRequests;

    public KnowledgeSource(KnowledgeSourceBackend[] backends) {
        super(backends);
        this.backendManager = new BackendManager<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource, KnowledgeSourceBackend>(
                this, backends);
        this.leafEventIdCache = new HashMap<Set<String>, Set<String>>();
        this.leafConstantIdCache = new HashMap<Set<String>, Set<String>>();
        this.primParamIdCache = new HashMap<Set<String>, Set<String>>();
        this.notFoundAbstractionDefinitionRequests = new HashSet<String>();
        this.notFoundEventDefinitionRequests = new HashSet<String>();
        this.notFoundPrimitiveParameterDefinitionRequests = new HashSet<String>();
        this.notFoundConstantDefinitionRequests = new HashSet<String>();
    }

    /**
     * Connect to the knowledge source backend(s).
     */
    private void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
        if (isClosed()) {
            throw new IllegalStateException("Knowledge source already closed!");
        }
        this.backendManager.initializeIfNeeded();
        if (this.backendManager.getBackends() != null
                && this.protempaKnowledgeBase == null) {
            this.protempaKnowledgeBase = new KnowledgeBase();
        }
    }

    /**
     * Returns the specified constant definition.
     * 
     * @param id
     *            an constant definition id {@link String}.
     * @return an {@link ConstantDefinition}, or <code>null</code> if none was
     *         found with the given <code>id</code>.
     */
    public ConstantDefinition readConstantDefinition(String id)
            throws KnowledgeSourceReadException {
        ConstantDefinition result = null;
        if (!this.notFoundConstantDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getConstantDefinition(id);
            }
            if (result == null
                    && !this.notFoundConstantDefinitionRequests.contains(id)) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(ex);
                }
                if (this.backendManager.getBackends() != null) {
                    for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                        result = backend.readConstantDefinition(id,
                                protempaKnowledgeBase);
                        if (result != null) {
                            return result;
                        }
                    }
                    this.notFoundConstantDefinitionRequests.add(id);
                }
            }
        }

        return result;
    }

    public boolean hasConstantDefinition(String id)
            throws KnowledgeSourceReadException {
        boolean result = false;
        if (!this.notFoundConstantDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getConstantDefinition(id) != null;
            }
            if (!result
                    && !this.notFoundConstantDefinitionRequests.contains(id)) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(ex);
                }
                if (this.backendManager.getBackends() != null) {
                    for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                        result = backend.hasConstantDefinition(id,
                                protempaKnowledgeBase);
                        if (result) {
                            return result;
                        }
                    }
                    this.notFoundConstantDefinitionRequests.add(id);
                }
            }
        }

        return result;
    }

    /**
     * Returns the specified event definition.
     * 
     * @param id
     *            an event definition id {@link String}.
     * @return an {@link EventDefinition}, or <code>null</code> if none was
     *         found with the given <code>id</code>.
     */
    public EventDefinition readEventDefinition(String id)
            throws KnowledgeSourceReadException {
        EventDefinition result = null;
        if (!this.notFoundEventDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getEventDefinition(id);
            }
            if (result == null
                    && !this.notFoundEventDefinitionRequests.contains(id)) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(ex);
                }
                if (this.backendManager.getBackends() != null) {
                    for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                        result = backend.readEventDefinition(id,
                                protempaKnowledgeBase);
                        if (result != null) {
                            return result;
                        }
                    }
                    this.notFoundEventDefinitionRequests.add(id);
                }
            }
        }

        return result;
    }

    public boolean hasEventDefinition(String id)
            throws KnowledgeSourceReadException {
        boolean result = false;
        if (!this.notFoundEventDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getEventDefinition(id) != null;
            }
            if (!result && !this.notFoundEventDefinitionRequests.contains(id)) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(ex);
                }
                if (this.backendManager.getBackends() != null) {
                    for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                        result = backend.hasEventDefinition(id,
                                protempaKnowledgeBase);
                        if (result) {
                            return result;
                        }
                    }
                    this.notFoundEventDefinitionRequests.add(id);
                }
            }
        }

        return result;
    }

    /**
     * Returns the specified proposition definition.
     * 
     * @param id
     *            a proposition definition id {@link String}.
     * @return a {@link PropositionDefinition}, or <code>null</code> if none was
     *         found with the given <code>id</code>.
     */
    public PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        PropositionDefinition result = readTemporalPropositionDefinition(id);
        if (result == null) {
            result = readConstantDefinition(id);
        }

        return result;
    }

    public TemporalPropositionDefinition readTemporalPropositionDefinition(
            String propId) throws KnowledgeSourceReadException {
        TemporalPropositionDefinition result =
                readAbstractionDefinition(propId);
        if (result == null) {
            result = readPrimitiveParameterDefinition(propId);
            if (result == null) {
                result = readEventDefinition(propId);
            }
        }
        return result;
    }

    public boolean hasPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return hasTemporalPropositionDefinition(id)
                || hasConstantDefinition(id);
    }

    public boolean hasTemporalPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return hasPrimitiveParameterDefinition(id)
                || hasEventDefinition(id)
                || hasAbstractionDefinition(id);
    }

    /**
     * Read the primitive parameter definition with the given id.
     * 
     * @param id
     *            a primitive parameter definition id <code>String</code>.
     * @return a {@link PrimitiveParameterDefinition} object, or
     *         <code>null</code> if none was found with the given
     *         <code>id</code>.
     */
    public PrimitiveParameterDefinition readPrimitiveParameterDefinition(
            String id) throws KnowledgeSourceReadException {
        PrimitiveParameterDefinition result = null;
        if (!this.notFoundPrimitiveParameterDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getPrimitiveParameterDefinition(id);
            }

            if (result == null) {
                try {
                    initializeIfNeeded();
                    if (this.backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                            result = backend.readPrimitiveParameterDefinition(
                                    id, protempaKnowledgeBase);
                            if (result != null) {
                                return result;
                            }
                        }
                        this.notFoundPrimitiveParameterDefinitionRequests.add(id);
                    }
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the primitive parameter definitions.",
                            ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the primitive parameter definitions.",
                            ex);
                }
            }
        }

        return result;
    }

    public boolean hasPrimitiveParameterDefinition(String id)
            throws KnowledgeSourceReadException {
        boolean result = false;
        if (!this.notFoundPrimitiveParameterDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getPrimitiveParameterDefinition(id) != null;
            }

            if (!result) {
                try {
                    initializeIfNeeded();
                    if (this.backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                            result = backend.hasPrimitiveParameterDefinition(
                                    id, protempaKnowledgeBase);
                            if (result) {
                                return result;
                            }
                        }
                        this.notFoundPrimitiveParameterDefinitionRequests.add(id);
                    }
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the primitive parameter definitions.",
                            ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the primitive parameter definitions.",
                            ex);
                }
            }
        }

        return result;
    }

    /**
     * Read the abstraction definition with the given id.
     * 
     * @param id
     *            an abstraction definition id.
     * @return an {@link AbstractionDefinition} object, or <code>null</code> if
     *         none was found with the given <code>id</code>.
     */
    public AbstractionDefinition readAbstractionDefinition(String id)
            throws KnowledgeSourceReadException {
        AbstractionDefinition result = null;
        if (!this.notFoundAbstractionDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getAbstractionDefinition(id);
            }
            if (result == null
                    && !this.notFoundAbstractionDefinitionRequests.contains(id)) {
                try {
                    initializeIfNeeded();
                    if (this.backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                            result = backend.readAbstractionDefinition(id,
                                    protempaKnowledgeBase);
                            if (result != null) {
                                return result;
                            }
                        }
                        this.notFoundAbstractionDefinitionRequests.add(id);
                    }
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the abstraction definitions.",
                            ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the abstraction definitions.",
                            ex);
                }
            }
        }
        return result;
    }

    public boolean hasAbstractionDefinition(String id)
            throws KnowledgeSourceReadException {
        boolean result = false;
        if (!this.notFoundAbstractionDefinitionRequests.contains(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getAbstractionDefinition(id) != null;
            }
            if (!result
                    && !this.notFoundAbstractionDefinitionRequests.contains(id)) {
                try {
                    initializeIfNeeded();
                    if (this.backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                            result = backend.hasAbstractionDefinition(id,
                                    protempaKnowledgeBase);
                            if (result) {
                                return result;
                            }
                        }
                        this.notFoundAbstractionDefinitionRequests.add(id);
                    }
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the abstraction definitions.",
                            ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred reading the abstraction definitions.",
                            ex);
                }
            }
        }
        return result;
    }

    @Override
    public void close() {
        clear();
        this.backendManager.close();
        this.protempaKnowledgeBase = null;
        super.close();
    }

    @Override
    public void clear() {
        if (this.protempaKnowledgeBase != null) {
            this.protempaKnowledgeBase.clear();
            this.leafEventIdCache.clear();
            this.primParamIdCache.clear();
            this.leafConstantIdCache.clear();
            this.notFoundAbstractionDefinitionRequests.clear();
            this.notFoundEventDefinitionRequests.clear();
            this.notFoundPrimitiveParameterDefinitionRequests.clear();
            this.notFoundConstantDefinitionRequests.clear();
        }
    }

    public Set<String> leafPropositionIds(String propId)
            throws KnowledgeSourceReadException {
        return leafPropositionIds(Collections.singleton(propId));
    }

    public Set<String> leafPropositionIds(Set<String> propIds)
            throws KnowledgeSourceReadException {
        Set<String> pIds = new HashSet<String>(primitiveParameterIds(propIds));
        Set<String> eventIds = leafEventIds(propIds);
        pIds.addAll(eventIds);
        Set<String> constantIds = leafConstantIds(propIds);
        pIds.addAll(constantIds);

        return pIds;
    }

    /**
     * Returns the set of primitive parameter ids needed to find the given
     * propositions. If a primitive parameter id is passed in, it is included in
     * the returned set.
     * 
     * @param propIds
     *            a <code>Set</code> of proposition id <code>String</code>s.
     *            Cannot be <code>null</code>.
     * @return an unmodifiable <code>Set</code> of primitive parameter id
     *         <code>String</code>s. Guaranteed not to return <code>null</code>.
     */
    public Set<String> primitiveParameterIds(Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        Set<String> cachedResult = this.primParamIdCache.get(propIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<String> result = new HashSet<String>();
            if (propIds != null) {
                primitiveParameterIdsHelper(propIds.toArray(new String[propIds.size()]), result);
                result = Collections.unmodifiableSet(result);
                this.primParamIdCache.put(propIds, result);
            }
            return result;
        }
    }

    /**
     * Helper method for finding primitive parameter ids.
     * 
     * @param paramIds
     *            a {@link Set} of proposition id {@link String}s, must not be
     *            <code>null</code>.
     * @param result
     *            a {@link Set} for storing the primitive parameter ids to
     *            return, must not be <code>null</code>.
     */
    private void primitiveParameterIdsHelper(String[] paramIds,
            Set<String> result) throws KnowledgeSourceReadException {
        for (String paramId : paramIds) {
            PrimitiveParameterDefinition primParamDef = readPrimitiveParameterDefinition(paramId);
            if (primParamDef != null) {
                String[] primParamDefInverseIsA = primParamDef.getInverseIsA();
                if (primParamDefInverseIsA == null
                        || primParamDefInverseIsA.length == 0) {
                    result.add(paramId);
                } else {
                    primitiveParameterIdsHelper(primParamDefInverseIsA, result);
                }
            } else {
                AbstractionDefinition def = readAbstractionDefinition(paramId);
                if (def != null) {
                    primitiveParameterIdsHelper(def.getInverseIsA(), result);
                    Set<String> abstractedFrom = def.getAbstractedFrom();
                    primitiveParameterIdsHelper(abstractedFrom.toArray(new String[abstractedFrom.size()]), result);
                } else {
                    if (readEventDefinition(paramId) == null
                            && readConstantDefinition(paramId) == null) {
                        throw new KnowledgeSourceReadException(paramId
                                + " is unknown");
                    }
                }
            }
        }
    }

    /**
     * Returns the set of primitive parameter ids needed to find instances of
     * the given proposition.
     * 
     * @param propId
     *            an abstraction id <code>String</code>. Cannot be
     *            <code>null</code>.
     * @return a newly-created {@link Set} of primitive parameter id
     *         {@link String}s. Guaranteed not to return <code>null</code>.
     */
    public Set<String> primitiveParameterIds(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        return primitiveParameterIds(Collections.singleton(propId));
    }

    /**
     * Given a set of abstraction and event ids, this method navigates the event
     * is-a hierarchy and collects and returns the set of event ids for
     * retrieval from the data source (e.g., the events at the leaves of the
     * tree).
     * 
     * @param abstractionAndEventIds
     *            a <code>Set</code> of abstraction and event id
     *            <code>String</code>s. Cannot be <code>null</code>.
     * @return a newly-created unmodifiable <code>Set</code> of event id
     *         <code>String</code>s. Guaranteed not to return <code>null</code>.
     */
    public Set<String> leafEventIds(Set<String> abstractionAndEventIds)
            throws KnowledgeSourceReadException {
        if (abstractionAndEventIds == null) {
            throw new IllegalArgumentException(
                    "abstractionAndEventIds cannot be null");
        }
        Set<String> cachedResult = this.leafEventIdCache.get(abstractionAndEventIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<String> result = new HashSet<String>();
            if (abstractionAndEventIds != null) {
                leafEventIdsHelper(abstractionAndEventIds.toArray(new String[abstractionAndEventIds.size()]),
                        result);
                result = Collections.unmodifiableSet(result);
                this.leafEventIdCache.put(abstractionAndEventIds, result);
            }
            return result;
        }
    }

    /**
     * Given an abstraction or event id, this method navigates the event is-a
     * hierarchy and collects and returns the set of event ids for retrieval
     * from the data source (e.g., the events at the leaves of the tree).
     * 
     * @param abstractionOrEventId
     *            an abstraction or event id <code>String</code>.
     * @return a newly-created unmodifiable <code>Set</code> of event id
     *         <code>String</code>s. Guaranteed not to return <code>null</code>.
     */
    public Set<String> leafEventIds(String abstractionOrEventId)
            throws KnowledgeSourceReadException {
        if (abstractionOrEventId == null) {
            throw new IllegalArgumentException(
                    "abstractionOrEventId cannot be null");
        }
        return leafEventIds(Collections.singleton(abstractionOrEventId));
    }

    /**
     * Actually gets the leaf event ids. This exists so that we can recurse
     * through the is-a hierarchy and aggregate the results in one set.
     * 
     * @param abstractionAndEventIds
     *            a <code>Set</code> of abstraction and event id
     *            <code>String</code>s.
     * @param result
     *            a non-<code>null</code> <code>Set</code> in which to aggregate
     *            leaf event ids.
     */
    private void leafEventIdsHelper(String[] abstractionAndEventIds,
            Set<String> result) throws KnowledgeSourceReadException {
        if (abstractionAndEventIds != null) {
            for (String abstractParameterOrEventId : abstractionAndEventIds) {
                EventDefinition eventDef =
                        this.readEventDefinition(abstractParameterOrEventId);
                if (eventDef != null) {
                    String[] inverseIsA = eventDef.getInverseIsA();
                    if (inverseIsA.length == 0) {
                        result.add(eventDef.getId());
                    } else {
                        leafEventIdsHelper(inverseIsA, result);
                    }
                } else {
                    AbstractionDefinition apDef =
                        readAbstractionDefinition(abstractParameterOrEventId);
                    if (apDef != null) {
                        Set<String> af = apDef.getAbstractedFrom();
                        leafEventIdsHelper(af.toArray(new String[af.size()]),
                                result);
                    } else {
                        ConstantDefinition constantDef =
                                readConstantDefinition(
                                abstractParameterOrEventId);
                        if (constantDef == null) {
                            throw new KnowledgeSourceReadException(
                                    "The proposition definition '"
                                    + abstractParameterOrEventId
                                    + "' is unknown");
                        }
                    }
                }
            }
        }
    }

    /**
     * Given a set of constant ids, this method navigates the event is-a
     * hierarchy and collects and returns the set of constant ids for retrieval
     * from the data source (e.g., the events at the leaves of the tree).
     * 
     * @param constantIds
     *            a <code>Set</code> of constant id <code>String</code>s. Cannot
     *            be <code>null</code>.
     * @return a newly-created unmodifiable <code>Set</code> of constant id
     *         <code>String</code>s. Guaranteed not to return <code>null</code>.
     */
    public Set<String> leafConstantIds(Set<String> constantIds)
            throws KnowledgeSourceReadException {
        if (constantIds == null) {
            throw new IllegalArgumentException("constantIds cannot be null");
        }
        Set<String> cachedResult = this.leafConstantIdCache.get(constantIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<String> result = new HashSet<String>();
            if (constantIds != null) {
                leafConstantIdsHelper(constantIds.toArray(
                        new String[constantIds.size()]), result);
                result = Collections.unmodifiableSet(result);
                this.leafConstantIdCache.put(constantIds, result);
            }
            return result;
        }
    }

    /**
     * Given a constant id, this method navigates the constant is-a hierarchy
     * and collects and returns the set of constant ids for retrieval from the
     * data source (e.g., the constants at the leaves of the tree).
     * 
     * @param constantId
     *            a constant id <code>String</code>.
     * @return a newly-created unmodifiable <code>Set</code> of constant id
     *         <code>String</code>s. Guaranteed not to return <code>null</code>.
     */
    public Set<String> leafConstantIds(String constantId)
            throws KnowledgeSourceReadException {
        if (constantId == null) {
            throw new IllegalArgumentException("constant cannot be null");
        }
        return leafConstantIds(Collections.singleton(constantId));
    }

    /**
     * Actually gets the leaf constant ids. This exists so that we can recurse
     * through the is-a hierarchy and aggregate the results in one set.
     * 
     * @param constantIds
     *            a <code>Set</code> of constant id <code>String</code>s.
     * @param result
     *            a non-<code>null</code> <code>Set</code> in which to aggregate
     *            leaf constant ids.
     */
    private void leafConstantIdsHelper(String[] constantIds, Set<String> result)
            throws KnowledgeSourceReadException {
        if (constantIds != null) {
            for (String constantId : constantIds) {
                ConstantDefinition constantDef = readConstantDefinition(constantId);
                if (constantDef != null) {
                    String[] inverseIsA = constantDef.getInverseIsA();
                    if (inverseIsA.length == 0) {
                        result.add(constantDef.getId());
                    } else {
                        leafConstantIdsHelper(inverseIsA, result);
                    }
                } else {
                    if (!hasTemporalPropositionDefinition(constantId)) {
                        throw new KnowledgeSourceReadException(
                                "The proposition definition '"
                                + constantId + "' is unknown");
                    }
                }
            }
        }
    }

    /**
     * Gets the mappings from term IDs to proposition IDs for each backend.
     * 
     * @return a {@link Map} of {@link String}s to a {@link List} of
     *         <code>String</code>s, with the keys being {@link Term} IDs and
     *         the values being lists of {@link PropositionDefinition} IDs.
     */
    public List<String> getPropositionDefinitionsByTerm(String termId)
            throws KnowledgeSourceReadException {
        List<String> result = new ArrayList<String>();
        try {
            initializeIfNeeded();
        } catch (BackendInitializationException ex) {
            throw new KnowledgeSourceReadException(ex);
        } catch (BackendNewInstanceException ex) {
            throw new KnowledgeSourceReadException(ex);
        }
        for (KnowledgeSourceBackend backend : backendManager.getBackends()) {
            result.addAll(backend.getPropositionDefinitionsByTerm(termId));
        }

        return result;
    }

    @Override
    public void backendUpdated(KnowledgeSourceBackendUpdatedEvent event) {
        clear();
        fireKnowledgeSourceUpdated();
    }

    /**
     * Notifies registered listeners that the knowledge source has been updated.
     * 
     * @see KnowledgeSourceUpdatedEvent
     * @see SourceListener
     */
    private void fireKnowledgeSourceUpdated() {
        fireSourceUpdated(new KnowledgeSourceUpdatedEvent(this));
    }
}
