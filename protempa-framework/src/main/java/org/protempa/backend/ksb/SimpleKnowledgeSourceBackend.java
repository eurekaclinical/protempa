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
package org.protempa.backend.ksb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;
import org.protempa.AbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaUtil;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

public final class SimpleKnowledgeSourceBackend
        extends AbstractKnowledgeSourceBackend {

    private Map<String, PropositionDefinition> propDefsMap;
    private Map<String, AbstractionDefinition> abstractionDefsMap;
    private Map<String, TemporalPropositionDefinition> tempPropDefsMap;
    private Map<String, ContextDefinition> contextDefsMap;
    private final Map<String, List<String>> isAMap;
    private final Map<String, List<String>> abstractedIntoMap;
    private final Map<String, List<String>> inducesMap;
    private final Map<String, List<String>> subContextOfMap;

    public SimpleKnowledgeSourceBackend() {
        this.propDefsMap = new HashMap<>();
        this.abstractionDefsMap = new HashMap<>();
        this.isAMap = new HashMap<>();
        this.abstractedIntoMap = new HashMap<>();
        this.contextDefsMap = new HashMap<>();
        this.inducesMap = new HashMap<>();
        this.subContextOfMap = new HashMap<>();
        this.tempPropDefsMap = new HashMap<>();
    }

    public SimpleKnowledgeSourceBackend(PropositionDefinition... propDefs) {
        this();
        for (PropositionDefinition propDef : propDefs) {
            String propId = propDef.getId();
            propDefsMap.put(propId, propDef);
            if (propDef instanceof AbstractionDefinition) {
                AbstractionDefinition ad = (AbstractionDefinition) propDef;
                abstractionDefsMap.put(ad.getId(), ad);
                for (String abstractedFromPropId : ad.getAbstractedFrom()) {
                    Collections.putList(this.abstractedIntoMap,
                            abstractedFromPropId, propId);
                }
            }
            if (propDef instanceof TemporalPropositionDefinition) {
                TemporalPropositionDefinition tpd = (TemporalPropositionDefinition) propDef;
                tempPropDefsMap.put(tpd.getId(), tpd);
            }
            if (propDef instanceof ContextDefinition) {
                ContextDefinition cd = (ContextDefinition) propDef;
                for (TemporalExtendedPropositionDefinition tepd : cd.getInducedBy()) {
                    Collections.putList(this.inducesMap, tepd.getPropositionId(), propId);
                }
                for (String tempPropId : cd.getSubContexts()) {
                    Collections.putList(this.subContextOfMap, tempPropId, propId);
                }
                this.contextDefsMap.put(cd.getId(), cd);
            }
            for (String inverseIsAPropId : propDef.getInverseIsA()) {
                Collections.putList(this.isAMap, inverseIsAPropId, propId);
            }
        }
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        super.initialize(config);
    }

    /**
     * Make public so that tests can call it.
     *
     * @see AbstractKnowledgeSourceBackend
     */
    @Override
    public void fireKnowledgeSourceBackendUpdated() {
        super.fireKnowledgeSourceBackendUpdated();
    }

    @Override
    public String getDisplayName() {
        return "Simple Knowledge Source Backend";
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.propDefsMap.get(id);
    }

    @Override
    public List<PropositionDefinition> readPropositionDefinitions(String[] ids) throws KnowledgeSourceReadException {
        List<PropositionDefinition> result = new ArrayList<>();
        for (String id : ids) {
            PropositionDefinition ad = this.propDefsMap.get(id);
            if (ad == null) {
                throw new KnowledgeSourceReadException("No proposition definition with id " + id);
            }
            result.add(ad);
        }
        return result;
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id)
            throws KnowledgeSourceReadException {
        return this.abstractionDefsMap.get(id);
    }

    @Override
    public List<AbstractionDefinition> readAbstractionDefinitions(String[] ids) throws KnowledgeSourceReadException {
        List<AbstractionDefinition> result = new ArrayList<>();
        for (String id : ids) {
            AbstractionDefinition ad = this.abstractionDefsMap.get(id);
            if (ad == null) {
                throw new KnowledgeSourceReadException("No abstraction definition with id " + id);
            }
            result.add(ad);
        }
        return result;
    }
    
    @Override
    public String[] readAbstractedInto(String propId) {
        List<String> propIds = this.abstractedIntoMap.get(propId);
        if (propIds == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            return propIds.toArray(new String[propIds.size()]);
        }
    }

    @Override
    public String[] readIsA(String propId) {
        List<String> propIds = this.isAMap.get(propId);
        if (propIds == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            return propIds.toArray(new String[propIds.size()]);
        }
    }

    @Override
    public ContextDefinition readContextDefinition(String id) throws KnowledgeSourceReadException {
        return this.contextDefsMap.get(id);
    }

    @Override
    public List<ContextDefinition> readContextDefinitions(String[] ids) throws KnowledgeSourceReadException {
        List<ContextDefinition> result = new ArrayList<>();
        for (String id : ids) {
            ContextDefinition ad = this.contextDefsMap.get(id);
            if (ad == null) {
                throw new KnowledgeSourceReadException("No context definition with id " + id);
            }
            result.add(ad);
        }
        return result;
    }

    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return this.tempPropDefsMap.get(id);
    }

    @Override
    public List<TemporalPropositionDefinition> readTemporalPropositionDefinitions(String[] ids) throws KnowledgeSourceReadException {
        List<TemporalPropositionDefinition> result = new ArrayList<>();
        for (String id : ids) {
            TemporalPropositionDefinition ad = this.tempPropDefsMap.get(id);
            if (ad == null) {
                throw new KnowledgeSourceReadException("No temporal proposition definition with id " + id);
            }
            result.add(ad);
        }
        return result;
    }

    @Override
    public String[] readInduces(String propId) throws KnowledgeSourceReadException {
        List<String> propIds = this.inducesMap.get(propId);
        if (propIds == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            return propIds.toArray(new String[propIds.size()]);
        }
    }

    @Override
    public String[] readSubContextOfs(String propId) throws KnowledgeSourceReadException {
        List<String> propIds = this.subContextOfMap.get(propId);
        if (propIds == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            return propIds.toArray(new String[propIds.size()]);
        }
    }

    @Override
    public Set<String> getKnowledgeSourceSearchResults(String searchKey)
            throws KnowledgeSourceReadException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> collectPropIdDescendantsUsingAllNarrower(boolean inDataSourceOnly, String[] propIds) {
        return collectSubtreePropositionIdsInt(propIds, true, inDataSourceOnly);
    }
    
    @Override
    public Collection<String> collectPropIdDescendantsUsingInverseIsA(String[] propIds) {
        return collectSubtreePropositionIdsInt(propIds, false, false);
    }

    private Collection<String> collectSubtreePropositionIdsInt(String[] propIds, boolean narrower, boolean inDataSource) {
        Set<String> result = new HashSet<>(Arrays.asSet(propIds));
        Queue<String> queue = new LinkedList<>();
        queue.addAll(result);
        while (!queue.isEmpty()) {
            String propId = queue.poll();
            PropositionDefinition pd = this.propDefsMap.get(propId);
            if (!inDataSource || pd.getInDataSource()) {
                result.add(propId);
            }
            if (narrower) {
                Arrays.addAll(queue, pd.getChildren());
            } else {
                Arrays.addAll(queue, pd.getInverseIsA());
            }
        }
        return result;
    }

    @Override
    public Collection<PropositionDefinition> collectPropDefDescendantsUsingAllNarrower(boolean inDataSourceOnly, String[] propIds) {
        return collectSubtreePropositionDefinitionsInt(propIds, true, inDataSourceOnly);
    }
    
    @Override
    public Collection<PropositionDefinition> collectPropDefDescendantsUsingInverseIsA(String[] propIds) {
        return collectSubtreePropositionDefinitionsInt(propIds, false, false);
    }

    private Collection<PropositionDefinition> collectSubtreePropositionDefinitionsInt(String[] propIds, boolean narrower, boolean inDataSource) {
        ProtempaUtil.checkArray(propIds, "propDefs");
        Set<PropositionDefinition> propResult = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        for (String pd : propIds) {
            queue.add(pd);
        }
        while (!queue.isEmpty()) {
            String propId = queue.poll();
            PropositionDefinition pd = this.propDefsMap.get(propId);
            if (!inDataSource || pd.getInDataSource()) {
                propResult.add(pd);
            }
            if (narrower) {
                Arrays.addAll(queue, pd.getChildren());
            } else {
                Arrays.addAll(queue, pd.getInverseIsA());
            }
        }
        return propResult;
    }
}
