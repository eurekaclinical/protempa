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
import org.protempa.TemporalExtendedParameterDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.Offsets;
import org.protempa.proposition.Relation;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.Value;

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
    public void convert(Instance complexAbstractionInstance,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {

        if (complexAbstractionInstance != null
                && protempaKnowledgeBase != null
                && !protempaKnowledgeBase.hasAbstractionDefinition(
                complexAbstractionInstance.getName())) {

            HighLevelAbstractionDefinition cad =
                    new HighLevelAbstractionDefinition(
                    protempaKnowledgeBase,
                    complexAbstractionInstance.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(complexAbstractionInstance, cad, cm);
            Util.setProperties(complexAbstractionInstance, cad, cm);
            Util.setTerms(complexAbstractionInstance, cad, cm);
            Util.setGap(complexAbstractionInstance, cad, backend, cm);
            Map<Instance, TemporalExtendedPropositionDefinition>
                    extendedParameterCache =
                    new HashMap<Instance,
                    TemporalExtendedPropositionDefinition>();
            addComponentAbstractionDefinitions(complexAbstractionInstance, cad,
                    extendedParameterCache, backend);
            addRelationships(extendedParameterCache,
                    complexAbstractionInstance, cad, backend);
            Util.setInverseIsAs(complexAbstractionInstance, cad, cm);
            setTemporalOffsets(complexAbstractionInstance, cad, backend,
                    extendedParameterCache);
        }
    }

    private static void setTemporalOffsets(Instance complexAbstractionInstance,
            HighLevelAbstractionDefinition cad,
            ProtegeKnowledgeSourceBackend backend,
            Map<Instance, TemporalExtendedPropositionDefinition>
                    extendedParameterCache) 
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
                        extendedParameterValue(startExtendedParamInstance, cm));
            }

            if (finishExtendedParamInstance != null) {
                temporalOffsets.setFinishTemporalExtendedPropositionDefinition(
                        extendedParameterCache.get(
                        finishExtendedParamInstance));
                temporalOffsets.setFinishAbstractParamValue(
                        extendedParameterValue(finishExtendedParamInstance,
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

    @Override
    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
        return protegeParameter != null
                && protempaKnowledgeBase != null
                && protempaKnowledgeBase.hasAbstractionDefinition(
                protegeParameter.getName());
    }

    /**
     * Returns the proposition id for an extended proposition definition.
     *
     * @param extendedProposition
     *            an ExtendedProposition.
     * @return a proposition id {@link String}.
     */
    private static String propositionId(Instance extendedProposition) {
        Instance proposition = 
                (Instance) extendedProposition.getOwnSlotValue(
                extendedProposition.getKnowledgeBase().getSlot("proposition"));
        if (proposition.hasType(proposition.getKnowledgeBase().getCls(
                "ConstantParameter"))) {
            throw new IllegalStateException(
                    "Constant parameters are not yet supported as " +
                    "components of a high level abstraction definition.");
        } else {
            return proposition.getName();
        }
    }

    /**
     * Returns whether a Protege instance is a Parameter.
     *
     * @param extendedParameter
     *            a Protege instance that is assumed to be a Proposition.
     * @return <code>true</code> if the provided Protege instance is a
     *         Parameter, <code>false</code> otherwise.
     */
    private static boolean isParameter(Instance extendedParameter,
            ConnectionManager cm) throws KnowledgeSourceReadException {
        Instance proposition = (Instance) cm.getOwnSlotValue(extendedParameter,
                cm.getSlot("proposition"));
        if (proposition.hasType(cm.getCls("Parameter"))) {
            return true;
        } else {
            return false;
        }
    }

    private static TemporalExtendedPropositionDefinition
            newTemporalExtendedPropositionDefinition(
            Instance extendedProposition,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        String ad = propositionId(extendedProposition);

        String displayName = (String) cm.getOwnSlotValue(extendedProposition,
                cm.getSlot("displayName"));
        String abbrevDisplayName =
                (String) cm.getOwnSlotValue(extendedProposition,
                cm.getSlot("abbrevDisplayName"));

        TemporalExtendedPropositionDefinition result;
        if (isParameter(extendedProposition, cm)) {
            TemporalExtendedParameterDefinition r =
                    new TemporalExtendedParameterDefinition(ad);
            r.setValue(extendedParameterValue(extendedProposition, cm));
            result = r;
        } else {
            result = new TemporalExtendedPropositionDefinition(ad);
        }

        result.setDisplayName(displayName);
        result.setAbbreviatedDisplayName(abbrevDisplayName);
        result.setMinLength(Util.parseTimeConstraint(extendedProposition,
                "minDuration", cm));
        result.setMinLengthUnit(Util.parseUnitsConstraint(extendedProposition,
                "minDurationUnits", backend, cm));
        result.setMaxLength(Util.parseTimeConstraint(extendedProposition,
                "maxDuration", cm));
        result.setMaxLengthUnit(Util.parseUnitsConstraint(extendedProposition,
                "maxDurationUnits", backend, cm));

        return result;
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
            Map<Instance, TemporalExtendedPropositionDefinition>
            extendedParameterCache,
            Instance instance, HighLevelAbstractionDefinition cad,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        for (Iterator<?> itr = instance.getOwnSlotValues(
                instance.getKnowledgeBase().getSlot("withRelations"))
                .iterator(); itr.hasNext();) {
            Instance relationInstance = (Instance) itr.next();

            Instance lhsExtendedParameter = 
                    (Instance) cm.getOwnSlotValue(relationInstance,
                    cm.getSlot("lhs"));
            Instance rhsExtendedParameter = 
                    (Instance) cm.getOwnSlotValue(relationInstance,
                    cm.getSlot("rhs"));

            Integer mins1s2 = Util.parseTimeConstraint(relationInstance,
                    "mins1s2", cm);
            Unit mins1s2Units = Util.parseUnitsConstraint(relationInstance,
                    "mins1s2Units", backend, cm);
            Integer maxs1s2 = Util.parseTimeConstraint(relationInstance,
                    "maxs1s2", cm);
            Unit maxs1s2Units = Util.parseUnitsConstraint(relationInstance,
                    "maxs1s2Units", backend, cm);
            Integer mins1f2 = Util.parseTimeConstraint(relationInstance,
                    "mins1f2", cm);
            Unit mins1f2Units = Util.parseUnitsConstraint(relationInstance,
                    "mins1f2Units", backend, cm);
            Integer maxs1f2 = Util.parseTimeConstraint(relationInstance,
                    "maxs1f2", cm);
            Unit maxs1f2Units = Util.parseUnitsConstraint(relationInstance,
                    "maxs1f2Units", backend, cm);
            Integer minf1s2 = Util.parseTimeConstraint(relationInstance,
                    "minf1s2", cm);
            Unit minf1s2Units = Util.parseUnitsConstraint(relationInstance,
                    "minf1s2Units", backend, cm);
            Integer maxf1s2 = Util.parseTimeConstraint(relationInstance,
                    "maxf1s2", cm);
            Unit maxf1s2Units = Util.parseUnitsConstraint(relationInstance,
                    "maxf1s2Units", backend, cm);
            Integer minf1f2 = Util.parseTimeConstraint(relationInstance,
                    "minf1f2", cm);
            Unit minf1f2Units = Util.parseUnitsConstraint(relationInstance,
                    "minf1f2Units", backend, cm);
            Integer maxf1f2 = Util.parseTimeConstraint(relationInstance,
                    "maxf1f2", cm);
            Unit maxf1f2Units = Util.parseUnitsConstraint(relationInstance,
                    "maxf1f2Units", backend, cm);

            Relation relation = new Relation(mins1s2, mins1s2Units, maxs1s2,
                    maxs1s2Units, mins1f2, mins1f2Units, maxs1f2, maxs1f2Units,
                    minf1s2, minf1s2Units, maxf1s2, maxf1s2Units, minf1f2,
                    minf1f2Units, maxf1f2, maxf1f2Units);

            TemporalExtendedPropositionDefinition lhsEP =
                    extendedParameterCache.get(lhsExtendedParameter);

            TemporalExtendedPropositionDefinition rhsEP =
                    extendedParameterCache.get(rhsExtendedParameter);

            cad.setRelation(lhsEP, rhsEP, relation);
        }
    }

    private static Value extendedParameterValue(
            Instance extendedParamInstance, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Value result = null;
        String resultStr = null;
        Instance paramConstraint =
                (Instance) cm.getOwnSlotValue(extendedParamInstance,
                cm.getSlot("parameterValue"));
        if (paramConstraint != null) {
            resultStr = (String) cm.getOwnSlotValue(paramConstraint,
                    cm.getSlot("displayName"));
            if (resultStr != null) {
                result = new NominalValue(resultStr);
            }
        }
        return result;
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
            Map<Instance, TemporalExtendedPropositionDefinition>
            extendedParameterCache,
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
                    newTemporalExtendedPropositionDefinition(
                    extendedParameter, backend);
            extendedParameterCache.put(extendedParameter, def);
            cad.add(def);
        }
    }
}
