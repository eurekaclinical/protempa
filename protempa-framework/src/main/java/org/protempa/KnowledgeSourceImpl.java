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

import org.protempa.valueset.ValueSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
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
    private final PropositionDefinitionReader propDefReader;
    private final AbstractionDefinitionReader abstractionDefReader;
    private final ContextDefinitionReader contextDefReader;
    private final TemporalPropositionDefinitionReader tempPropDefReader;
    private SubtreePropositionDefinitionGetterRegular inDataSourceGetter;
    private SubtreePropositionDefinitionGetterRegular collectSubtreeGetter;

    public KnowledgeSourceImpl(KnowledgeSourceBackend... backends) {
        super(backends);
        this.propDefReader = new PropositionDefinitionReader();
        this.abstractionDefReader = new AbstractionDefinitionReader();
        this.contextDefReader = new ContextDefinitionReader();
        this.tempPropDefReader = new TemporalPropositionDefinitionReader();
    }

    /**
     * Connect to the knowledge source backend(s).
     */
    private void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
        if (isClosed()) {
            throw new IllegalStateException("Knowledge source already closed!");
        }
        if (this.inDataSourceGetter == null) {
            this.inDataSourceGetter
                    = new SubtreePropositionDefinitionGetterRegular(this, true);
        }
        if (this.collectSubtreeGetter == null) {
            this.collectSubtreeGetter
                    = new SubtreePropositionDefinitionGetterRegular(this, false);
        }
    }

    private void initializeIfNeeded(String template, String... propIds)
            throws KnowledgeSourceReadException {
        try {
            initializeIfNeeded();
        } catch (BackendInitializationException | BackendNewInstanceException ex) {
            String action = MessageFormat.format(template, StringUtils.join(propIds, ", "));
            throw new KnowledgeSourceReadException(
                    "An error occurred " + action, ex);
        }
    }

    private void initializeIfNeeded(String action)
            throws KnowledgeSourceReadException {
        initializeIfNeeded(action, null);
    }

    @Override
    public List<PropositionDefinition> readInverseIsA(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        List<PropositionDefinition> result = new ArrayList<>();
        initializeIfNeeded("reading inverseIsA of {0}", propDef.getId());
        String[] propIds = propDef.getInverseIsA();
        result.addAll(readPropositionDefinitions(propIds));
        return result;
    }

    @Override
    public List<PropositionDefinition> readIsA(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readIsA(propDef.getId());
    }

    @Override
    public List<String> readIsAPropIds(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readIsAPropIds(propDef.getId());
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
    public List<PropositionDefinition> readIsA(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> isAs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(isAs, backend.readIsA(id));
        }
        return readPropositionDefinitions(isAs.toArray(new String[isAs.size()]));
    }

    @Override
    public List<String> readIsAPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> isAs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(isAs, backend.readIsA(id));
        }
        return new ArrayList<>(isAs);
    }

    @Override
    public List<PropositionDefinition> readAbstractedFrom(
            AbstractionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded("reading abstractedFrom of {0}",
                propDef.getId());

        Set<String> propIds = propDef.getAbstractedFrom();
        return readPropositionDefinitions(propIds.toArray(new String[propIds.size()]));
    }

    @Override
    public List<AbstractionDefinition> readAbstractedInto(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readAbstractedInto(propDef.getId());
    }

    @Override
    public List<String> readAbstractedIntoPropIds(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readAbstractedIntoPropIds(propDef.getId());
    }

    @Override
    public List<PropositionDefinition> readAbstractedFrom(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        AbstractionDefinition propDef = readAbstractionDefinition(id);
        if (propDef == null) {
            return Collections.emptyList();
        } else {
            return readAbstractedFrom(propDef);
        }
    }

    @Override
    public List<AbstractionDefinition> readAbstractedInto(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> abstractedIntos = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(abstractedIntos,
                    backend.readAbstractedInto(id));
        }
        return readAbstractionDefinitions(abstractedIntos.toArray(new String[abstractedIntos.size()]));
    }

    @Override
    public List<String> readAbstractedIntoPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> abstractedIntos = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(abstractedIntos,
                    backend.readAbstractedInto(id));
        }
        return new ArrayList<>(abstractedIntos);
    }

    @Override
    public List<ContextDefinition> readInduces(
            TemporalPropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readInduces(propDef.getId());
    }

    @Override
    public List<String> readInducesPropIds(
            TemporalPropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        return readInducesPropIds(propDef.getId());
    }

    @Override
    public List<ContextDefinition> readInduces(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> induces = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(induces,
                    backend.readInduces(id));
        }
        return readContextDefinitions(induces.toArray(new String[induces.size()]));
    }

    @Override
    public List<String> readInducesPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> induces = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(induces,
                    backend.readInduces(id));
        }
        return new ArrayList<>(induces);
    }

    @Override
    public List<ContextDefinition> readSubContextOfs(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> subContextOfs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(subContextOfs,
                    backend.readSubContextOfs(id));
        }
        return readContextDefinitions(subContextOfs.toArray(new String[subContextOfs.size()]));
    }

    @Override
    public List<String> readSubContextOfPropIds(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Set<String> subContextOfs = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            Arrays.addAll(subContextOfs,
                    backend.readSubContextOfs(id));
        }
        return new ArrayList<>(subContextOfs);
    }

    @Override
    public List<ContextDefinition> readSubContextOfs(ContextDefinition cd)
            throws KnowledgeSourceReadException {
        if (cd == null) {
            throw new IllegalArgumentException("cd cannot be null");
        }
        return readSubContextOfs(cd.getId());
    }

    @Override
    public List<String> readSubContextOfPropIds(ContextDefinition cd)
            throws KnowledgeSourceReadException {
        if (cd == null) {
            throw new IllegalArgumentException("cd cannot be null");
        }
        return readSubContextOfPropIds(cd.getId());
    }

    @Override
    public List<ContextDefinition> readSubContexts(String contextId) throws KnowledgeSourceReadException {
        if (contextId == null) {
            throw new IllegalArgumentException("contextId cannot be null");
        }
        ContextDefinition pd = readContextDefinition(contextId);
        if (pd != null) {
            return readSubContexts(pd);
        } else {
            return new ArrayList<>(0);
        }

    }

    @Override
    public List<ContextDefinition> readSubContexts(ContextDefinition contextDef) throws KnowledgeSourceReadException {
        if (contextDef == null) {
            throw new IllegalArgumentException("contextDef cannot be null");
        }
        initializeIfNeeded("reading subContexts of {0}",
                contextDef.getId());

        String[] propIds = contextDef.getSubContexts();
        return readContextDefinitions(propIds);
    }

    @Override
    public List<PropositionDefinition> readParents(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }

        List<String> result = readParentPropIds(propDef);

        return readPropositionDefinitions(result.toArray(new String[result.size()]));
    }

    @Override
    public List<String> readParentPropIds(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        List<String> subContextOfs;
        List<String> induces;
        if (propDef instanceof ContextDefinition) {
            subContextOfs = readSubContextOfPropIds((ContextDefinition) propDef);
            induces = readInducesPropIds((ContextDefinition) propDef);
        } else {
            subContextOfs = Collections.emptyList();
            induces = Collections.emptyList();
        }

        List<String> ad = readAbstractedIntoPropIds(propDef);
        List<String> pd = readIsAPropIds(propDef);
        Set<String> result = new HashSet<>();
        result.addAll(subContextOfs);
        result.addAll(induces);
        result.addAll(ad);
        result.addAll(pd);
        return new ArrayList<>(result);
    }

    @Override
    public List<PropositionDefinition> readParents(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        PropositionDefinition pd = readPropositionDefinition(propId);
        if (pd != null) {
            return readParents(pd);
        } else {
            return new ArrayList<>(0);
        }
    }

    @Override
    public List<String> readParentPropIds(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        PropositionDefinition pd = readPropositionDefinition(propId);
        if (pd != null) {
            return readParentPropIds(pd);
        } else {
            return new ArrayList<>(0);
        }
    }

    @Override
    public List<TemporalPropositionDefinition> readInducedBy(String contextId) throws KnowledgeSourceReadException {
        if (contextId == null) {
            throw new IllegalArgumentException("contextId cannot be null");
        }
        ContextDefinition pd = readContextDefinition(contextId);
        if (pd != null) {
            return readInducedBy(pd);
        } else {
            return new ArrayList<>(0);
        }
    }

    @Override
    public List<TemporalPropositionDefinition> readInducedBy(ContextDefinition contextDef) throws KnowledgeSourceReadException {
        if (contextDef == null) {
            throw new IllegalArgumentException("contextDef cannot be null");
        }
        List<TemporalPropositionDefinition> result = new ArrayList<>();
        initializeIfNeeded("reading inducedBy of {0}",
                contextDef.getId());

        TemporalExtendedPropositionDefinition[] tepds = contextDef.getInducedBy();
        Set<String> propIds = new HashSet<>();
        for (TemporalExtendedPropositionDefinition tepd : tepds) {
            propIds.add(tepd.getPropositionId());
        }
        return readTemporalPropositionDefinitions(propIds.toArray(new String[propIds.size()]));
    }

    @Override
    public boolean hasTemporalPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return this.tempPropDefReader.has(id);
    }

    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return this.tempPropDefReader.read(id);
    }

    @Override
    public List<TemporalPropositionDefinition> readTemporalPropositionDefinitions(String... propIds) throws KnowledgeSourceReadException {
        return this.tempPropDefReader.read(propIds);
    }

    private abstract class AbstractDefinitionReader<E extends PropositionDefinition> {

        final E read(String id) throws KnowledgeSourceReadException {
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            initializeIfNeeded("reading the proposition definition {0}", id);

            E result = null;
            if (result == null) {
                for (KnowledgeSourceBackend backend : getBackends()) {
                    result = readFromBackend(id, backend);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return result;
        }

        final List<E> read(String[] ids) throws KnowledgeSourceReadException {
            assert ids != null : "ids cannot be null";
            initializeIfNeeded("reading the proposition definitions {0}", ids);

            List<E> result = new ArrayList<>();

            for (KnowledgeSourceBackend backend : getBackends()) {
                result.addAll(readFromBackend(Arrays.asList(ids), backend));
            }
            return result;
        }

        final boolean has(String id) throws KnowledgeSourceReadException {
            if (id == null) {
                throw new IllegalArgumentException("id cannot be null");
            }
            return read(id) != null;
        }

        protected abstract E readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException;

        protected abstract List<E> readFromBackend(List<String> ids,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException;

    }

    private final class PropositionDefinitionReader extends AbstractDefinitionReader<PropositionDefinition> {

        @Override
        protected PropositionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readPropositionDefinition(id);
        }

        @Override
        protected List<PropositionDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readPropositionDefinitions(ids.toArray(new String[ids.size()]));
        }
    }

    private final class AbstractionDefinitionReader
            extends AbstractDefinitionReader<AbstractionDefinition> {

        @Override
        protected AbstractionDefinition readFromBackend(String id,
                KnowledgeSourceBackend backend)
                throws KnowledgeSourceReadException {
            return backend.readAbstractionDefinition(id);
        }

        @Override
        protected List<AbstractionDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readAbstractionDefinitions(ids.toArray(new String[ids.size()]));
        }

    }

    private final class ContextDefinitionReader
            extends AbstractDefinitionReader<ContextDefinition> {

        @Override
        protected ContextDefinition readFromBackend(String id, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readContextDefinition(id);
        }

        @Override
        protected List<ContextDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readContextDefinitions(ids.toArray(new String[ids.size()]));
        }

    }

    private final class TemporalPropositionDefinitionReader
            extends AbstractDefinitionReader<TemporalPropositionDefinition> {

        @Override
        protected TemporalPropositionDefinition readFromBackend(String id, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readTemporalPropositionDefinition(id);
        }

        @Override
        protected List<TemporalPropositionDefinition> readFromBackend(List<String> ids, KnowledgeSourceBackend backend) throws KnowledgeSourceReadException {
            return backend.readTemporalPropositionDefinitions(ids.toArray(new String[ids.size()]));
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
    public List<PropositionDefinition> readPropositionDefinitions(String... propIds)
            throws KnowledgeSourceReadException {
        return this.propDefReader.read(propIds);
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
    public List<AbstractionDefinition> readAbstractionDefinitions(String... propIds) throws KnowledgeSourceReadException {
        return this.abstractionDefReader.read(propIds);
    }

    @Override
    public ContextDefinition readContextDefinition(String id) throws KnowledgeSourceReadException {
        return this.contextDefReader.read(id);
    }

    @Override
    public List<ContextDefinition> readContextDefinitions(String... propIds) throws KnowledgeSourceReadException {
        return this.contextDefReader.read(propIds);
    }

    @Override
    public boolean hasAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return this.abstractionDefReader.has(id);
    }

    @Override
    public boolean hasContextDefinition(String id) throws KnowledgeSourceReadException {
        return this.contextDefReader.has(id);
    }

    @Override
    public ValueSet readValueSet(String id) throws KnowledgeSourceReadException {
        ValueSet result = null;
        initializeIfNeeded("reading the value set {0}", id);
        if (getBackends() != null) {
            for (KnowledgeSourceBackend backend : getBackends()) {
                result = backend.readValueSet(id);
                if (result != null) {
                    return result;
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasValueSet(String id) throws KnowledgeSourceReadException {
        boolean result = false;
        if (!result) {
            initializeIfNeeded("reading the value set {0}", id);
            for (KnowledgeSourceBackend backend : getBackends()) {
                result = backend.readValueSet(id) != null;
                if (result) {
                    return result;
                }
            }
        }
        return result;
    }

    @Override
    public void close() throws SourceCloseException {
        clear();
        super.close();
    }

    @Override
    public void clear() {
    }

    @Override
    public Set<String> collectPropIdDescendantsUsingAllNarrower(boolean inDataSourceOnly, String... propIds)
            throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition ids for {0} with inDataSource set to true",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.inDataSourceGetter.subtreePropIds(inDataSourceOnly, propIds);
    }

    @Override
    public Set<PropositionDefinition> collectPropDefDescendantsUsingAllNarrower(
            boolean inDataSourceOnly, String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition definitions for {0} with inDataSource set to true",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.inDataSourceGetter.subtreePropDefs(inDataSourceOnly, propIds);
    }

    @Override
    public Set<String> collectPropIdDescendantsUsingInverseIsA(String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition ids for {0}",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.collectSubtreeGetter.subtreePropIds(false, propIds);
    }

    @Override
    public Set<PropositionDefinition> collectPropDefDescendantsUsingInverseIsA(String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded(
                "Getting proposition definitions for {0}",
                StringUtils.join(propIds, ","));
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        return this.collectSubtreeGetter.subtreePropDefs(false, propIds);
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
        List<String> result = new ArrayList<>();
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

    /**
     * Gets the proposition Definitions from each backend that match the
     * searchKey
     *
     * @param searchKey
     * @return
     * @throws KnowledgeSourceReadException
     */
    @Override
    public List<String> getMatchingPropIds(String searchKey)
            throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.INFO,
                "Searching knowledge source For search string {0}", searchKey);
        Set<String> searchResults = getSearchResultsFromBackend(searchKey);
        return new ArrayList<>(searchResults);
    }

    private Set<String> getSearchResultsFromBackend(String searchKey)
            throws KnowledgeSourceReadException {
        Set<String> results = new HashSet<>();
        for (KnowledgeSourceBackend backend : getBackends()) {
            results.addAll(backend.getKnowledgeSourceSearchResults(searchKey));
        }
        return results;
    }
}
