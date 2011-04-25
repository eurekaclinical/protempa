package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.ConstantDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;

class ConstantConverter implements PropositionConverter {

    @Override
    public ConstantDefinition convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConstantDefinition result = new ConstantDefinition(
                protempaKnowledgeBase, protegeProposition.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeProposition, result, cm);
        Util.setInDataSource(protegeProposition, result, cm);
        Util.setProperties(protegeProposition, result, cm);
        Util.setTerms(protegeProposition, result, cm);
        Util.setInverseIsAs(protegeProposition, result, cm);
        return result;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
        return protempaKnowledgeBase.getConstantDefinition(
                protegeProposition.getName());
    }
}
