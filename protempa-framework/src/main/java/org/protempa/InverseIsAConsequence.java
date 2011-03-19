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
    
    private final PropositionCopier copier;

    InverseIsAConsequence(String propId, DerivationsBuilder listener) {
        assert propId != null : "propId cannot be null";
        assert listener != null : "listener cannot be null";
        this.copier = new PropositionCopier(propId, listener);
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper,
            WorkingMemory workingMemory)
            throws Exception {
        PropositionVisitable o =
                (PropositionVisitable) workingMemory.getObject(
                knowledgeHelper.getTuple().get(0));
        this.copier.setWorkingMemory(workingMemory);
        o.accept(this.copier);
        this.copier.setWorkingMemory(null);
    }
}
