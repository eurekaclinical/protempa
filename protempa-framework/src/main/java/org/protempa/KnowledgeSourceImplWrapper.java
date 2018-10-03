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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.query.And;

/**
 * @author Andrew Post
 */
class KnowledgeSourceImplWrapper
        extends AbstractSource<KnowledgeSourceUpdatedEvent, KnowledgeSourceBackend, KnowledgeSourceUpdatedEvent, KnowledgeSourceBackendUpdatedEvent>
        implements KnowledgeSource {

    private final KnowledgeSource knowledgeSource;
    private final Map<String, AbstractionDefinition> abstractionDefinitionsMap;
    private final Map<String, PropositionDefinition> propositionDefinitionsMap;
    private final Map<String, TemporalPropositionDefinition> temporalPropDefsMap;
    private final Map<String, ContextDefinition> contextDefinitionsMap;
    private final Map<String, List<String>> isAMap;
    private final Map<String, List<String>> abstractedIntoMap;
    private final Map<String, List<String>> inducesMap;
    private final Map<String, List<String>> subContextOfMap;
    private final Map<String, List<String>> termIdMap;
    private SubtreePropositionDefinitionGetterForWrapper inDataSourceGetter;
    private boolean initialized;
    private SubtreePropositionDefinitionGetterForWrapper collectSubtreeGetter;

    KnowledgeSourceImplWrapper(KnowledgeSource knowledgeSource,
            PropositionDefinition... propositionDefinitions) {
        super(new KnowledgeSourceBackend[0]);
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        this.knowledgeSource = knowledgeSource;
        this.propositionDefinitionsMap = new HashMap<>();
        this.abstractionDefinitionsMap = new HashMap<>();
        this.contextDefinitionsMap = new HashMap<>();
        this.temporalPropDefsMap = new HashMap<>();
        this.isAMap = new HashMap<>();
        this.abstractedIntoMap = new HashMap<>();
        this.inducesMap = new HashMap<>();
        this.subContextOfMap = new HashMap<>();
        this.termIdMap = new HashMap<>();
        for (PropositionDefinition propDef : propositionDefinitions) {
            String propId = propDef.getId();
            if (propDef instanceof ContextDefinition) {
                ContextDefinition cd = (ContextDefinition) propDef;
                for (TemporalExtendedPropositionDefinition inducedBy : cd.getInducedBy()) {
                    org.arp.javautil.collections.Collections.putList(this.inducesMap, inducedBy.getPropositionId(), propId);
                }
                for (String subContextId : cd.getSubContexts()) {
                    org.arp.javautil.collections.Collections.putList(this.subContextOfMap, subContextId, propId);
                }
                this.contextDefinitionsMap.put(propId, cd);
            } else {
                if (propDef instanceof AbstractionDefinition) {
                    AbstractionDefinition ad = (AbstractionDefinition) propDef;
                    for (String abstractedFromPropId
                            : ad.getAbstractedFrom()) {
                        org.arp.javautil.collections.Collections.putList(
                                this.abstractedIntoMap, abstractedFromPropId,
                                propId);
                    }
                    this.abstractionDefinitionsMap.put(propId, ad);
                }
                if (propDef instanceof TemporalPropositionDefinition) {
                    this.temporalPropDefsMap.put(propId, (TemporalPropositionDefinition) propDef);
                }
            }
            this.propositionDefinitionsMap.put(propId, propDef);
            for (String inverseIsAPropId : propDef.getInverseIsA()) {
                org.arp.javautil.collections.Collections.putList(
                        this.isAMap, inverseIsAPropId, propId);
            }
            for (String termId : propDef.getTermIds()) {
                org.arp.javautil.collections.Collections.putList(
                        this.termIdMap, termId, propId);
            }
        }

        this.knowledgeSource.addSourceListener(
                new SourceListener<KnowledgeSourceUpdatedEvent>() {
                    @Override
                    public void sourceUpdated(KnowledgeSourceUpdatedEvent e) {
                        fireSourceUpdated(e);
                    }

                    @Override
                    public void closedUnexpectedly(
                            SourceClosedUnexpectedlyEvent e) {
                                fireClosedUnexpectedly(e);
                            }
                });
    }

    private void initializeIfNeeded() {
        if (!this.initialized) {
            this.inDataSourceGetter
                    = new SubtreePropositionDefinitionGetterForWrapper(this.propositionDefinitionsMap, this.knowledgeSource, true);
            this.collectSubtreeGetter
                    = new SubtreePropositionDefinitionGetterForWrapper(this.propositionDefinitionsMap, this.knowledgeSource, false);
            this.initialized = true;
        }
    }

    @Override
    public boolean hasAbstractionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        boolean result = this.abstractionDefinitionsMap.containsKey(id);
        if (!result) {
            result = this.knowledgeSource.hasAbstractionDefinition(id);
        }
        return result;
    }

    @Override
    public boolean hasPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        boolean result = this.propositionDefinitionsMap.containsKey(id);
        if (!result) {
            result = this.knowledgeSource.hasPropositionDefinition(id);
        }
        return result;
    }

    @Override
    public boolean hasContextDefinition(String id) throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        boolean result = this.contextDefinitionsMap.containsKey(id);
        if (!result) {
            result = this.knowledgeSource.hasContextDefinition(id);
        }
        return result;
    }

    @Override
    public boolean hasTemporalPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        boolean result = this.temporalPropDefsMap.containsKey(id);
        if (!result) {
            result = this.knowledgeSource.hasTemporalPropositionDefinition(id);
        }
        return result;
    }

    @Override
    public boolean hasValueSet(String id) throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        return this.knowledgeSource.hasValueSet(id);
    }

    @Override
    public Set<PropositionDefinition> collectPropDefDescendantsUsingAllNarrower(
            boolean inDataSourceOnly, String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded();
        return this.inDataSourceGetter.collectPropDefs(inDataSourceOnly, propIds);
    }

    @Override
    public Set<String> collectPropIdDescendantsUsingAllNarrower(boolean inDataSourceOnly, String... propIds)
            throws KnowledgeSourceReadException {
        initializeIfNeeded();
        return this.inDataSourceGetter.collectPropIds(inDataSourceOnly, propIds);
    }

    @Override
    public Set<PropositionDefinition> collectPropDefDescendantsUsingInverseIsA(String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded();
        return this.collectSubtreeGetter.collectPropDefs(false, propIds);
    }

    @Override
    public Set<String> collectPropIdDescendantsUsingInverseIsA(String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded();
        return this.collectSubtreeGetter.collectPropIds(false, propIds);
    }

    @Override
    public List<TemporalPropositionDefinition> readInducedBy(String id) throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        ContextDefinition def = readContextDefinition(id);
        if (def != null) {
            return readInducedBy(readContextDefinition(id));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<TemporalPropositionDefinition> readInducedBy(
            ContextDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        Set<String> propIds = new HashSet<>();
        for (TemporalExtendedPropositionDefinition tepd : propDef.getInducedBy()) {
            propIds.add(tepd.getPropositionId());
        }
        return readTemporalPropositionDefinitions(propIds.toArray(new String[propIds.size()]));
    }

    @Override
    public List<ContextDefinition> readSubContexts(String id) throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        ContextDefinition def = readContextDefinition(id);
        if (def != null) {
            return readSubContexts(readContextDefinition(id));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ContextDefinition> readSubContexts(
            ContextDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        return readContextDefinitions(propDef.getSubContexts());
    }

    @Override
    public List<PropositionDefinition> readAbstractedFrom(
            AbstractionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        Set<String> abstractedFrom = propDef.getAbstractedFrom();
        return readPropositionDefinitions(abstractedFrom.toArray(new String[abstractedFrom.size()]));
    }

    @Override
    public List<PropositionDefinition> readAbstractedFrom(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        AbstractionDefinition def = readAbstractionDefinition(id);
        if (def != null) {
            return readAbstractedFrom(readAbstractionDefinition(id));
        } else {
            return Collections.emptyList();
        }
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
    public List<AbstractionDefinition> readAbstractedInto(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<AbstractionDefinition> result
                = new ArrayList<>();
        if (this.abstractedIntoMap.containsKey(propId)) {
            List<String> propIds = this.abstractedIntoMap.get(propId);
            if (propIds != null) {
                result.addAll(readAbstractionDefinitions(propIds.toArray(new String[propIds.size()])));
            }
        }
        List<AbstractionDefinition> r
                = this.knowledgeSource.readAbstractedInto(propId);
        for (AbstractionDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }
        return result;
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
    public List<ContextDefinition> readInduces(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<ContextDefinition> result
                = new ArrayList<>();
        if (this.inducesMap.containsKey(propId)) {
            List<String> propIds = this.inducesMap.get(propId);
            if (propIds != null) {
                result.addAll(readContextDefinitions(propIds.toArray(new String[propIds.size()])));
            }
        }
        List<ContextDefinition> r
                = this.knowledgeSource.readInduces(propId);
        for (ContextDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }
        return result;
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        AbstractionDefinition result
                = this.abstractionDefinitionsMap.get(id);
        if (result == null) {
            result = this.knowledgeSource.readAbstractionDefinition(id);
        }
        return result;
    }

    @Override
    public List<PropositionDefinition> readInverseIsA(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        return readPropositionDefinitions(propDef.getInverseIsA());
    }

    @Override
    public List<PropositionDefinition> readInverseIsA(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        PropositionDefinition def = readPropositionDefinition(id);
        if (def != null) {
            return readInverseIsA(def);
        } else {
            return new ArrayList<>(0);
        }
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
    public List<PropositionDefinition> readIsA(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<PropositionDefinition> result
                = new ArrayList<>();
        if (this.isAMap.containsKey(propId)) {
            List<String> propIds = this.isAMap.get(propId);

            if (propIds != null) {
                result.addAll(readPropositionDefinitions(propIds.toArray(new String[propIds.size()])));
            }
        }

        List<PropositionDefinition> r
                = this.knowledgeSource.readIsA(propId);
        for (PropositionDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }

        return result;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        PropositionDefinition result
                = this.propositionDefinitionsMap.get(id);

        if (result == null) {
            result = this.knowledgeSource.readPropositionDefinition(id);
        }
        return result;
    }

    @Override
    public List<PropositionDefinition> readPropositionDefinitions(String... propIds) throws KnowledgeSourceReadException {
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        initializeIfNeeded();
        List<PropositionDefinition> result = new ArrayList<>();
        List<String> propIdsToGet = new ArrayList<>();
        for (String id : propIds) {
            PropositionDefinition propDef = this.propositionDefinitionsMap.get(id);
            if (propDef != null) {
                result.add(propDef);
            } else {
                propIdsToGet.add(id);
            }
        }

        result.addAll(this.knowledgeSource.readPropositionDefinitions(propIdsToGet.toArray(new String[propIdsToGet.size()])));
        return result;
    }

    @Override
    public List<AbstractionDefinition> readAbstractionDefinitions(String... propIds) throws KnowledgeSourceReadException {
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        initializeIfNeeded();
        List<AbstractionDefinition> result = new ArrayList<>();
        List<String> propIdsToGet = new ArrayList<>();
        for (String id : propIds) {
            AbstractionDefinition propDef = this.abstractionDefinitionsMap.get(id);
            if (propDef != null) {
                result.add(propDef);
            } else {
                propIdsToGet.add(id);
            }
        }

        result.addAll(this.knowledgeSource.readAbstractionDefinitions(propIdsToGet.toArray(new String[propIdsToGet.size()])));
        return result;
    }

    @Override
    public List<TemporalPropositionDefinition> readTemporalPropositionDefinitions(String... propIds) throws KnowledgeSourceReadException {
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        initializeIfNeeded();
        List<TemporalPropositionDefinition> result = new ArrayList<>();
        List<String> propIdsToGet = new ArrayList<>();
        for (String id : propIds) {
            TemporalPropositionDefinition propDef = this.temporalPropDefsMap.get(id);
            if (propDef != null) {
                result.add(propDef);
            } else {
                propIdsToGet.add(id);
            }
        }

        result.addAll(this.knowledgeSource.readTemporalPropositionDefinitions(propIdsToGet.toArray(new String[propIdsToGet.size()])));
        return result;
    }

    @Override
    public List<ContextDefinition> readContextDefinitions(String... propIds) throws KnowledgeSourceReadException {
        ProtempaUtil.checkArrayForNullElement(propIds, "propIds");
        initializeIfNeeded();
        List<ContextDefinition> result = new ArrayList<>();
        List<String> propIdsToGet = new ArrayList<>();
        for (String id : propIds) {
            ContextDefinition propDef = this.contextDefinitionsMap.get(id);
            if (propDef != null) {
                result.add(propDef);
            } else {
                propIdsToGet.add(id);
            }
        }

        result.addAll(this.knowledgeSource.readContextDefinitions(propIdsToGet.toArray(new String[propIdsToGet.size()])));
        return result;
    }

    PropositionDefinition readPropositionDefinitionInt(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        PropositionDefinition result
                = this.propositionDefinitionsMap.get(id);

        return result;
    }

    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        TemporalPropositionDefinition result
                = this.temporalPropDefsMap.get(id);

        if (result == null) {
            result = this.knowledgeSource.readTemporalPropositionDefinition(id);
        }
        return result;
    }

    @Override
    public ContextDefinition readContextDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        ContextDefinition result
                = this.contextDefinitionsMap.get(id);

        if (result == null) {
            result = this.knowledgeSource.readContextDefinition(id);
        }
        return result;
    }

    @Override
    public ValueSet readValueSet(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        return this.knowledgeSource.readValueSet(id);
    }

    @Override
    public List<PropositionDefinition> readParents(
            PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        return readParents(propDef.getId());
    }

    @Override
    public List<PropositionDefinition> readParents(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        if (this.propositionDefinitionsMap.containsKey(propId)) {
            List<PropositionDefinition> result
                    = new ArrayList<>();
            result.addAll(readIsA(propId));
            result.addAll(readAbstractedInto(propId));
            return result;
        } else {
            return this.knowledgeSource.readParents(propId);
        }
    }

    @Override
    public void close() throws SourceCloseException {
        if (this.initialized) {
            this.inDataSourceGetter.clear();
        }
        this.knowledgeSource.close();
    }

    @Override
    public void clear() {
        if (this.initialized) {
            this.inDataSourceGetter.clear();
        }
        this.knowledgeSource.clear();
    }

    /**
     * No-op (this knowledge source has no backends).
     *
     * @param evt the backend updated event.
     */
    @Override
    public void backendUpdated(KnowledgeSourceBackendUpdatedEvent evt) {
    }

    @Override
    public List<ContextDefinition> readSubContextOfs(String propId) throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<ContextDefinition> result
                = new ArrayList<>();
        if (this.subContextOfMap.containsKey(propId)) {
            List<String> propIds
                    = this.subContextOfMap.get(propId);

            if (propIds != null) {
                result.addAll(readContextDefinitions(propIds.toArray(new String[propIds.size()])));
            }
        }
        List<ContextDefinition> r
                = this.knowledgeSource.readSubContextOfs(propId);
        for (ContextDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }
        return result;
    }

    @Override
    public List<ContextDefinition> readSubContextOfs(ContextDefinition contextDef) throws KnowledgeSourceReadException {
        if (contextDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        return readSubContextOfs(contextDef.getId());
    }

    @Override
    public List<String> readAbstractedIntoPropIds(PropositionDefinition propDef) throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        return readAbstractedIntoPropIds(propDef.getId());
    }

    @Override
    public List<String> readAbstractedIntoPropIds(String propId) throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<String> result = new ArrayList<>();
        if (this.abstractedIntoMap.containsKey(propId)) {
            List<String> propIds = this.abstractedIntoMap.get(propId);

            if (propIds != null) {
                for (String abstractedIntoPropId : propIds) {
                    result.add(abstractedIntoPropId);
                }
            }
        }
        List<String> r
                = this.knowledgeSource.readAbstractedIntoPropIds(propId);
        for (String def : r) {
            if (!propositionDefinitionsMap.containsKey(def)) {
                result.add(def);
            }
        }
        return result;
    }

    @Override
    public List<String> readIsAPropIds(PropositionDefinition propDef) throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        return readIsAPropIds(propDef.getId());
    }

    @Override
    public List<String> readIsAPropIds(String propId) throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<String> result = new ArrayList<>();
        if (this.isAMap.containsKey(propId)) {
            List<String> propIds = this.isAMap.get(propId);

            if (propIds != null) {
                for (String isAPropId : propIds) {
                    result.add(isAPropId);
                }
            }
        }

        List<String> r
                = this.knowledgeSource.readIsAPropIds(propId);
        for (String def : r) {
            if (!propositionDefinitionsMap.containsKey(def)) {
                result.add(def);
            }
        }

        return result;
    }

    @Override
    public List<String> readSubContextOfPropIds(String propId) throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<String> result = new ArrayList<>();
        if (this.subContextOfMap.containsKey(propId)) {
            List<String> propIds
                    = this.subContextOfMap.get(propId);

            if (propIds != null) {
                for (String subContextOfId : propIds) {
                    result.add(subContextOfId);
                }
            }
        }
        List<String> r
                = this.knowledgeSource.readSubContextOfPropIds(propId);
        for (String def : r) {
            if (!propositionDefinitionsMap.containsKey(def)) {
                result.add(def);
            }
        }
        return result;
    }

    @Override
    public List<String> readSubContextOfPropIds(ContextDefinition contextDef) throws KnowledgeSourceReadException {
        if (contextDef == null) {
            throw new IllegalArgumentException("contextDef cannot be null");
        }
        return readSubContextOfPropIds(contextDef.getId());
    }

    @Override
    public List<String> readInducesPropIds(String propId) throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        List<String> result = new ArrayList<>();
        if (this.inducesMap.containsKey(propId)) {
            List<String> propIds = this.inducesMap.get(propId);
            if (propIds != null) {
                result.addAll(propIds);
            }
        }
        List<String> r = this.knowledgeSource.readInducesPropIds(propId);
        for (String def : r) {
            if (!propositionDefinitionsMap.containsKey(def)) {
                result.add(def);
            }
        }
        return result;
    }

    @Override
    public List<String> readInducesPropIds(TemporalPropositionDefinition propDef) throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        return readInducesPropIds(propDef.getId());
    }

    @Override
    public List<String> readParentPropIds(PropositionDefinition propDef) throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        return readParentPropIds(propDef.getId());
    }

    @Override
    public List<String> readParentPropIds(String propId) throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        if (this.propositionDefinitionsMap.containsKey(propId)) {
            List<String> result
                    = new ArrayList<>();
            result.addAll(readIsAPropIds(propId));
            result.addAll(readAbstractedIntoPropIds(propId));
            return result;
        } else {
            return this.knowledgeSource.readParentPropIds(propId);
        }
    }

    @Override
    public List<String> getMatchingPropIds(String searchKey) throws KnowledgeSourceReadException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
