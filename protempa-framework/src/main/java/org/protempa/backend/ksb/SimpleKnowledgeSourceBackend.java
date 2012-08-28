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
package org.protempa.backend.ksb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.arp.javautil.collections.Collections;
import org.protempa.AbstractionDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.ksb.AbstractKnowledgeSourceBackend;

public final class SimpleKnowledgeSourceBackend
        extends AbstractKnowledgeSourceBackend {
    private Map<String, PropositionDefinition> propDefsMap;
    private Map<String, AbstractionDefinition> abstractionDefsMap;
    private final Map<String, List<String>> isAMap;
    private final Map<String, List<String>> abstractedIntoMap;
    
    public SimpleKnowledgeSourceBackend() {
        this.propDefsMap = new HashMap<String, PropositionDefinition>();
        this.abstractionDefsMap = new HashMap<String, AbstractionDefinition>();
        this.isAMap = new HashMap<String, List<String>>();
        this.abstractedIntoMap = new HashMap<String, List<String>>();
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
            for (String inverseIsAPropId : propDef.getInverseIsA()) {
                Collections.putList(this.isAMap, inverseIsAPropId, propId);
            }
        }
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
    }

    /**
     * Make public so that tests can call it.
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
    public AbstractionDefinition readAbstractionDefinition(String id) 
            throws KnowledgeSourceReadException {
        return this.abstractionDefsMap.get(id);
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
}
