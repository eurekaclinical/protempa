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

import org.arp.javautil.arrays.Arrays;

import java.util.HashSet;
import java.util.Set;
import org.protempa.backend.ksb.KnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
class SubtreePropositionDefinitionGetterRegular {

    private final KnowledgeSource knowledgeSource;
    private final boolean narrower;

    SubtreePropositionDefinitionGetterRegular(
            KnowledgeSource knowledgeSource, boolean narrower) {
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        this.knowledgeSource = knowledgeSource;
        this.narrower = narrower;

    }

    Set<String> subtreePropIds(boolean inDataSourceOnly, String... propIds)
            throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        String[] partialResultArr = propIdsAsSet.toArray(new String[propIdsAsSet.size()]);
        Set<String> result = new HashSet<>();
        for (KnowledgeSourceBackend backend : this.knowledgeSource.getBackends()) {
            if (this.narrower) {
                result.addAll(backend.collectPropIdDescendantsUsingAllNarrower(inDataSourceOnly, partialResultArr));
            } else {
                result.addAll(backend.collectPropIdDescendantsUsingInverseIsA(partialResultArr));
            }
        }
        return result;
    }

    Set<PropositionDefinition> subtreePropDefs(boolean inDataSourceOnly,
            String... propIds) throws KnowledgeSourceReadException {
        Set<String> propIdsAsSet = Arrays.asSet(propIds);
        String[] partialResultArr = propIdsAsSet.toArray(new String[propIdsAsSet.size()]);
        Set<PropositionDefinition> result = new HashSet<>();
        for (KnowledgeSourceBackend backend : this.knowledgeSource.getBackends()) {
            if (this.narrower) {
                result.addAll(backend.collectPropDefDescendantsUsingAllNarrower(inDataSourceOnly, partialResultArr));
            } else {
                result.addAll(backend.collectPropDefDescendantsUsingInverseIsA(partialResultArr));
            }
        }
        return result;
    }

}
