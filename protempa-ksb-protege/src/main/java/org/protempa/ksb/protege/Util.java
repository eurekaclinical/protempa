package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.AbstractAbstractionDefinition;
import org.protempa.AbstractPropositionDefinition;
import org.protempa.SimpleGapFunction;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.ValueFactory;

/**
 * Utility class for classes in <code>edu.virginia.pbhs.protempa.protege</code>.
 * 
 * @author Andrew Post
 */
class Util {

	/*
	 * Allowed time constraint units in the Protege knowledge base.
	 */

	/**
	 * Minutes (60 * 1000 milliseconds).
	 */
	private static final String MINUTE = "Minute";

	/**
	 * Hours (60 * 60 * 1000 milliseconds).
	 */
	private static final String HOUR = "Hour";

	/**
	 * Days (24 * 60 * 60 * 1000 milliseconds).
	 */
	private static final String DAY = "Day";

	static final Map<String, AbsoluteTimeUnit> ABSOLUTE_DURATION_MULTIPLIER = new HashMap<String, AbsoluteTimeUnit>();
	static {
		ABSOLUTE_DURATION_MULTIPLIER.put(MINUTE, AbsoluteTimeUnit.MINUTE);
		ABSOLUTE_DURATION_MULTIPLIER.put(HOUR, AbsoluteTimeUnit.HOUR);
		ABSOLUTE_DURATION_MULTIPLIER.put(DAY, AbsoluteTimeUnit.DAY);
	}

	static final Map<String, RelativeHourUnit> RELATIVE_HOURS_DURATION_MULTIPLIER = new HashMap<String, RelativeHourUnit>();
	static {
		RELATIVE_HOURS_DURATION_MULTIPLIER.put(HOUR, RelativeHourUnit.HOUR);
	}

	static final Map<String, ValueFactory> VALUE_CLASS_NAME_TO_VALUE_FACTORY = new HashMap<String, ValueFactory>();
	static {
		VALUE_CLASS_NAME_TO_VALUE_FACTORY.put("Value", ValueFactory.VALUE);
		VALUE_CLASS_NAME_TO_VALUE_FACTORY.put("NominalValue",
				ValueFactory.NOMINAL);
		VALUE_CLASS_NAME_TO_VALUE_FACTORY.put("OrdinalValue",
				ValueFactory.ORDINAL);
		VALUE_CLASS_NAME_TO_VALUE_FACTORY.put("NumericalValue",
				ValueFactory.NUMERICAL);
		VALUE_CLASS_NAME_TO_VALUE_FACTORY.put("DoubleValue",
				ValueFactory.NUMBER);
		VALUE_CLASS_NAME_TO_VALUE_FACTORY.put("InequalityDoubleValue",
				ValueFactory.INEQUALITY);
	}

	private Util() {

	}

	private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(Util.class
				.getPackage().getName());
	}

	static Logger logger() {
		return LazyLoggerHolder.instance;
	}

	/**
	 * Calculates a time constraint in milliseconds from a pair of time
	 * constraint and units values in a Protege instance.
	 * 
	 * @param instance
	 *            a Protege <code>Instance</code> object.
	 * @param constraint
	 *            a time constraint slot name. The named slot is expected to
	 *            have an Integer value.
	 * @param constraintUnits
	 *            a time constraint units slot name. May have the values
	 *            "Minute", "Hour", or "Day".
	 * @return a <code>Weight</code> object representing a time in
	 *         milliseconds.
	 */
	static Integer parseTimeConstraint(Instance instance, String constraint) {
		Integer constraintValue = null;
		if (instance != null && constraint != null) {
			constraintValue = (Integer) instance.getOwnSlotValue(instance
					.getKnowledgeBase().getSlot(constraint));
		}

		return constraintValue;
	}

	static Unit parseUnitsConstraint(Instance instance, String constraintUnits,
			ProtegeKnowledgeSourceBackend backend) {
		String constraintUnitsValue = null;
		if (instance != null && constraintUnits != null) {
			constraintUnitsValue = (String) instance.getOwnSlotValue(instance
					.getKnowledgeBase().getSlot(constraintUnits));
		}

		if (constraintUnitsValue == null) {
			return null;
		} else {
			return backend.parseUnit(constraintUnitsValue);
		}
	}

	/**
	 * @param instance
	 * @param d
	 */
	static void setGap(Instance instance, AbstractAbstractionDefinition d,
			ProtegeKnowledgeSourceBackend backend) {
		Integer maxGap = (Integer) instance.getOwnSlotValue(instance
				.getKnowledgeBase().getSlot("maxGap"));
		Unit maxGapUnits = Util.parseUnitsConstraint(instance, "maxGapUnits",
				backend);
		d.setGapFunction(new SimpleGapFunction(maxGap, maxGapUnits));
	}

	static void setNames(Instance complexAbstractionInstance,
			AbstractPropositionDefinition cad) {
		cad.setDisplayName((String) complexAbstractionInstance
				.getOwnSlotValue(complexAbstractionInstance.getKnowledgeBase()
						.getSlot("displayName")));
		cad.setAbbreviatedDisplayName((String) complexAbstractionInstance
				.getOwnSlotValue(complexAbstractionInstance.getKnowledgeBase()
						.getSlot("abbrevDisplayName")));
	}

	static void setInverseIsAs(Instance propInstance,
			AbstractPropositionDefinition propDef) {
		Collection<?> isas = propInstance.getDirectOwnSlotValues(propInstance
				.getKnowledgeBase().getSlot("inverseIsA"));
		if (isas != null) {
			String[] inverseIsAs = new String[isas.size()];
			int i = 0;
			for (Object isAInstance : isas) {
				inverseIsAs[i++] = ((Instance) isAInstance).getName();
			}
			propDef.setInverseIsA(inverseIsAs);
		}
	}

}
