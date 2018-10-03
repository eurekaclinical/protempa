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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.arp.javautil.collections.Collections;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.Proposition;

/**
 * Creates a new proposition that has an isA relationship with an existing
 * proposition.
 * 
 * @author Andrew Post
 * 
 */
class InverseIsAConsequence implements Consequence {

    private static final long serialVersionUID = 6157152982863451759L;
    
    private final Map<String, List<PropositionCopier>> copiersMap;
    
    /**
     * Creates an instance with mappings from propositions to derived
     * propositions with an isA relationship, and a {@link DerivationsBuilder}
     * to maintain links between propositions and derived propositions.
     * 
     * @param inverseIsAPropIdMap a {@link Map<String, List<String>>} from the 
     * id of a proposition to the ids of propositions that are derived from it.
     * Cannot be <code>null</code>.
     * @param listener a {@link DerivationsBuilder}. Cannot be 
     * <code>null</code>.
     */
    InverseIsAConsequence(Map<String, List<String>> inverseIsAPropIdMap, 
            DerivationsBuilder listener) {
        assert inverseIsAPropIdMap != null : 
                "inverseIsAPropIdMap cannot be null";
        assert listener != null : "listener cannot be null";
        this.copiersMap = new HashMap<>();
        for (Map.Entry<String, List<String>> me : 
                inverseIsAPropIdMap.entrySet()) {
            List<String> targetPropIds = me.getValue();
            List<PropositionCopier> pcs = 
                    new ArrayList<>(targetPropIds.size());
            for (String targetPropId : targetPropIds) {
                pcs.add(new PropositionCopier(targetPropId, listener));
            }
            Collections.putListMult(this.copiersMap, me.getKey(), pcs);
        }
    }

    /**
     * Uses the <code>inverseIsAPropIdMap</code> to create one or more
     * derived propositions from the proposition that fired this rule.
     * 
     * @param knowledgeHelper a {@link KnowledgeHelper}.
     * @param workingMemory the {@link WorkingMemory}.
     */
    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper,
            WorkingMemory workingMemory) {
        Proposition prop = (Proposition) workingMemory.getObject(
                knowledgeHelper.getTuple().get(0));
        List<PropositionCopier> copiers = this.copiersMap.get(prop.getId());
        assert copiers != null : "copiers should never be null";
        for (PropositionCopier copier : copiers) {
            copier.grab(knowledgeHelper);
            prop.accept(copier);
            copier.release();
        }
    }
}
