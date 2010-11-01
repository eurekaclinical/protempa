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
    public void convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        if (protegeProposition != null
                && protempaKnowledgeBase != null
                && !protempaKnowledgeBase
                        .hasAbstractionDefinition(protegeProposition.getName())) {
            PairDefinition pd = new PairDefinition(protempaKnowledgeBase,
                    protegeProposition.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(protegeProposition, pd, cm);
            Util.setProperties(protegeProposition, pd, cm);
            Util.setTerms(protegeProposition, pd, cm);
            addComponentAbstractionDefinitions(protegeProposition, pd,
                    backend);
            setRelation(protegeProposition, pd, backend);
            Util.setInverseIsAs(protegeProposition, pd, cm);
        }
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
        return protegeParameter != null
                && protempaKnowledgeBase != null
                && protempaKnowledgeBase
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
