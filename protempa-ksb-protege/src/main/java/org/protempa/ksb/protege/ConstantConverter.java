package org.protempa.ksb.protege;


import edu.stanford.smi.protege.model.Instance;
import org.protempa.ConstantDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;

class ConstantConverter implements PropositionConverter {

    @Override
    public void convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        if (protegeProposition != null
                && protempaKnowledgeBase != null
                && !protempaKnowledgeBase.hasConstantDefinition(
                protegeProposition.getName())) {

            ConstantDefinition constantDef = new ConstantDefinition(
                    protempaKnowledgeBase, protegeProposition.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(protegeProposition, constantDef, cm);
            Util.setProperties(protegeProposition, constantDef, cm);
            Util.setInverseIsAs(protegeProposition, constantDef, cm);
        }
    }

    @Override
    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
        return protegeProposition != null
                && protempaKnowledgeBase != null
                && protempaKnowledgeBase.hasConstantDefinition(protegeProposition.getName());
    }
}
