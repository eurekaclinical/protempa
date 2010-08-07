package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import org.protempa.KnowledgeBase;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionValueDefinition;
import org.protempa.SlidingWindowWidthMode;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueFormat;

/**
 * 
 * @author Andrew Post
 */
class LowLevelAbstractionConverter implements PropositionConverter {

	/**
	 * 
	 */
	LowLevelAbstractionConverter() {
	}

	public void convert(Instance simpleAbstractionInstance,
			org.protempa.KnowledgeBase protempaKnowledgeBase,
			ProtegeKnowledgeSourceBackend backend) {
        assert simpleAbstractionInstance != null :
            "simpleAbstractionInstance cannot be null";
        assert protempaKnowledgeBase != null :
            "protempaKnowledgeBase cannot be null";
        assert backend != null : "backend cannot be null";

        LowLevelAbstractionDefinition d = constructDetector(
                simpleAbstractionInstance, protempaKnowledgeBase,
                backend);
        setGapBetweenValues(simpleAbstractionInstance, d, backend);
        setPatternLength(simpleAbstractionInstance, d);
        if (d != null) {
            for (Iterator<?> itr = simpleAbstractionInstance
                    .getOwnSlotValues(
                            simpleAbstractionInstance.getKnowledgeBase()
                                    .getSlot("allowedValues")).iterator(); itr
                    .hasNext();) {

                Instance allowedValue = (Instance) itr.next();
                constructDetectorValue(d, allowedValue, backend);
            }
        }
	}

	public boolean protempaKnowledgeBaseHasProposition(
			Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
		return protempaKnowledgeBase
					.hasAbstractionDefinition(protegeParameter.getName());
	}

	/**
	 * @param simpleAbstractionInstance
     * @param protempaKnowledgeBase
	 * @param config
	 * @param backend
	 */
	private static LowLevelAbstractionDefinition constructDetector(
			Instance simpleAbstractionInstance,
			org.protempa.KnowledgeBase protempaKnowledgeBase,
			ProtegeKnowledgeSourceBackend backend) {
		LowLevelAbstractionDefinition result = null;
		if (!protempaKnowledgeBase
						.hasAbstractionDefinition(simpleAbstractionInstance
								.getName())) {
			LowLevelAbstractionDefinition d = new LowLevelAbstractionDefinition(
					protempaKnowledgeBase, simpleAbstractionInstance.getName());
			Util.setNames(simpleAbstractionInstance, d);
			Util.setInverseIsAs(simpleAbstractionInstance, d);
			Util.setGap(simpleAbstractionInstance, d, backend);
			setDuration(simpleAbstractionInstance, d, backend);
			setValueType(simpleAbstractionInstance, d);
			Instance algoIntf = (Instance) simpleAbstractionInstance
					.getOwnSlotValue(simpleAbstractionInstance
							.getKnowledgeBase().getSlot("usingAlgorithm"));
			// set parameter types here?
			if (algoIntf != null) {
				d.setAlgorithmId(algoIntf.getName());
				d.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
			}
			result = d;
		}
		return result;
	}

	/**
	 * @param instance
	 * @param d
	 */
	private static void setDuration(Instance instance,
			LowLevelAbstractionDefinition d,
			ProtegeKnowledgeSourceBackend backend) {
		d.setMinimumDuration(Util.parseTimeConstraint(instance,
				"minDuration"));
		d.setMinimumDurationUnits(Util.parseUnitsConstraint(instance,
				"minDurationUnits", backend));
		d.setMaximumDuration(Util.parseTimeConstraint(instance,
				"maxDuration"));
		d.setMaximumDurationUnits(Util.parseUnitsConstraint(instance,
				"maxDurationUnits", backend));
	}

	/**
	 * @param llad
	 * @param allowedValue
     * @param config
	 * @param backend
	 */
	private static void constructDetectorValue(
			LowLevelAbstractionDefinition llad, Instance allowedValue,
			ProtegeKnowledgeSourceBackend backend) {
		if (llad != null && allowedValue != null) {
			LowLevelAbstractionValueDefinition d = new LowLevelAbstractionValueDefinition(
					llad, allowedValue.getName());
			d.setValue(new NominalValue((String) allowedValue
					.getOwnSlotValue(allowedValue.getKnowledgeBase().getSlot(
							"displayName"))));
			d.setParameterValue("minThreshold", ValueFormat
					.parse((String) allowedValue.getOwnSlotValue(allowedValue
							.getKnowledgeBase().getSlot("minValThreshold"))));
			d.setParameterValue("maxThreshold", ValueFormat
					.parse((String) allowedValue.getOwnSlotValue(allowedValue
							.getKnowledgeBase().getSlot("maxValThreshold"))));
			setThresholdComps(d, allowedValue);
		}
	}

	/**
	 * @param valueTypeP
	 * @param abstractedFroms
	 * @param d
	 * @return
	 */
	private static Cls readAndSetTypes(Slot valueTypeP,
			Collection<?> abstractedFroms, LowLevelAbstractionDefinition d) {
		Cls finalValueTypeAF = null;
		boolean valueTypeConsistent = true;
		for (Iterator<?> itr3 = abstractedFroms.iterator(); itr3.hasNext();) {
			Instance abstractedFrom = (Instance) itr3.next();
			String abstractedFromName = abstractedFrom.getName();
			d.addPrimitiveParameterId(abstractedFromName);
			Cls valueTypeAF = (Cls) abstractedFrom.getOwnSlotValue(valueTypeP);
			if (finalValueTypeAF == null) {
				finalValueTypeAF = valueTypeAF;
			} else {
				valueTypeConsistent = finalValueTypeAF.equals(valueTypeAF);
				if (!valueTypeConsistent) {
					throw new IllegalArgumentException(
							"value types inconsistent");
				}
			}
		}
		return finalValueTypeAF;
	}

	/**
	 * @param instance
	 * @param d
	 */
	private static void setGapBetweenValues(Instance abstractParameter,
			LowLevelAbstractionDefinition d,
			ProtegeKnowledgeSourceBackend backend) {
		d.setMinimumGapBetweenValues(Util.parseTimeConstraint(
				abstractParameter, "minGapValues"));
		d.setMinimumGapBetweenValuesUnits(Util.parseUnitsConstraint(
				abstractParameter, "minGapValuesUnits", backend));
		d.setMaximumGapBetweenValues(Util.parseTimeConstraint(
				abstractParameter, "maxGapValues"));
		d.setMaximumGapBetweenValuesUnits(Util.parseUnitsConstraint(
				abstractParameter, "maxGapValuesUnits", backend));
	}

	/**
	 * @param abstractParameterValue
	 * @param d
	 */
	private static void setPatternLength(Instance abstractParameter,
			LowLevelAbstractionDefinition d) {
		Integer minNumVal = (Integer) abstractParameter
				.getOwnSlotValue(abstractParameter.getKnowledgeBase().getSlot(
						"minValues"));
		Integer maxNumVal = (Integer) abstractParameter
				.getOwnSlotValue(abstractParameter.getKnowledgeBase().getSlot(
						"maxValues"));
		if (minNumVal != null || maxNumVal != null) {
			d.setSlidingWindowWidthMode(SlidingWindowWidthMode.RANGE);
		}
		if (minNumVal != null) {
			d.setMinimumNumberOfValues(minNumVal);
		}
		if (maxNumVal != null) {
			d.setMaximumNumberOfValues(maxNumVal);
		}
	}

	/**
	 * @param model
	 * @param instance
	 * @param d
	 */
	private static void setValueType(Instance instance,
			LowLevelAbstractionDefinition d) {
		Cls finalValueTypeAF = readAndSetTypes(instance.getKnowledgeBase()
				.getSlot("valueType"), instance.getOwnSlotValues(instance
				.getKnowledgeBase().getSlot("abstractedFrom")), d);
		if (finalValueTypeAF != null) {
			d
					.setValueType((ValueFactory) Util.VALUE_CLASS_NAME_TO_VALUE_FACTORY
							.get(finalValueTypeAF.getName()));
		}
	}

	private static final Map<String, ValueComparator> STRING_TO_VAL_COMP_MAP = new HashMap<String, ValueComparator>();
	static {
		STRING_TO_VAL_COMP_MAP.put("eq", ValueComparator.EQUAL_TO);
		STRING_TO_VAL_COMP_MAP.put("gt", ValueComparator.GREATER_THAN);
		STRING_TO_VAL_COMP_MAP.put("gte",
				ValueComparator.GREATER_THAN_OR_EQUAL_TO);
		STRING_TO_VAL_COMP_MAP.put("lt", ValueComparator.LESS_THAN);
		STRING_TO_VAL_COMP_MAP
				.put("lte", ValueComparator.LESS_THAN_OR_EQUAL_TO);
	}

	private static void setThresholdComps(LowLevelAbstractionValueDefinition d,
			Instance extendedParameterConstraint) {
		d.setParameterComp("minThreshold", STRING_TO_VAL_COMP_MAP
				.get(extendedParameterConstraint
						.getOwnSlotValue(extendedParameterConstraint
								.getKnowledgeBase().getSlot("minValComp"))));
		d.setParameterComp("maxThreshold", STRING_TO_VAL_COMP_MAP
				.get(extendedParameterConstraint
						.getOwnSlotValue(extendedParameterConstraint
								.getKnowledgeBase().getSlot("maxValComp"))));
	}
}
