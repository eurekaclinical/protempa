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

import java.text.MessageFormat;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
public final class KnowledgeSourceImpl
        extends AbstractSource<KnowledgeSourceUpdatedEvent, KnowledgeSourceBackend, KnowledgeSourceUpdatedEvent, KnowledgeSourceBackendUpdatedEvent> implements KnowledgeSource {

    /**
     * PROTEMPA knowledge base.
     */
    private PropositionDefinitionCache propositionDefinitionCache;
    private final Map<Set<String>, Set<String>> propIdInDataSourceCache;
    private final Map<Set<String>, Set<PropositionDefinition>> propIdPropInDataSourceCache;
    private final Map<Set<String>, Set<PropositionDefinition>> propIdPropCache;
    private final Map<String, Object> notFoundAbstractionDefinitionRequests;
    private final Map<String, Object> notFoundValueSetRequests;
    private final Map<String, Object> notFoundPropositionDefinitionRequests;
    private final PropositionDefinitionReader propDefReader;
    private final AbstractionDefinitionReader abstractionDefReader;
    private final Map<PropositionDefinition, List<PropositionDefinition>> inverseIsACache;
    private final Map<AbstractionDefinition, List<PropositionDefinition>> abstractedFromCache;

    @SuppressWarnings("unchecked")
    public KnowledgeSourceImpl(KnowledgeSourceBackend[] backends) {
        super(backends != null ? backends : new KnowledgeSourceBackend[0]);
        this.propIdPropInDataSourceCache = new ReferenceMap();
        this.propIdInDataSourceCache = new ReferenceMap();
        this.propIdPropCache = new ReferenceMap();
        this.inverseIsACache = new ReferenceMap();
        this.abstractedFromCache = new ReferenceMap();
        this.notFoundAbstractionDefinitionRequests = new WeakHashMap<String, Object>();
        this.notFoundValueSetRequests = new WeakHashMap<String, Object>();
        this.notFoundPropositionDefinitionRequests = new WeakHashMap<String, Object>();

        this.propDefReader = new PropositionDefinitionReader();
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
        if (this.propositionDefinitionCache == null) {
            this.propositionDefinitionCache = new PropositionDefinitionCache();
        }
    }

    private void initializeIfNeeded(String template, String substitution) throws KnowledgeSourceReadException {
        try {
            initializeIfNeeded();
        } catch (BackendInitializationException ex) {
            String action = MessageFormat.format(template, substitution);
            throw new KnowledgeSourceReadException(
                    "An error occurred " + action, ex);
        } catch (BackendNewInstanceException ex) {
            String action = MessageFormat.format(template, substitution);
            throw new KnowledgeSourceReadException(
                    "An error occurred " + action, ex);
        }
    }

    private void initializeIfNeeded(String action)
            throws KnowledgeSourceReadException {
        initializeIfNeeded(action, null);
    }

    @Override
    public List<PropositionDefinition> readInverseIsA(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        List<PropositionDefinition> result = this.inverseIsACache.get(propDef);
        if (result != null) {
            return result;
        } else {
            result = new ArrayList<PropositionDefinition>();
            initializeIfNeeded("reading inverseIsA of {0}", propDef.getId());
            String[] propIds = propDef.getInverseIsA();
            for (String propId : propIds) {
                result.add(readPropositionDefinition(propId));
            }
            result = Collections.unmodifiableList(result);
            this.inverseIsACache.put(propDef, result);
            return result;
        }
    }

    @Override
    public List<PropositionDefinition> readInverseIsA(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        PropositionDefinition propDef = readPropositionDefinition(id);
        if (propDef == null) {
            return Collections.emptyList();
        } else {
            return readInverseIsA(propDef);
        }
    }

    @Override
    public List<? extends PropositionDefinition> readAbstractedFrom(AbstractionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        List<PropositionDefinition> result =
                this.abstractedFromCache.get(propDef);
        if (result != null) {
            return result;
        } else {
            result = new ArrayList<PropositionDefinition>();
            initializeIfNeeded("reading abstractedFrom of {0}",
                    propDef.getId());

            Set<String> propIds = propDef.getAbstractedFrom();
            for (String propId : propIds) {
                result.add(readPropositionDefinition(propId));
            }
            result = Collections.unmodifiableList(result);
            if (propDef != null) {
                this.abstractedFromCache.put(propDef, result);
            }
            return result;
        }
    }

    @Override
    public List<? extends PropositionDefinition> readAbstractedFrom(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        AbstractionDefinition propDef = readAbstractionDefinition(id);
        if (propDef == null) {
            return Collections.emptyList();
        } else {
            return readAbstractedFrom((AbstractionDefinition) propDef);
        }
    }

    private abstract class AbstractDefinitionReader<E extends PropositionDefinition> {

        final E read(String id) throws KnowledgeSourceReadException {
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            initializeIfNeeded("reading the proposition definition {0}", id);

            E result = null;
            if (!isInNotFound(id)) {
                if (propositionDefinitionCache != null) {
                    result = readFromKnowledgeBase(id);
                    if (result == null) {
                        if (!propositionDefinitionCache.hasPropositionDefinition(id)
                                && getBackends() != null) {
                            for (KnowledgeSourceBackend backend : getBackends()) {
                                result = readFromBackend(id, backend);
                                if (result != null) {
                                    try {
                                        putInKnowledgeBase(result);
                                    } catch (InvalidPropositionIdException ex) {
                                        ProtempaUtil.logger().log(Level.SEVERE, "Error adding proposition definition to cache", ex);
                                        throw new AssertionError("Error adding proposition definition to cache: " + ex.getMessage());
                                    }
                                    return result;
                                }
                            }
                        }
                        putInNotFound(id);
                    }
                }
            }
            return result;
        }

        final boolean has(String id) throws KnowledgeSourceReadException {
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            return read(id) != null;
        }

        protected abstract boolean isInNotFound(String id);

        protected abstract void putInNotFound(String id);

        protected abstract E readFromKnowledgeBase(String id);

        protected abstract E readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException;

        protected abstract void putInKnowledgeBase(E propDef)
                throws InvalidPropositionIdException;
    }

    private final class PropositionDefinitionReader extends AbstractDefinitionReader<PropositionDefinition> {

        @Override
        protected PropositionDefinition readFromKnowledgeBase(String id) {
            return propositionDefinitionCache.getPropositionDefinition(id);
        }

        @Override
        protected PropositionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readPropositionDefinition(id);
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundPropositionDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundPropositionDefinitionRequests.put(id, null);
        }

        @Override
        protected void putInKnowledgeBase(PropositionDefinition propDef) throws InvalidPropositionIdException {
            propositionDefinitionCache.addPropositionDefinition(propDef);
        }
    }

    private final class AbstractionDefinitionReader extends AbstractDefinitionReader<AbstractionDefinition> {

        @Override
        protected AbstractionDefinition readFromKnowledgeBase(String id) {
            return propositionDefinitionCache.getAbstractionDefinition(id);
        }

        @Override
        protected AbstractionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readAbstractionDefinition(id);
        }

        @Override
        protected boolean isInNotFound(String id) {
            return notFoundAbstractionDefinitionRequests.containsKey(id);
        }

        @Override
        protected void putInNotFound(String id) {
            notFoundAbstractionDefinitionRequests.put(id, null);
        }

        @Override
        protected void putInKnowledgeBase(AbstractionDefinition propDef) throws InvalidPropositionIdException {
            propositionDefinitionCache.addAbstractionDefinition(propDef);
            propositionDefinitionCache.addPropositionDefinition(propDef);
        }
    }

    /**
     * Returns the specified proposition definition.
     *
     * @param id a proposition id {@link String}. Cannot be <code>null</code>.
     * @return a {@link PropositionDefinition}, or <code>null</code> if none was
     * found with the given <code>id</code>.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge base.
     */
    @Override
    public PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.propDefReader.read(id);
    }

    @Override
    public boolean hasPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.propDefReader.has(id);
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return this.abstractionDefReader.read(id);
    }

    @Override
    public boolean hasAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return this.abstractionDefReader.has(id);
    }

    @Override
    public ValueSet readValueSet(String id) throws KnowledgeSourceReadException {
        ValueSet result = null;
        if (!this.notFoundValueSetRequests.containsKey(id)) {
            if (propositionDefinitionCache != null) {
                result = propositionDefinitionCache.getValueSet(id);
            }
            if (result == null) {
                initializeIfNeeded("reading the value set {0}", id);
                if (getBackends() != null) {
                    for (KnowledgeSourceBackend backend : getBackends()) {
                        result = backend.readValueSet(id);
                        if (result != null) {
                            try {
                                this.propositionDefinitionCache.addValueSet(result);
                            } catch (InvalidValueSetDefinitionException ex) {
                                ProtempaUtil.logger().log(Level.SEVERE, "Error adding value set definition to cache", ex);
                                throw new AssertionError("Error adding value set definition to cache: " + ex.getMessage());
                            }
                            return result;
                        }
                    }
                    this.notFoundValueSetRequests.put(id, null);
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasValueSet(String id) throws KnowledgeSourceReadException {
        boolean result = false;
        if (!this.notFoundValueSetRequests.containsKey(id)) {
            if (propositionDefinitionCache != null) {
                result = propositionDefinitionCache.getValueSet(id) != null;
            }
            if (!result) {
                initializeIfNeeded("reading the value set {0}", id);
                for (KnowledgeSourceBackend backend : getBackends()) {
                    result = backend.readValueSet(id) != null;
                    if (result) {
                        return result;
                    }
                }
                this.notFoundValueSetRequests.put(id, null);
            }
        }
        return result;
    }

    @Override
    public void close() {
        clear();
        this.propositionDefinitionCache = null;
        super.close();
    }

    @Override
    public void clear() {
        if (this.propositionDefinitionCache != null) {
            this.propositionDefinitionCache.clear();
            this.propIdInDataSourceCache.clear();
            this.propIdPropInDataSourceCache.clear();
            this.propIdPropCache.clear();
            this.inverseIsACache.clear();
            this.abstractedFromCache.clear();
            this.notFoundAbstractionDefinitionRequests.clear();
            this.notFoundValueSetRequests.clear();
            this.notFoundPropositionDefinitionRequests.clear();

        }
    }

    @Override
    public Set<String> inDataSourcePropositionIds(String... propIds)
            throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        return inDataSourcePropositionIds(propIdsAsSet,
                this.propIdInDataSourceCache);
    }

    @Override
    public Set<PropositionDefinition> inDataSourcePropositionDefinitions(
            String... propIds) throws KnowledgeSourceReadException {
        return inDataSourcePropositionDefinitions(Arrays.asSet(propIds));
    }

    private Set<PropositionDefinition> inDataSourcePropositionDefinitions(
            Set<String> propIds) throws KnowledgeSourceReadException {
        assert propIds != null : "propIds cannot be null";
        return inDataSourcePropositionDefinitions(propIds,
                this.propIdPropInDataSourceCache);
    }

    private Set<PropositionDefinition> inDataSourcePropositionDefinitions(
            Set<String> propIds,
            Map<Set<String>, Set<PropositionDefinition>> cache)
            throws KnowledgeSourceReadException {
        if (propIds.contains(null)) {
            throw new IllegalArgumentException(
                    "propIds cannot contain a null element");
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
            Map<Set<String>, Set<String>> cache)
            throws KnowledgeSourceReadException {
        if (propIds.contains(null)) {
            throw new IllegalArgumentException(
                    "propIds cannot contain a null element");
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
        List<PropositionDefinition> propDefs =
                new ArrayList<PropositionDefinition>();
        for (String propId : propIds) {
            PropositionDefinition propDef = readPropositionDefinition(propId);
            if (propDef != null) {
                propDefs.add(propDef);
            }
        }
        inDataSourcePropositionIdsHelper(propDefs, result, propResult);
    }

    private void inDataSourcePropositionIdsHelper(
            List<PropositionDefinition> propDefs,
            Set<String> result, Set<PropositionDefinition> propResult)
            throws KnowledgeSourceReadException {
        for (PropositionDefinition propDef : propDefs) {
            String propDefId = propDef.getId();
            List<PropositionDefinition> children =
                    new ArrayList<PropositionDefinition>();
            if (propDef instanceof AbstractionDefinition) {
                for (PropositionDefinition ad : readAbstractedFrom((AbstractionDefinition) propDef)) {
                    children.add(ad);
                }
            }
            for (PropositionDefinition propId : readInverseIsA(propDef)) {
                children.add(propId);
            }

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
     * Gets the mappings from term IDs to proposition IDs for each backend.
     *
     * @return a {@link Map} of {@link String}s to a {@link List} of
     * <code>String</code>s, with the keys being {@link Term} IDs and the values
     * being lists of {@link PropositionDefinition} IDs.
     */
    @Override
    public List<String> getPropositionDefinitionsByTerm(
            And<TermSubsumption> termSubsumptionClause)
            throws KnowledgeSourceReadException {
        List<String> result = new ArrayList<String>();
        initializeIfNeeded("getting proposition definitions by term");
        for (KnowledgeSourceBackend backend : getBackends()) {
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
