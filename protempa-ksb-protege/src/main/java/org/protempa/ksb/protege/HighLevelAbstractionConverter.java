package org.protempa.ksb.protege;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.IntervalSide;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.Offsets;
import org.protempa.PropositionDefinition;
import org.protempa.proposition.Relation;

/**
 * Converts and adds Protege complex abstraction instances to a PROTEMPA
 * knowledge base.
 * 
 * @author Andrew Post
 */
class HighLevelAbstractionConverter implements PropositionConverter {

    HighLevelAbstractionConverter() {
    }

    @Override
    public HighLevelAbstractionDefinition convert(Instance complexAbstractionInstance,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        HighLevelAbstractionDefinition result =
                new HighLevelAbstractionDefinition(
                protempaKnowledgeBase,
                complexAbstractionInstance.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(complexAbstractionInstance, result, cm);
        Util.setProperties(complexAbstractionInstance, result, cm);
        Util.setTerms(complexAbstractionInstance, result, cm);
        Util.setGap(complexAbstractionInstance, result, backend, cm);
        Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache =
                new HashMap<Instance, TemporalExtendedPropositionDefinition>();
        addComponentAbstractionDefinitions(complexAbstractionInstance, result,
                extendedParameterCache, backend);
        addRelationships(extendedParameterCache,
                complexAbstractionInstance, result, backend);
        Util.setInverseIsAs(complexAbstractionInstance, result, cm);
        setTemporalOffsets(complexAbstractionInstance, result, backend,
                extendedParameterCache);
        return result;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
        return protempaKnowledgeBase.getAbstractionDefinition(
                protegeProposition.getName());
    }

    private static void setTemporalOffsets(Instance complexAbstractionInstance,
            HighLevelAbstractionDefinition cad,
            ProtegeKnowledgeSourceBackend backend,
            Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        Instance temporalOffsetInstance =
                (Instance) cm.getOwnSlotValue(complexAbstractionInstance,
                cm.getSlot("temporalOffsets"));
        if (temporalOffsetInstance != null) {
            Offsets temporalOffsets = new Offsets();
            Instance startExtendedParamInstance =
                    (Instance) cm.getOwnSlotValue(temporalOffsetInstance,
                    cm.getSlot("startExtendedProposition"));
            Instance finishExtendedParamInstance =
                    (Instance) cm.getOwnSlotValue(temporalOffsetInstance,
                    cm.getSlot("finishExtendedProposition"));
            if (startExtendedParamInstance != null) {
                temporalOffsets.setStartTemporalExtendedPropositionDefinition(
                        extendedParameterCache.get(
                        startExtendedParamInstance));
                temporalOffsets.setStartAbstractParamValue(
                        Util.extendedParameterValue(startExtendedParamInstance,
                        cm));
            }

            if (finishExtendedParamInstance != null) {
                temporalOffsets.setFinishTemporalExtendedPropositionDefinition(
                        extendedParameterCache.get(
                        finishExtendedParamInstance));
                temporalOffsets.setFinishAbstractParamValue(
                        Util.extendedParameterValue(finishExtendedParamInstance,
                        cm));
            }

            temporalOffsets.setStartIntervalSide(IntervalSide.intervalSide(
                    (String) cm.getOwnSlotValue(temporalOffsetInstance,
                    cm.getSlot("startSide"))));
            temporalOffsets.setFinishIntervalSide(IntervalSide.intervalSide(
                    (String) cm.getOwnSlotValue(temporalOffsetInstance,
                    cm.getSlot("finishSide"))));
            Integer startOffset = Util.parseTimeConstraint(
                    temporalOffsetInstance, "startOffset", cm);
            temporalOffsets.setStartOffset(startOffset);

            temporalOffsets.setStartOffsetUnits(Util.parseUnitsConstraint(
                    temporalOffsetInstance, "startOffsetUnits", backend, cm));
            Integer finishOffset = Util.parseTimeConstraint(
                    temporalOffsetInstance, "finishOffset", cm);
            temporalOffsets.setFinishOffset(finishOffset);
            temporalOffsets.setFinishOffsetUnits(Util.parseUnitsConstraint(
                    temporalOffsetInstance, "finishOffsetUnits", backend, cm));
            cad.setTemporalOffset(temporalOffsets);
        }
    }

    /**
     * Adds all of the temporal relationships in the given Protege complex
     * abstraction instance (specified with the <code>withRelationships</code>
     * slot) to the given PROTEMPA complex abstraction definition.
     *
     * @param instance
     *            a Protege complex abstraction <code>Instance</code>.
     * @param cad
     *            a PROTEMPA <code>ComplexAbstractionDefinition</code> instance.
     */
    private static void addRelationships(
            Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache,
            Instance instance, HighLevelAbstractionDefinition cad,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        for (Iterator<?> itr = instance.getOwnSlotValues(
                instance.getKnowledgeBase().getSlot("withRelations")).iterator(); itr.hasNext();) {
            Instance relationInstance = (Instance) itr.next();

            Instance lhsExtendedParameter =
                    (Instance) cm.getOwnSlotValue(relationInstance,
                    cm.getSlot("lhs"));
            Instance rhsExtendedParameter =
                    (Instance) cm.getOwnSlotValue(relationInstance,
                    cm.getSlot("rhs"));

            Relation relation = Util.instanceToRelation(relationInstance,
                    cm, backend);

            TemporalExtendedPropositionDefinition lhsEP =
                    extendedParameterCache.get(lhsExtendedParameter);

            TemporalExtendedPropositionDefinition rhsEP =
                    extendedParameterCache.get(rhsExtendedParameter);

            cad.setRelation(lhsEP, rhsEP, relation);
        }
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
            Instance complexAbstractionInstance,
            HighLevelAbstractionDefinition cad,
            Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        Set<Object> extendedParameters = new HashSet<Object>();
        ConnectionManager cm = backend.getConnectionManager();
        for (Iterator<?> itr = cm.getOwnSlotValues(complexAbstractionInstance,
                cm.getSlot("withRelations")).iterator(); itr.hasNext();) {
            Instance relation = (Instance) itr.next();
            Object lhs = cm.getOwnSlotValue(relation, cm.getSlot("lhs"));
            if (lhs != null) {
                extendedParameters.add(lhs);
            }
            Object rhs = cm.getOwnSlotValue(relation, cm.getSlot("rhs"));
            if (rhs != null) {
                extendedParameters.add(rhs);
            }
        }

        for (Iterator<Object> itr = extendedParameters.iterator(); itr.hasNext();) {
            Instance extendedParameter = (Instance) itr.next();
            TemporalExtendedPropositionDefinition def =
                    Util.instanceToTemporalExtendedPropositionDefinition(
                    extendedParameter, backend);
            extendedParameterCache.put(extendedParameter, def);
            cad.add(def);
        }
    }
}
