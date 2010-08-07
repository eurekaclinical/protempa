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

	public void convert(Instance complexAbstractionInstance,
			KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) {

		if (complexAbstractionInstance != null
				&& protempaKnowledgeBase != null
				&& !protempaKnowledgeBase
						.hasAbstractionDefinition(complexAbstractionInstance
								.getName())) {

			HighLevelAbstractionDefinition cad =
                    new HighLevelAbstractionDefinition(
					protempaKnowledgeBase,
                    complexAbstractionInstance.getName());
			Util.setNames(complexAbstractionInstance, cad);
			Util.setInverseIsAs(complexAbstractionInstance, cad);
			Util.setGap(complexAbstractionInstance, cad, backend);
			Map<Instance, TemporalExtendedPropositionDefinition>
                    extendedParameterCache =
                    new HashMap<Instance,
                    TemporalExtendedPropositionDefinition>();
			addComponentAbstractionDefinitions(complexAbstractionInstance, cad,
					extendedParameterCache, backend);
			addRelationships(extendedParameterCache,
					complexAbstractionInstance, cad, backend);
			setTemporalOffsets(complexAbstractionInstance, cad, backend);
		}
	}

	private static void setTemporalOffsets(Instance complexAbstractionInstance,
			HighLevelAbstractionDefinition cad,
            ProtegeKnowledgeSourceBackend backend) {
		Instance temporalOffsetInstance = (Instance) complexAbstractionInstance
				.getOwnSlotValue(complexAbstractionInstance.getKnowledgeBase()
						.getSlot("temporalOffsets"));
		if (temporalOffsetInstance != null) {
			Offsets temporalOffsets = new Offsets();
			Instance startExtendedParamInstance =
                    (Instance) temporalOffsetInstance
					.getOwnSlotValue(temporalOffsetInstance.getKnowledgeBase()
							.getSlot("startExtendedProposition"));
			Instance finishExtendedParamInstance =
                    (Instance) temporalOffsetInstance
					.getOwnSlotValue(temporalOffsetInstance.getKnowledgeBase()
							.getSlot("finishExtendedProposition"));
			if (startExtendedParamInstance != null) {
				String def = propositionId(startExtendedParamInstance);
				if (def != null) {
					temporalOffsets.setStartAbstractParamId(def);
				}
				temporalOffsets
						.setStartAbstractParamValue(
                        extendedParameterValue(startExtendedParamInstance));
			}

			if (finishExtendedParamInstance != null) {
				String def = propositionId(finishExtendedParamInstance);
				if (def != null) {
					temporalOffsets.setFinishAbstractParamId(def);
				}
				temporalOffsets
						.setFinishAbstractParamValue(
                        extendedParameterValue(finishExtendedParamInstance));
			}

			temporalOffsets.setStartIntervalSide(IntervalSide
					.intervalSide((String) temporalOffsetInstance
							.getOwnSlotValue(temporalOffsetInstance
									.getKnowledgeBase().getSlot("startSide"))));
			temporalOffsets
					.setFinishIntervalSide(IntervalSide
							.intervalSide((String) temporalOffsetInstance
									.getOwnSlotValue(temporalOffsetInstance
											.getKnowledgeBase().getSlot(
													"finishSide"))));

			Integer startOffset = Util.parseTimeConstraint(
					temporalOffsetInstance, "startOffset");
			temporalOffsets.setStartOffset(startOffset);

			temporalOffsets.setStartOffsetUnits(Util.parseUnitsConstraint(
					temporalOffsetInstance, "startOffsetUnits", backend));
			Integer finishOffset = Util.parseTimeConstraint(
					temporalOffsetInstance, "finishOffset");
			temporalOffsets.setFinishOffset(finishOffset);
			temporalOffsets.setFinishOffsetUnits(Util.parseUnitsConstraint(
					temporalOffsetInstance, "finishOffsetUnits", backend));
			cad.setTemporalOffset(temporalOffsets);
		}
	}

	public boolean protempaKnowledgeBaseHasProposition(
			Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
		return protegeParameter != null
				&& protempaKnowledgeBase != null
				&& protempaKnowledgeBase
						.hasAbstractionDefinition(protegeParameter.getName());
	}

	/**
	 * Returns the proposition id for an extended proposition definition.
	 *
	 * @param extendedProposition
	 *            an ExtendedProposition.
	 * @return a proposition id {@link String}.
	 */
	private static String propositionId(Instance extendedProposition) {
		Instance proposition = (Instance) extendedProposition
				.getOwnSlotValue(extendedProposition.getKnowledgeBase()
						.getSlot("proposition"));
		if (proposition.hasType(proposition.getKnowledgeBase().getCls(
				"ConstantParameter"))) {
			throw new IllegalStateException(
					"Constant parameters are not yet supported as components of a high level abstraction definition.");
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
	private static boolean isParameter(Instance extendedParameter) {
		Instance proposition = (Instance) extendedParameter
				.getOwnSlotValue(extendedParameter.getKnowledgeBase().getSlot(
						"proposition"));
		if (proposition.hasType(proposition.getKnowledgeBase().getCls(
				"Parameter"))) {
			return true;
		} else {
			return false;
		}
	}

	private static TemporalExtendedPropositionDefinition newTemporalExtendedPropositionDefinition(
			Instance extendedProposition, HighLevelAbstractionDefinition cad,
			ProtegeKnowledgeSourceBackend backend) {
		String ad = propositionId(extendedProposition);

		String displayName = (String) extendedProposition
				.getOwnSlotValue(extendedProposition.getKnowledgeBase()
						.getSlot("displayName"));
		String abbrevDisplayName = (String) extendedProposition
				.getOwnSlotValue(extendedProposition.getKnowledgeBase()
						.getSlot("abbrevDisplayName"));

		TemporalExtendedPropositionDefinition result;
		if (isParameter(extendedProposition)) {
			TemporalExtendedParameterDefinition r =
                    new TemporalExtendedParameterDefinition(ad);
			r.setValue(extendedParameterValue(extendedProposition));
			result = r;
		} else {
			result = new TemporalExtendedPropositionDefinition(ad);
		}

		result.setDisplayName(displayName);
		result.setAbbreviatedDisplayName(abbrevDisplayName);
		result.setMinLength(Util.parseTimeConstraint(extendedProposition,
				"minDuration"));
		result.setMinLengthUnit(Util.parseUnitsConstraint(extendedProposition,
				"minDurationUnits", backend));
		result.setMaxLength(Util.parseTimeConstraint(extendedProposition,
				"maxDuration"));
		result.setMaxLengthUnit(Util.parseUnitsConstraint(extendedProposition,
				"maxDurationUnits", backend));

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
			ProtegeKnowledgeSourceBackend backend) {
		for (Iterator<?> itr = instance.getOwnSlotValues(
				instance.getKnowledgeBase().getSlot("withRelations"))
				.iterator(); itr.hasNext();) {
			Instance relationInstance = (Instance) itr.next();

			Instance lhsExtendedParameter = (Instance) relationInstance
					.getOwnSlotValue(relationInstance.getKnowledgeBase()
							.getSlot("lhs"));
			Instance rhsExtendedParameter = (Instance) relationInstance
					.getOwnSlotValue(relationInstance.getKnowledgeBase()
							.getSlot("rhs"));

			Integer mins1s2 = Util.parseTimeConstraint(relationInstance,
					"mins1s2");
			Unit mins1s2Units = Util.parseUnitsConstraint(relationInstance,
					"mins1s2Units", backend);
			Integer maxs1s2 = Util.parseTimeConstraint(relationInstance,
					"maxs1s2");
			Unit maxs1s2Units = Util.parseUnitsConstraint(relationInstance,
					"maxs1s2Units", backend);
			Integer mins1f2 = Util.parseTimeConstraint(relationInstance,
					"mins1f2");
			Unit mins1f2Units = Util.parseUnitsConstraint(relationInstance,
					"mins1f2Units", backend);
			Integer maxs1f2 = Util.parseTimeConstraint(relationInstance,
					"maxs1f2");
			Unit maxs1f2Units = Util.parseUnitsConstraint(relationInstance,
					"maxs1f2Units", backend);
			Integer minf1s2 = Util.parseTimeConstraint(relationInstance,
					"minf1s2");
			Unit minf1s2Units = Util.parseUnitsConstraint(relationInstance,
					"minf1s2Units", backend);
			Integer maxf1s2 = Util.parseTimeConstraint(relationInstance,
					"maxf1s2");
			Unit maxf1s2Units = Util.parseUnitsConstraint(relationInstance,
					"maxf1s2Units", backend);
			Integer minf1f2 = Util.parseTimeConstraint(relationInstance,
					"minf1f2");
			Unit minf1f2Units = Util.parseUnitsConstraint(relationInstance,
					"minf1f2Units", backend);
			Integer maxf1f2 = Util.parseTimeConstraint(relationInstance,
					"maxf1f2");
			Unit maxf1f2Units = Util.parseUnitsConstraint(relationInstance,
					"maxf1f2Units", backend);

			Relation relation = new Relation(mins1s2, mins1s2Units, maxs1s2,
					maxs1s2Units, mins1f2, mins1f2Units, maxs1f2, maxs1f2Units,
					minf1s2, minf1s2Units, maxf1s2, maxf1s2Units, minf1f2,
					minf1f2Units, maxf1f2, maxf1f2Units);

			TemporalExtendedPropositionDefinition lhsEP = extendedParameterCache
					.get(lhsExtendedParameter);

			TemporalExtendedPropositionDefinition rhsEP = extendedParameterCache
					.get(rhsExtendedParameter);

			cad.setRelation(lhsEP, rhsEP, relation);
		}
	}

	private static Value extendedParameterValue(
            Instance extendedParamInstance) {
		Value result = null;
		String resultStr = null;
		Instance paramConstraint = (Instance) extendedParamInstance
				.getOwnSlotValue(extendedParamInstance.getKnowledgeBase()
						.getSlot("parameterValue"));
		if (paramConstraint != null) {
			resultStr = (String) paramConstraint
					.getOwnSlotValue(paramConstraint.getKnowledgeBase()
							.getSlot("displayName"));
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
			ProtegeKnowledgeSourceBackend backend) {
		Set<Object> extendedParameters = new HashSet<Object>();

		for (Iterator<?> itr = complexAbstractionInstance.getOwnSlotValues(
				complexAbstractionInstance.getKnowledgeBase().getSlot(
						"withRelations")).iterator(); itr.hasNext();) {
			Instance relation = (Instance) itr.next();
			Object lhs = relation.getOwnSlotValue(relation.getKnowledgeBase()
					.getSlot("lhs"));
			if (lhs != null) {
				extendedParameters.add(lhs);
			}
			Object rhs = relation.getOwnSlotValue(relation.getKnowledgeBase()
					.getSlot("rhs"));
			if (rhs != null) {
				extendedParameters.add(rhs);
			}
		}

		for (Iterator<Object> itr = extendedParameters.iterator(); itr
				.hasNext();) {
			Instance extendedParameter = (Instance) itr.next();
			TemporalExtendedPropositionDefinition def =
                    newTemporalExtendedPropositionDefinition(
					extendedParameter, cad, backend);
			extendedParameterCache.put(extendedParameter, def);
			cad.add(def);
		}
	}
}
