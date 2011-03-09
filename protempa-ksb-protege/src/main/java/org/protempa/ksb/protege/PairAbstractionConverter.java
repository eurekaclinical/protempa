package org.protempa.ksb.protege;


import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PairDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.Relation;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

public class PairAbstractionConverter implements PropositionConverter {

    @Override
    public PairDefinition convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        PairDefinition result = (PairDefinition) protempaKnowledgeBase.getAbstractionDefinition(protegeProposition.getName());
        if (result == null) {
            result = new PairDefinition(protempaKnowledgeBase,
                    protegeProposition.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(protegeProposition, result, cm);
            Util.setProperties(protegeProposition, result, cm);
            Util.setTerms(protegeProposition, result, cm);
            addComponentAbstractionDefinitions(protegeProposition, result,
                    backend);
            setRelation(protegeProposition, result, backend);
            Util.setInverseIsAs(protegeProposition, result, cm);
        }
        return result;
    }

    private void setRelation(
            Instance protegeProposition, PairDefinition pd,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        Slot slot = cm.getSlot("withRelation");
        Instance relationInstance = (Instance) cm.getOwnSlotValue(
                protegeProposition, slot);

        Relation relation = Util.instanceToRelation(relationInstance,
                    cm, backend);

        pd.setRelation(relation);

    }

    @Override
    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
        return protempaKnowledgeBase
                        .hasAbstractionDefinition(protegeParameter.getName());
    }

    /**
     * Adds all of the abstraction definitions for which the given complex
     * abstraction instance defines a temporal relation to the given complex
     * abstraction definition.
     * 
     * @param complexAbstractionInstance
     *            a Protege complex abstraction <code>Instance</code>.
     * @param cad
     *            a PROTEMPA <code>HighLevelAbstractionDefinition</code>
     *            instance.
     */
    private static void addComponentAbstractionDefinitions(
            Instance pairAbstractionInstance,
            PairDefinition pd,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        Instance relation = (Instance) cm.getOwnSlotValue(
                pairAbstractionInstance, cm.getSlot("withRelation"));
        Instance lhs = (Instance) cm.getOwnSlotValue(relation,
                cm.getSlot("lhs"));
        assert lhs != null : "lhs cannot be null";
        Instance rhs = (Instance) cm.getOwnSlotValue(relation,
                cm.getSlot("rhs"));
        assert rhs != null : "rhs cannot be null";
        TemporalExtendedPropositionDefinition lhsDefinition =
                Util.instanceToTemporalExtendedPropositionDefinition(lhs, backend);
        TemporalExtendedPropositionDefinition rhsDefinition =
                Util.instanceToTemporalExtendedPropositionDefinition(rhs, backend);
        pd.setLeftHandProposition(lhsDefinition);
        pd.setRightHandProposition(rhsDefinition);
    }

}
