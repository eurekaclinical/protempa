package org.protempa.ksb.protege;

import java.util.logging.Level;

import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PairDefinition;
import org.protempa.TemporalExtendedParameterDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.Relation;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.Value;

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
            addRelationships(protegeProposition, pd, backend);
            Util.setInverseIsAs(protegeProposition, pd, cm);
        }
    }

    private void addRelationships(
            Instance protegeProposition, PairDefinition pd,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        Slot slot = cm.getSlot("withRelation");
        Instance relationInstance = (Instance) cm.getOwnSlotValue(
                protegeProposition, slot);

        Integer mins1s2 = Util.parseTimeConstraint(relationInstance, "mins1s2",
                cm);
        Unit mins1s2Units = Util.parseUnitsConstraint(relationInstance,
                "mins1s2Units", backend, cm);
        Integer maxs1s2 = Util.parseTimeConstraint(relationInstance, "maxs1s2",
                cm);
        Unit maxs1s2Units = Util.parseUnitsConstraint(relationInstance,
                "maxs1s2Units", backend, cm);
        Integer mins1f2 = Util.parseTimeConstraint(relationInstance, "mins1f2",
                cm);
        Unit mins1f2Units = Util.parseUnitsConstraint(relationInstance,
                "mins1f2Units", backend, cm);
        Integer maxs1f2 = Util.parseTimeConstraint(relationInstance, "maxs1f2",
                cm);
        Unit maxs1f2Units = Util.parseUnitsConstraint(relationInstance,
                "maxs1f2Units", backend, cm);
        Integer minf1s2 = Util.parseTimeConstraint(relationInstance, "minf1s2",
                cm);
        Unit minf1s2Units = Util.parseUnitsConstraint(relationInstance,
                "minf1s2Units", backend, cm);
        Integer maxf1s2 = Util.parseTimeConstraint(relationInstance, "maxf1s2",
                cm);
        Unit maxf1s2Units = Util.parseUnitsConstraint(relationInstance,
                "maxf1s2Units", backend, cm);
        Integer minf1f2 = Util.parseTimeConstraint(relationInstance, "minf1f2",
                cm);
        Unit minf1f2Units = Util.parseUnitsConstraint(relationInstance,
                "minf1f2Units", backend, cm);
        Integer maxf1f2 = Util.parseTimeConstraint(relationInstance, "maxf1f2",
                cm);
        Unit maxf1f2Units = Util.parseUnitsConstraint(relationInstance,
                "maxf1f2Units", backend, cm);

        Relation relation = new Relation(mins1s2, mins1s2Units, maxs1s2,
                maxs1s2Units, mins1f2, mins1f2Units, maxs1f2, maxs1f2Units,
                minf1s2, minf1s2Units, maxf1s2, maxf1s2Units, minf1f2,
                minf1f2Units, maxf1f2, maxf1f2Units);

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
                newTemporalExtendedPropositionDefinition(lhs, backend);
        TemporalExtendedPropositionDefinition rhsDefinition =
                newTemporalExtendedPropositionDefinition(rhs, backend);
        pd.setLeftHandProposition(lhsDefinition);
        pd.setRightHandProposition(rhsDefinition);
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

    /**
     * Returns the proposition id for an extended proposition definition.
     * 
     * @param extendedProposition
     *            an ExtendedProposition.
     * @return a proposition id {@link String}.
     */
    private static String propositionId(Instance extendedProposition, 
            ConnectionManager cm) throws KnowledgeSourceReadException {
        Instance proposition = (Instance)
                cm.getOwnSlotValue(extendedProposition,
                cm.getSlot("proposition"));
        if (proposition.hasType(cm.getCls("ConstantParameter"))) {
            throw new IllegalStateException(
                    "Constant parameters are not yet supported as "
                            + "components of a pair definition.");
        } else {
            return proposition.getName();
        }
    }

    private static Value extendedParameterValue(Instance extendedParamInstance,
            ConnectionManager cm) throws KnowledgeSourceReadException {
        Value result = null;
        String resultStr = null;
        Instance paramConstraint = (Instance) cm.getOwnSlotValue(
                extendedParamInstance, cm.getSlot("parameterValue"));
        if (paramConstraint != null) {
            resultStr = (String) cm.getOwnSlotValue(paramConstraint,
                    cm.getSlot("displayName"));
            if (resultStr != null) {
                result = new NominalValue(resultStr);
            }
        }
        return result;
    }

    private static TemporalExtendedPropositionDefinition newTemporalExtendedPropositionDefinition(
            Instance extendedProposition, ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        String ad = propositionId(extendedProposition, cm);

        String displayName = (String) cm.getOwnSlotValue(extendedProposition,
                cm.getSlot("displayName"));
        String abbrevDisplayName = (String) cm.getOwnSlotValue(
                extendedProposition, cm.getSlot("abbrevDisplayName"));

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

}
