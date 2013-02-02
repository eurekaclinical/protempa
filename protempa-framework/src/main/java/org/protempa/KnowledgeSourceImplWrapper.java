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
 *
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
    private InDataSourcePropositionDefinitionGetter inDataSourceGetter;
    private boolean initialized;

    KnowledgeSourceImplWrapper(KnowledgeSource knowledgeSource,
            PropositionDefinition... propositionDefinitions) {
        super(new KnowledgeSourceBackend[0]);
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        this.knowledgeSource = knowledgeSource;
        this.propositionDefinitionsMap =
                new HashMap<String, PropositionDefinition>();
        this.abstractionDefinitionsMap =
                new HashMap<String, AbstractionDefinition>();
        this.contextDefinitionsMap =
                new HashMap<String, ContextDefinition>();
        this.temporalPropDefsMap =
                new HashMap<String, TemporalPropositionDefinition>();
        this.isAMap = new HashMap<String, List<String>>();
        this.abstractedIntoMap = new HashMap<String, List<String>>();
        this.inducesMap = new HashMap<String, List<String>>();
        this.subContextOfMap = new HashMap<String, List<String>>();
        this.termIdMap = new HashMap<String, List<String>>();
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
                    for (String abstractedFromPropId :
                            ad.getAbstractedFrom()) {
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
            this.inDataSourceGetter =
                    new InDataSourcePropositionDefinitionGetter(this);
            this.initialized = true;
        }
    }

    @Override
    public List<String> getPropositionDefinitionsByTerm(
            And<TermSubsumption> termSubsumptionClause)
            throws KnowledgeSourceReadException {
        if (termSubsumptionClause == null) {
            throw new IllegalArgumentException(
                    "termSubsumptionClause cannot be null");
        }
        initializeIfNeeded();

        List<Set<String>> propIdSets = new ArrayList<Set<String>>();
        for (TermSubsumption ts : termSubsumptionClause.getAnded()) {
            Set<String> subsumpPropIds = new HashSet<String>();
            for (String termId : ts.getTerms()) {
                List<String> propIds = this.termIdMap.get(termId);
                if (propIds != null) {
                    subsumpPropIds.addAll(propIds);
                }
            }
            propIdSets.add(subsumpPropIds);
        }

        Set<String> matchingPropIds =
                org.arp.javautil.collections.Collections.intersection(
                propIdSets);

        List<String> result = new ArrayList<String>();
        result.addAll(matchingPropIds);

        List<String> resultFromKS =
                this.knowledgeSource.getPropositionDefinitionsByTerm(
                termSubsumptionClause);
        for (String propId : resultFromKS) {
            if (!this.propositionDefinitionsMap.containsKey(propId)) {
                result.add(propId);
            }
        }

        return result;
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
    public Set<PropositionDefinition> inDataSourcePropositionDefinitions(
            String... propIds) throws KnowledgeSourceReadException {
        initializeIfNeeded();
        return this.inDataSourceGetter.inDataSourcePropositionDefinitions(
                propIds);
    }

    @Override
    public Set<String> inDataSourcePropositionIds(String... propIds)
            throws KnowledgeSourceReadException {
        initializeIfNeeded();
        return this.inDataSourceGetter.inDataSourcePropositionIds(propIds);
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
        List<TemporalPropositionDefinition> result =
                new ArrayList<TemporalPropositionDefinition>();
        for (TemporalExtendedPropositionDefinition tepd : propDef.getInducedBy()) {
            result.add(readTemporalPropositionDefinition(tepd.getPropositionId()));
        }
        return result;
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
        List<ContextDefinition> result = new ArrayList<ContextDefinition>();
        for (String subContextId : propDef.getSubContexts()) {
            ContextDefinition contextDefinition = readContextDefinition(subContextId);
            if (contextDefinition != null) {
                result.add(contextDefinition);
            } else {
                throw new AssertionError("Context definition " + propDef.getId() + " is invalid because sub-context " + subContextId + " does not exist.");
            }
        }
        return result;
    }
    
    @Override
    public List<PropositionDefinition> readAbstractedFrom(
            AbstractionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        List<PropositionDefinition> result =
                new ArrayList<PropositionDefinition>();
        for (String propId : propDef.getAbstractedFrom()) {
            result.add(readPropositionDefinition(propId));
        }
        return result;
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
        initializeIfNeeded();
        String propId = propDef.getId();
        List<AbstractionDefinition> result =
                new ArrayList<AbstractionDefinition>();
        if (this.abstractedIntoMap.containsKey(propId)) {
            List<String> propIds = this.abstractedIntoMap.get(propDef.getId());

            if (propIds != null) {
                for (String abstractedIntoPropId : propIds) {
                    result.add(
                            readAbstractionDefinition(abstractedIntoPropId));
                }
            }
        }
        List<AbstractionDefinition> r =
                this.knowledgeSource.readAbstractedInto(propDef);
        for (AbstractionDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }
        return result;
    }

    @Override
    public List<AbstractionDefinition> readAbstractedInto(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        PropositionDefinition propDef = readPropositionDefinition(propId);
        if (propDef != null) {
            return readAbstractedInto(propDef);
        } else {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<ContextDefinition> readInduces(
            TemporalPropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        String propId = propDef.getId();
        List<ContextDefinition> result =
                new ArrayList<ContextDefinition>();
        if (this.inducesMap.containsKey(propId)) {
            List<String> propIds = this.inducesMap.get(propDef.getId());

            if (propIds != null) {
                for (String inducesPropId : propIds) {
                    result.add(
                            readContextDefinition(inducesPropId));
                }
            }
        }
        List<ContextDefinition> r =
                this.knowledgeSource.readInduces(propDef);
        for (ContextDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }
        return result;
    }

    @Override
    public List<ContextDefinition> readInduces(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        TemporalPropositionDefinition propDef = 
                readTemporalPropositionDefinition(propId);
        if (propDef != null) {
            return readInduces(propDef);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        AbstractionDefinition result =
                this.abstractionDefinitionsMap.get(id);
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
        List<PropositionDefinition> result =
                new ArrayList<PropositionDefinition>();
        for (String propId : propDef.getInverseIsA()) {
            result.add(readPropositionDefinition(propId));
        }
        return result;
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
            return Collections.emptyList();
        }
    }

    @Override
    public List<PropositionDefinition> readIsA(PropositionDefinition propDef)
            throws KnowledgeSourceReadException {
        if (propDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        String propId = propDef.getId();
        List<PropositionDefinition> result =
                new ArrayList<PropositionDefinition>();
        if (this.isAMap.containsKey(propId)) {
            List<String> propIds = this.isAMap.get(propDef.getId());

            if (propIds != null) {
                for (String isAPropId : propIds) {
                    result.add(
                            readPropositionDefinition(isAPropId));
                }
            }
        }

        List<PropositionDefinition> r =
                this.knowledgeSource.readIsA(propDef);
        for (PropositionDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }

        return result;
    }

    @Override
    public List<PropositionDefinition> readIsA(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        PropositionDefinition propDef = readPropositionDefinition(propId);
        if (propDef != null) {
            return readIsA(propDef);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        PropositionDefinition result =
                this.propositionDefinitionsMap.get(id);
        
        if (result == null) {
            result = this.knowledgeSource.readPropositionDefinition(id);
        }
        return result;
    }
    
    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        initializeIfNeeded();
        TemporalPropositionDefinition result =
                this.temporalPropDefsMap.get(id);
        
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
        ContextDefinition result =
                this.contextDefinitionsMap.get(id);
        
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
        initializeIfNeeded();
        String propId = propDef.getId();
        if (this.propositionDefinitionsMap.containsKey(propId)) {
            List<PropositionDefinition> result =
                    new ArrayList<PropositionDefinition>();
            result.addAll(readIsA(propDef));
            result.addAll(readAbstractedInto(propDef));
            return result;
        } else {
            return this.knowledgeSource.readParents(propDef);
        }
    }

    @Override
    public List<PropositionDefinition> readParents(String propId)
            throws KnowledgeSourceReadException {
        if (propId == null) {
            throw new IllegalArgumentException("propId cannot be null");
        }
        initializeIfNeeded();
        PropositionDefinition propDef = readPropositionDefinition(propId);
        if (propDef != null) {
            return readParents(propDef);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void close() {
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
    public List<ContextDefinition> readSubContextOfs(String id) throws KnowledgeSourceReadException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        ContextDefinition def = readContextDefinition(id);
        if (def != null) {
            return readSubContextOfs(readContextDefinition(id));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ContextDefinition> readSubContextOfs(ContextDefinition contextDef) throws KnowledgeSourceReadException {
        if (contextDef == null) {
            throw new IllegalArgumentException("propDef cannot be null");
        }
        initializeIfNeeded();
        String propId = contextDef.getId();
        List<ContextDefinition> result =
                new ArrayList<ContextDefinition>();
        if (this.subContextOfMap.containsKey(propId)) {
            List<String> propIds = 
                    this.subContextOfMap.get(contextDef.getId());

            if (propIds != null) {
                for (String subContextOfId : propIds) {
                    result.add(
                            readContextDefinition(subContextOfId));
                }
            }
        }
        List<ContextDefinition> r =
                this.knowledgeSource.readSubContextOfs(contextDef);
        for (ContextDefinition def : r) {
            if (!propositionDefinitionsMap.containsKey(def.getId())) {
                result.add(def);
            }
        }
        return result;
    }
}
