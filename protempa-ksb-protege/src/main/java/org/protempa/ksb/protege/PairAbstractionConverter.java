package org.protempa.ksb.protege;

import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PairDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.interval.Relation;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import org.protempa.PropositionDefinition;

public class PairAbstractionConverter implements PropositionConverter {

    @Override
    public PairDefinition convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        PairDefinition result = new PairDefinition(protempaKnowledgeBase,
                protegeProposition.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeProposition, result, cm);
        Util.setInDataSource(protegeProposition, result, cm);
        Util.setProperties(protegeProposition, result, cm);
        Util.setTerms(protegeProposition, result, cm);
        Util.setSolid(protegeProposition, result, cm);
        Util.setConcatenable(protegeProposition, result, cm);
        Util.setGap(protegeProposition, result, backend, cm);
        addComponentAbstractionDefinitions(protegeProposition, result,
                backend, cm);
        setRelation(protegeProposition, result, backend, cm);
        setRequireSecond(protegeProposition, result, cm);
        Util.setInverseIsAs(protegeProposition, result, cm);

        return result;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
        return protempaKnowledgeBase.getAbstractionDefinition(
                protegeProposition.getName());
    }

    private void setRelation(
            Instance protegeProposition, PairDefinition pd,
            ProtegeKnowledgeSourceBackend backend, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Slot slot = cm.getSlot("withRelation");
        Instance relationInstance = (Instance) cm.getOwnSlotValue(
                protegeProposition, slot);

        if (relationInstance != null) {
            Relation relation = Util.instanceToRelation(relationInstance,
                    cm, backend);
            pd.setRelation(relation);
        }
    }
    
    private void setRequireSecond(
            Instance protegeProposition, PairDefinition pd,
            ConnectionManager cm) 
            throws KnowledgeSourceReadException {
        Slot slot = cm.getSlot("requireSecond");
        Boolean requireSecond = 
                (Boolean) cm.getOwnSlotValue(protegeProposition, slot);
        assert requireSecond != null : "requireSecond cannot be null!";
        pd.setSecondRequired(requireSecond);
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
            Instance pairAbstractionInstance, PairDefinition pd,
            ProtegeKnowledgeSourceBackend backend, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Instance relation = (Instance) cm.getOwnSlotValue(
                pairAbstractionInstance, cm.getSlot("withRelation"));
        if (relation != null) {
            Instance lhs = (Instance) cm.getOwnSlotValue(relation,
                    cm.getSlot("lhs"));
            assert lhs != null : "lhs cannot be null";
            Instance rhs = (Instance) cm.getOwnSlotValue(relation,
                    cm.getSlot("rhs"));
            assert rhs != null : "rhs cannot be null";
            TemporalExtendedPropositionDefinition lhsDefinition =
                    Util.instanceToTemporalExtendedPropositionDefinition(lhs, 
                    backend);
            TemporalExtendedPropositionDefinition rhsDefinition =
                    Util.instanceToTemporalExtendedPropositionDefinition(rhs, 
                    backend);
            pd.setLeftHandProposition(lhsDefinition);
            pd.setRightHandProposition(rhsDefinition);
        }
    }
}
