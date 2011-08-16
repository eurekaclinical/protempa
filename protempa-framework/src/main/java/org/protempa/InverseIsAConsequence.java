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
        this.copiersMap = new HashMap<String, List<PropositionCopier>>();
        for (Map.Entry<String, List<String>> me : 
                inverseIsAPropIdMap.entrySet()) {
            List<String> targetPropIds = me.getValue();
            List<PropositionCopier> pcs = 
                    new ArrayList<PropositionCopier>(targetPropIds.size());
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
            copier.grab(workingMemory);
            prop.accept(copier);
            copier.release();
        }
    }
}
