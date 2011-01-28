package org.protempa;


import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.PropositionVisitable;

/**
 * Creates a new proposition that has an isA relationship with an existing
 * proposition.
 * 
 * @author Andrew Post
 * 
 */
class InverseIsAConsequence implements Consequence {

    private static final long serialVersionUID = 6157152982863451759L;
    private final String eventId;
    private final DerivationsBuilder listener;

    InverseIsAConsequence(String eventId, 
            DerivationsBuilder listener) {
        this.listener = listener;
        this.eventId = eventId;
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper,
            WorkingMemory workingMemory)
            throws Exception {
        PropositionVisitable o = 
                (PropositionVisitable) workingMemory.getObject(
                knowledgeHelper.getTuple().get(0));
        o.accept(new PropositionCopier(this.eventId, workingMemory,
                this.listener));
    }
}
