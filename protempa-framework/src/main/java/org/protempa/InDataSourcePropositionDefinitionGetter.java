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

import org.apache.commons.collections4.map.ReferenceMap;
import org.arp.javautil.arrays.Arrays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Andrew Post
 */
class InDataSourcePropositionDefinitionGetter {

    private KnowledgeSource knowledgeSource;
    private final Map<Set<String>, Set<String>> propIdInDataSourceCache;
    private final Map<Set<String>, Set<PropositionDefinition>> 
            propIdPropInDataSourceCache;

    InDataSourcePropositionDefinitionGetter(
            KnowledgeSource knowledgeSource) {
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        this.knowledgeSource = knowledgeSource;
        this.propIdInDataSourceCache = new ReferenceMap<>();
        this.propIdPropInDataSourceCache = new ReferenceMap<>();

    }
    
    Set<String> inDataSourcePropositionIds(String... propIds)
            throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        return inDataSourcePropositionIds(propIdsAsSet,
                this.propIdInDataSourceCache);
    }

    Set<PropositionDefinition> inDataSourcePropositionDefinitions(
            String... propIds) throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        return inDataSourcePropositionDefinitions(propIdsAsSet,
                this.propIdPropInDataSourceCache);
    }

    void clear() {
        this.propIdInDataSourceCache.clear();
        this.propIdPropInDataSourceCache.clear();

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

    private void inDataSourcePropositionIdsHelper(Collection<String> propIds,
            Set<String> result, Set<PropositionDefinition> propResult)
            throws KnowledgeSourceReadException {
        List<PropositionDefinition> propDefs =
                new ArrayList<PropositionDefinition>();
        for (String propId : propIds) {
            PropositionDefinition propDef = 
                    this.knowledgeSource.readPropositionDefinition(propId);
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
                for (PropositionDefinition ad : 
                        this.knowledgeSource.readAbstractedFrom(
                        (AbstractionDefinition) propDef)) {
                    children.add(ad);
                }
            }
            for (PropositionDefinition propId : 
                    this.knowledgeSource.readInverseIsA(propDef)) {
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
}
