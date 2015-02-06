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

import java.util.HashSet;
import java.util.Map;
import org.arp.javautil.arrays.Arrays;

import java.util.Set;
import org.protempa.CollectSubtreeGetterSlowStrategy.InDataSourceResult;

/**
 *
 * @author Andrew Post
 */
class SubtreePropositionDefinitionGetterForWrapper {

    private final CollectSubtreeGetterSlowStrategy inDataSourceSlowStrategy;
    private final CollectSubtreeGetterSlowStrategy collectSubtreeSlowStrategy;
    private final Map<String, PropositionDefinition> propositionDefinitionMap;
    private final KnowledgeSource knowledgeSource;
    private final boolean allNarrower;

    SubtreePropositionDefinitionGetterForWrapper(Map<String, PropositionDefinition> propositionDefinitionMap, KnowledgeSource knowledgeSource, boolean allNarrower) {
        assert propositionDefinitionMap != null : "propositionDefinitionMap cannot be null";
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        this.propositionDefinitionMap = propositionDefinitionMap;
        this.inDataSourceSlowStrategy = new CollectSubtreeGetterSlowStrategy(this.propositionDefinitionMap, true, true);
        this.collectSubtreeSlowStrategy = new CollectSubtreeGetterSlowStrategy(this.propositionDefinitionMap, false, false);
        this.knowledgeSource = knowledgeSource;
        this.allNarrower = allNarrower;
    }

    Set<String> collectPropIds(boolean inDataSource, String... propIds)
            throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        InDataSourceResult<String> partialResult = 
                inDataSource ? 
                    this.inDataSourceSlowStrategy.collectPropIds(propIdsAsSet) :
                    this.collectSubtreeSlowStrategy.collectPropIds(propIdsAsSet);
        Set<String> result = new HashSet<>(partialResult.getResult());
        Set<String> missing = partialResult.getMissing();
        if (this.allNarrower) {
            result.addAll(this.knowledgeSource.collectPropIdDescendantsUsingAllNarrower(inDataSource, missing.toArray(new String[missing.size()])));
        } else {
            result.addAll(this.knowledgeSource.collectPropIdDescendantsUsingInverseIsA(missing.toArray(new String[missing.size()])));
        }
        return result;
    }

    Set<PropositionDefinition> collectPropDefs(boolean inDataSource, String... propIds) throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        InDataSourceResult<PropositionDefinition> partialResult = 
                inDataSource ?
                    this.inDataSourceSlowStrategy.collectPropDefs(propIdsAsSet) :
                    this.collectSubtreeSlowStrategy.collectPropDefs(propIdsAsSet);
        Set<PropositionDefinition> result = new HashSet<>(partialResult.getResult());
        Set<String> missing = partialResult.getMissing();
        String[] partialResultArr = missing.toArray(new String[missing.size()]);
        if (this.allNarrower) {
            result.addAll(this.knowledgeSource.collectPropDefDescendantsUsingAllNarrower(inDataSource, partialResultArr));
        } else {
            result.addAll(this.knowledgeSource.collectPropDefDescendantsUsingInverseIsA(partialResultArr));
        }
        return result;
    }

    void clear() {
        this.inDataSourceSlowStrategy.clear();
    }

    
}
