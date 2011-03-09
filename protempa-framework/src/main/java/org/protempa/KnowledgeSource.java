package org.protempa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.commons.collections.map.ReferenceMap;
import org.arp.javautil.arrays.Arrays;

import org.protempa.backend.BackendNewInstanceException;
import org.protempa.proposition.value.ValueSet;
import org.protempa.query.And;

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
    private final Map<Set<String>, Set<String>> propIdCache;
    private final Map<Set<String>, Set<PropositionDefinition>> propIdPropCache;
    private final Map<String, Object> notFoundPrimitiveParameterDefinitionRequests;
    private final Map<String, Object> notFoundEventDefinitionRequests;
    private final Map<String, Object> notFoundConstantDefinitionRequests;
    private final Map<String, Object> notFoundAbstractionDefinitionRequests;
    private final Map<String, Object> notFoundValueSetRequests;
    private final Map<String, Object> notFoundPropositionDefinitionRequests;
    private final PropositionDefinitionReader propDefReader;
    private final ConstantDefinitionReader constantDefReader;
    private final EventDefinitionReader eventDefReader;
    private final PrimitiveParameterDefinitionReader primParamDefReader;
    private final AbstractionDefinitionReader abstractionDefReader;
    private final Map<PropositionDefinition, List<PropositionDefinition>> inverseIsACache;
    private final Map<PropositionDefinition, List<PropositionDefinition>> abstractedFromCache;

    @SuppressWarnings("unchecked")
    public KnowledgeSource(KnowledgeSourceBackend[] backends) {
        super(backends);
        this.backendManager = new BackendManager<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource, KnowledgeSourceBackend>(
                this, backends);
        this.propIdCache = new ReferenceMap();
        this.propIdPropCache = new ReferenceMap();
        this.inverseIsACache = new ReferenceMap();
        this.abstractedFromCache = new ReferenceMap();
        this.notFoundPrimitiveParameterDefinitionRequests = new WeakHashMap<String, Object>();
        this.notFoundAbstractionDefinitionRequests = new WeakHashMap<String, Object>();
        this.notFoundConstantDefinitionRequests = new WeakHashMap<String, Object>();
        this.notFoundEventDefinitionRequests = new WeakHashMap<String, Object>();
        this.notFoundValueSetRequests = new WeakHashMap<String, Object>();
        this.notFoundPropositionDefinitionRequests = new WeakHashMap<String, Object>();

        this.propDefReader = new PropositionDefinitionReader();
        this.constantDefReader = new ConstantDefinitionReader();
        this.eventDefReader = new EventDefinitionReader();
        this.primParamDefReader = new PrimitiveParameterDefinitionReader();
        this.abstractionDefReader = new AbstractionDefinitionReader();
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
        return this.constantDefReader.read(id);
    }

    public boolean hasConstantDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.constantDefReader.has(id);
    }

    public List<PropositionDefinition> readInverseIsA(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        List<PropositionDefinition> result = this.inverseIsACache.get(propDef);
        if (result != null) {
            return result;
        } else {
            result = new ArrayList<PropositionDefinition>();
            if (propDef != null) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(ex);
                }
                if (this.backendManager.getBackends() != null) {
                    for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                        result.addAll(backend.readInverseIsA(propDef,
                                protempaKnowledgeBase));
                    }
                }
            }
            result = Collections.unmodifiableList(result);
            if (propDef != null) {
                this.inverseIsACache.put(propDef, result);
            }
            return result;
        }
    }

    public List<PropositionDefinition> readInverseIsA(String id)
            throws KnowledgeSourceReadException {
        PropositionDefinition propDef = readPropositionDefinition(id);
        return readInverseIsA(propDef);
    }

    public List<PropositionDefinition> readAbstractedFrom(AbstractionDefinition propDef)
            throws KnowledgeSourceReadException {
        List<PropositionDefinition> result =
                this.abstractedFromCache.get(propDef);
        if (result != null) {
            return result;
        } else {
            result = new ArrayList<PropositionDefinition>();
            if (propDef != null) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(ex);
                }
                if (this.backendManager.getBackends() != null) {
                    for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                        result.addAll(backend.readAbstractedFrom(propDef,
                                protempaKnowledgeBase));
                    }
                }
            }
            result = Collections.unmodifiableList(result);
            if (propDef != null) {
                this.abstractedFromCache.put(propDef, result);
            }
            return result;
        }
    }

    public List<PropositionDefinition> readAbstractedFrom(String id)
            throws KnowledgeSourceReadException {
        AbstractionDefinition propDef = readAbstractionDefinition(id);
        return readAbstractedFrom(propDef);
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
        return eventDefReader.read(id);
    }

    public boolean hasEventDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.eventDefReader.has(id);
    }

    private abstract class AbstractPropositionDefinitionReader<P extends PropositionDefinition> {

        P read(String id) throws KnowledgeSourceReadException {
            P result = null;
            if (!isInNotFound(id)) {
                if (protempaKnowledgeBase != null) {
                    result = readFromKnowledgeBase(id);
                }

                if (result == null) {
                    try {
                        initializeIfNeeded();
                        if (backendManager.getBackends() != null) {
                            for (KnowledgeSourceBackend backend : backendManager.getBackends()) {
                                result = readFromBackend(id, backend);
                                if (result != null) {
                                    return result;
                                }
                            }
                            putInNotFound(id);
                        }
                    } catch (BackendInitializationException ex) {
                        throw new KnowledgeSourceReadException(
                                "An error occurred reading the proposition definition" + id,
                                ex);
                    } catch (BackendNewInstanceException ex) {
                        throw new KnowledgeSourceReadException(
                                "An error occurred reading the proposition definition" + id,
                                ex);
                    }
                }
            }

            return result;
        }

        boolean has(String id) throws KnowledgeSourceReadException {
            boolean result = false;
            if (!isInNotFound(id)) {
                if (protempaKnowledgeBase != null) {
                    if (readFromKnowledgeBase(id) != null) {
                        result = true;
                    }
                }

                if (!result) {
                    try {
                        initializeIfNeeded();
                        if (backendManager.getBackends() != null) {
                            for (KnowledgeSourceBackend backend : backendManager.getBackends()) {
                                result = readFromBackend(id, backend) != null;
                                if (result) {
                                    return result;
                                }
                            }
                            putInNotFound(id);
                        }
                    } catch (BackendInitializationException ex) {
                        throw new KnowledgeSourceReadException(
                                "An error occurred reading the proposition definition" + id,
                                ex);
                    } catch (BackendNewInstanceException ex) {
                        throw new KnowledgeSourceReadException(
                                "An error occurred reading the proposition definition" + id,
                                ex);
                    }
                }
            }

            return result;
        }

        protected abstract boolean isInNotFound(String id);

        protected abstract void putInNotFound(String id);

        protected abstract P readFromKnowledgeBase(String id);

        protected abstract P readFromBackend(String id, KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException;
    }

    private final class PropositionDefinitionReader
            extends AbstractPropositionDefinitionReader<PropositionDefinition> {

        @Override
        protected PropositionDefinition readFromKnowledgeBase(String id) {
            return protempaKnowledgeBase.getPropositionDefinition(id);
        }

        @Override
        protected PropositionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readPropositionDefinition(
                    id, protempaKnowledgeBase);
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundPropositionDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundPropositionDefinitionRequests.put(id, null);
        }
    }

    private final class EventDefinitionReader
            extends AbstractPropositionDefinitionReader<EventDefinition> {

        @Override
        protected EventDefinition readFromKnowledgeBase(String id) {
            return protempaKnowledgeBase.getEventDefinition(id);
        }

        @Override
        protected EventDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readEventDefinition(
                    id, protempaKnowledgeBase);
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundEventDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundEventDefinitionRequests.put(id, null);
        }
    }

    private final class ConstantDefinitionReader
            extends AbstractPropositionDefinitionReader<ConstantDefinition> {

        @Override
        protected ConstantDefinition readFromKnowledgeBase(String id) {
            return protempaKnowledgeBase.getConstantDefinition(id);
        }

        @Override
        protected ConstantDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readConstantDefinition(
                    id, protempaKnowledgeBase);
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundConstantDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundConstantDefinitionRequests.put(id, null);
        }
    }

    private final class PrimitiveParameterDefinitionReader
            extends AbstractPropositionDefinitionReader<PrimitiveParameterDefinition> {

        @Override
        protected PrimitiveParameterDefinition readFromKnowledgeBase(String id) {
            return protempaKnowledgeBase.getPrimitiveParameterDefinition(id);
        }

        @Override
        protected PrimitiveParameterDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readPrimitiveParameterDefinition(
                    id, protempaKnowledgeBase);
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundPrimitiveParameterDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundPrimitiveParameterDefinitionRequests.put(id, null);
        }
    }

    private final class AbstractionDefinitionReader
            extends AbstractPropositionDefinitionReader<AbstractionDefinition> {

        @Override
        protected AbstractionDefinition readFromKnowledgeBase(String id) {
            return protempaKnowledgeBase.getAbstractionDefinition(id);
        }

        @Override
        protected AbstractionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readAbstractionDefinition(
                    id, protempaKnowledgeBase);
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundAbstractionDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundAbstractionDefinitionRequests.put(id, null);
        }
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
        return this.propDefReader.read(id);
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
        return hasPrimitiveParameterDefinition(id) || hasEventDefinition(id)
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
        return this.primParamDefReader.read(id);
    }

    public boolean hasPrimitiveParameterDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.primParamDefReader.has(id);
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
        return this.abstractionDefReader.read(id);
    }

    public boolean hasAbstractionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.abstractionDefReader.has(id);
    }

    public ValueSet readValueSet(String id) throws KnowledgeSourceReadException {
        ValueSet result = null;
        if (!this.notFoundValueSetRequests.containsKey(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getValueSet(id);
            }
            if (result == null) {
                try {
                    initializeIfNeeded();
                    if (this.backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                            result = backend.readValueSet(id,
                                    protempaKnowledgeBase);
                            if (result != null) {
                                return result;
                            }
                        }
                        this.notFoundValueSetRequests.put(id, null);
                    }
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred while reading the value set.",
                            ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred while reading the value set.",
                            ex);
                }
            }
        }
        return result;
    }

    public boolean hasValueSet(String id) throws KnowledgeSourceReadException {
        boolean result = false;
        if (!this.notFoundValueSetRequests.containsKey(id)) {
            if (protempaKnowledgeBase != null) {
                result = protempaKnowledgeBase.getValueSet(id) != null;
            }
            if (!result) {
                try {
                    initializeIfNeeded();
                    if (this.backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : this.backendManager.getBackends()) {
                            result = backend.hasValueSet(id,
                                    protempaKnowledgeBase);
                            if (result) {
                                return result;
                            }
                        }
                        this.notFoundValueSetRequests.put(id, null);
                    }
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred while reading the value set.",
                            ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(
                            "An error occurred while reading the value set.",
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
            this.propIdCache.clear();
            this.propIdPropCache.clear();
            this.inverseIsACache.clear();
            this.abstractedFromCache.clear();
            this.notFoundPrimitiveParameterDefinitionRequests.clear();
            this.notFoundAbstractionDefinitionRequests.clear();
            this.notFoundConstantDefinitionRequests.clear();
            this.notFoundEventDefinitionRequests.clear();
            this.notFoundValueSetRequests.clear();
            this.notFoundPropositionDefinitionRequests.clear();

        }
    }

    /**
     * Returns the set of proposition ids needed to find instances of
     * the given proposition.
     *
     * @param propId
     *            one or multiple proposition id <code>String</code>s.
     *            Cannot be <code>null</code>.
     * @return a newly-created {@link Set} of proposition id
     *         {@link String}s. Guaranteed not to return <code>null</code>.
     */
    public Set<String> leafPropositionIds(String... propId)
            throws KnowledgeSourceReadException {
        Set<String> propIds = Arrays.asSet(propId);
        return leafPropositionIds(propIds);
    }

    public Set<String> leafPropositionIds(Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain null");
        }
        return leafPropositionIds(new HashSet<String>(propIds),
                this.propIdCache);
    }

    public Set<PropositionDefinition> leafPropositionDefinitions(
            Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain null");
        }
        return leafPropositionDefinitions(new HashSet<String>(propIds),
                this.propIdPropCache);
    }

    private Set<String> leafPropositionIds(Set<String> propIds,
            Map<Set<String>, Set<String>> cache)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain a null element");
        }

        Set<String> cachedResult = cache.get(propIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<String> result = new HashSet<String>();
            if (propIds != null) {
                leafPropositionIdsHelper(propIds, result, null);
                result = Collections.unmodifiableSet(result);
                cache.put(propIds, result);
            }
            return result;
        }
    }

    private Set<PropositionDefinition> leafPropositionDefinitions(Set<String> propIds,
            Map<Set<String>, Set<PropositionDefinition>> cache)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain a null element");
        }

        Set<PropositionDefinition> cachedResult = cache.get(propIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<PropositionDefinition> propResult = new HashSet<PropositionDefinition>();
            if (propIds != null) {
                leafPropositionIdsHelper(propIds, null, propResult);
                propResult = Collections.unmodifiableSet(propResult);
                cache.put(propIds, propResult);
            }
            return propResult;
        }
    }

    public Set<String> leafPrimitiveParameterIds(String... propId)
            throws KnowledgeSourceReadException {
        Set<String> propIds = Arrays.asSet(propId);
        return leafPrimitiveParameterIds(propIds);
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
    public Set<String> leafPrimitiveParameterIds(Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain null");
        }
        Set<String> pids = leafPropositionIds(new HashSet<String>(propIds),
                this.propIdCache);
        Set<String> result = new HashSet<String>();
        for (String pid : pids) {
            if (hasPrimitiveParameterDefinition(pid)) {
                result.add(pid);
            }
        }
        return Collections.unmodifiableSet(result);
    }



    public Set<String> leafEventIds(String... propId)
            throws KnowledgeSourceReadException {
        Set<String> propIds = Arrays.asSet(propId);
        return leafEventIds(propIds);
    }

    public Set<String> leafEventIds(Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain null");
        }
        Set<String> pids = leafPropositionIds(new HashSet<String>(propIds),
                this.propIdCache);
        Set<String> result = new HashSet<String>();
        for (String pid : pids) {
            if (hasEventDefinition(pid)) {
                result.add(pid);
            }
        }
        return result;
    }

    public Set<String> leafConstantIds(String... propId)
            throws KnowledgeSourceReadException {
        Set<String> propIds = Arrays.asSet(propId);
        return leafConstantIds(propIds);
    }

    public Set<String> leafConstantIds(Set<String> propIds)
            throws KnowledgeSourceReadException {
        if (propIds == null) {
            throw new IllegalArgumentException("propIds cannot be null");
        }
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain null");
        }
        Set<String> pids = leafPropositionIds(new HashSet<String>(propIds),
                this.propIdCache);
        Set<String> result = new HashSet<String>();
        for (String pid : pids) {
            if (hasConstantDefinition(pid)) {
                result.add(pid);
            }
        }
        return result;
    }

    /**
     * Actually gets the leaf constant ids. This exists so that we can recurse
     * through the is-a hierarchy and aggregate the results in one set.
     * 
     * @param propIds
     *            a <code>Set</code> of constant id <code>String</code>s.
     * @param result
     *            a non-<code>null</code> <code>Set</code> in which to aggregate
     *            leaf constant ids.
     */
    private void leafPropositionIdsHelper(Collection<String> propIds,
            Set<String> result, Set<PropositionDefinition> propResult)
            throws KnowledgeSourceReadException {
        List<PropositionDefinition> propDefs = new ArrayList<PropositionDefinition>();
        for (String propId : propIds) {
            PropositionDefinition propDef = readPropositionDefinition(propId);
            if (propDef != null) {
                propDefs.add(propDef);
            }
        }
        leafPropositionIdsHelper(propDefs, result, propResult);
    }

    private void leafPropositionIdsHelper(List<PropositionDefinition> propDefs,
            Set<String> result, Set<PropositionDefinition> propResult)
            throws KnowledgeSourceReadException {
        for (PropositionDefinition propDef : propDefs) {
            List<PropositionDefinition> children =
                    new ArrayList<PropositionDefinition>(
                    readAbstractedFrom(propDef.getId()));
            children.addAll(readInverseIsA(propDef.getId()));
            if (children.isEmpty()) {
                if (result != null) {
                    result.add(propDef.getId());
                }
                if (propResult != null) {
                    propResult.add(propDef);
                }
            } else {
                leafPropositionIdsHelper(children, result, propResult);
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
    public List<String> getPropositionDefinitionsByTerm(
            And<TermSubsumption> termSubsumptionClause)
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
            result.addAll(backend.getPropositionsByTermSubsumption(termSubsumptionClause));
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
