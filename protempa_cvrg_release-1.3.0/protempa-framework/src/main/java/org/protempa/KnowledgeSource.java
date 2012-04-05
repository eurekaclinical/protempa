/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
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
     * PROTEMPA knowledge base.
     */
    private KnowledgeBase protempaKnowledgeBase;
    private final BackendManager<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource, KnowledgeSourceBackend> backendManager;
    private final Map<Set<String>, Set<String>> propIdInDataSourceCache;
    private final Map<Set<String>, Set<PropositionDefinition>> propIdPropInDataSourceCache;
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
        this.propIdPropInDataSourceCache = new ReferenceMap();
        this.propIdInDataSourceCache = new ReferenceMap();
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
     * @param id a proposition id {@link String}. Cannot be <code>null</code>.
     * @return a {@link ConstantDefinition}, or <code>null</code> if none was
     *         found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
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
     * @param id a proposition id {@link String}. Cannot be <code>null</code>.
     * @return an {@link EventDefinition}, or <code>null</code> if none was
     *         found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
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
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            try {
                initializeIfNeeded();
            } catch (BackendInitializationException ex) {
                throw new KnowledgeSourceReadException(
                        "An error occurred reading the proposition definition" + id,
                        ex);
            } catch (BackendNewInstanceException ex) {
                throw new KnowledgeSourceReadException(
                        "An error occurred reading the proposition definition" + id,
                        ex);
            }

            P result = null;
            if (!isInNotFound(id)) {
                boolean lookInBackend = false;
                if (protempaKnowledgeBase != null) {
                    result = readFromKnowledgeBase(id);
                    if (result == null) {
                        if (protempaKnowledgeBase.getPropositionDefinition(id) != null) {
                            putInNotFound(id);
                        } else {
                            lookInBackend = true;
                        }
                    }
                }

                if (lookInBackend) {
                    if (backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : backendManager.getBackends()) {
                            result = readFromBackend(id, backend);
                            if (result != null) {
                                return result;
                            }
                        }
                        putInNotFound(id);
                    }
                }
            }

            return result;
        }

        boolean has(String id) throws KnowledgeSourceReadException {
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            try {
                initializeIfNeeded();
            } catch (BackendInitializationException ex) {
                throw new KnowledgeSourceReadException(
                        "An error occurred reading the proposition definition" + id,
                        ex);
            } catch (BackendNewInstanceException ex) {
                throw new KnowledgeSourceReadException(
                        "An error occurred reading the proposition definition" + id,
                        ex);
            }
            boolean result = false;
            if (!isInNotFound(id)) {
                boolean lookInBackend = false;
                if (protempaKnowledgeBase != null) {
                    result = (readFromKnowledgeBase(id) != null);
                    if (!result) {
                        if (protempaKnowledgeBase.getPropositionDefinition(id) != null) {
                            putInNotFound(id);
                        } else {
                            lookInBackend = true;
                        }
                    }
                }

                if (lookInBackend) {
                    if (backendManager.getBackends() != null) {
                        for (KnowledgeSourceBackend backend : backendManager.getBackends()) {
                            result = readFromBackend(id, backend) != null;
                            if (result) {
                                return result;
                            }
                        }
                        putInNotFound(id);
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
     *            a proposition id {@link String}. Cannot be
     *            <code>null</code>.
     * @return a {@link PropositionDefinition}, or <code>null</code> if none was
     *         found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
     */
    public PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.propDefReader.read(id);
    }

    /**
     * Returns the specified temporal proposition definition.
     *
     * @param id
     *            a proposition id {@link String}. Cannot be
     *            <code>null</code>.
     * @return a {@link TemporalPropositionDefinition}, or <code>null</code>
     * if none was found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
     */
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
     *            a proposition id {@link String}.
     *            Cannot be <code>null</code>.
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
     *            a proposition definition id {@link String}.
     *            Cannot be <code>null</code>.
     * @return an {@link AbstractionDefinition} object, or <code>null</code> if
     *         none was found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
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
                            result = backend.readValueSet(id,
                                    protempaKnowledgeBase) != null;
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
            this.propIdInDataSourceCache.clear();
            this.propIdPropInDataSourceCache.clear();
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

    public Set<String> inDataSourcePropositionIds(String... propIds) throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        return inDataSourcePropositionIds(propIdsAsSet, this.propIdInDataSourceCache);
    }

    public Set<PropositionDefinition> inDataSourcePropositionDefinitions(
            String... propIds) throws KnowledgeSourceReadException {
        return inDataSourcePropositionDefinitions(Arrays.asSet(propIds));
    }


    Set<PropositionDefinition> inDataSourcePropositionDefinitions(
            Set<String> propIds) throws KnowledgeSourceReadException {
        assert propIds != null : "propIds cannot be null";
        return inDataSourcePropositionDefinitions(propIds, this.propIdPropInDataSourceCache);
    }

    private Set<PropositionDefinition> inDataSourcePropositionDefinitions(Set<String> propIds,
            Map<Set<String>, Set<PropositionDefinition>> cache)
            throws KnowledgeSourceReadException {
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain a null element");
        }

        Set<PropositionDefinition> cachedResult = cache.get(propIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<PropositionDefinition> propResult =
                    new HashSet<PropositionDefinition>();
            if (propIds != null) {
                inDataSourcePropositionIdsHelper(propIds, null, propResult);
                propResult = Collections.unmodifiableSet(propResult);
                cache.put(propIds, propResult);
            }
            return propResult;
        }
    }

    private Set<String> inDataSourcePropositionIds(Set<String> propIds,
            Map<Set<String>, Set<String>> cache) throws KnowledgeSourceReadException {
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain a null element");
        }

        Set<String> cachedResult = cache.get(propIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<String> result = new HashSet<String>();
            if (propIds != null) {
                inDataSourcePropositionIdsHelper(propIds, result, null);
                result = Collections.unmodifiableSet(result);
                cache.put(propIds, result);
            }
            return result;
        }
    }

    private void inDataSourcePropositionIdsHelper(Collection<String> propIds,
            Set<String> result, Set<PropositionDefinition> propResult)
            throws KnowledgeSourceReadException {
        List<PropositionDefinition> propDefs = new ArrayList<PropositionDefinition>();
        for (String propId : propIds) {
            PropositionDefinition propDef = readPropositionDefinition(propId);
            if (propDef != null) {
                propDefs.add(propDef);
            }
        }
        inDataSourcePropositionIdsHelper(propDefs, result, propResult);
    }

    private void inDataSourcePropositionIdsHelper(List<PropositionDefinition> propDefs,
            Set<String> result, Set<PropositionDefinition> propResult)
            throws KnowledgeSourceReadException {
        for (PropositionDefinition propDef : propDefs) {
            String propDefId = propDef.getId();
            List<PropositionDefinition> children =
                    new ArrayList<PropositionDefinition>(
                    readAbstractedFrom(propDefId));
            children.addAll(readInverseIsA(propDefId));

            if (propDef.getInDataSource()) {
                if (result != null) {
                    result.add(propDefId);
                }
                if (propResult != null) {
                    propResult.add(propDef);
                }
            }
            inDataSourcePropositionIdsHelper(children, result, propResult);
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
    public Set<String> leafPropositionIds(String... propIds)
            throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        return leafPropositionIds(propIdsAsSet, this.propIdCache);
    }

    public Set<PropositionDefinition> leafPropositionDefinitions(
            String... propIds) throws KnowledgeSourceReadException {
        return leafPropositionDefinitions(Arrays.asSet(propIds));
    }

    Set<PropositionDefinition> leafPropositionDefinitions(
            Set<String> propIds)
            throws KnowledgeSourceReadException {
        assert propIds != null : "propIds cannot be null";
        return leafPropositionDefinitions(propIds, this.propIdPropCache);
    }

    private Set<String> leafPropositionIds(Set<String> propIds,
            Map<Set<String>, Set<String>> cache)
            throws KnowledgeSourceReadException {
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
        if (propIds.contains(null)) {
            throw new IllegalArgumentException("propIds cannot contain a null element");
        }

        Set<PropositionDefinition> cachedResult = cache.get(propIds);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Set<PropositionDefinition> propResult =
                    new HashSet<PropositionDefinition>();
            if (propIds != null) {
                leafPropositionIdsHelper(propIds, null, propResult);
                propResult = Collections.unmodifiableSet(propResult);
                cache.put(propIds, propResult);
            }
            return propResult;
        }
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
            String propDefId = propDef.getId();
            List<PropositionDefinition> children =
                    new ArrayList<PropositionDefinition>(
                    readAbstractedFrom(propDefId));
            children.addAll(readInverseIsA(propDefId));
            if (children.isEmpty()) {
                if (result != null) {
                    result.add(propDefId);
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