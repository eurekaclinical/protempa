package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Cls;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import org.protempa.AbstractAbstractionDefinition;
import org.protempa.AbstractPropositionDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropertyDefinition;
import org.protempa.SimpleGapFunction;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.ValueType;

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
    static final Map<String, ValueType> VALUE_CLASS_NAME_TO_VALUE_TYPE = new HashMap<String, ValueType>();

    static {
        VALUE_CLASS_NAME_TO_VALUE_TYPE.put("Value", ValueType.VALUE);
        VALUE_CLASS_NAME_TO_VALUE_TYPE.put("NominalValue",
                ValueType.NOMINALVALUE);
        VALUE_CLASS_NAME_TO_VALUE_TYPE.put("OrdinalValue",
                ValueType.ORDINALVALUE);
        VALUE_CLASS_NAME_TO_VALUE_TYPE.put("NumericalValue",
                ValueType.NUMERICALVALUE);
        VALUE_CLASS_NAME_TO_VALUE_TYPE
                .put("DoubleValue", ValueType.NUMBERVALUE);
        VALUE_CLASS_NAME_TO_VALUE_TYPE.put("InequalityDoubleValue",
                ValueType.INEQUALITYNUMBERVALUE);
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
     * @return a <code>Weight</code> object representing a time in milliseconds.
     */
    static Integer parseTimeConstraint(Instance instance, String constraint,
            ConnectionManager cm) throws KnowledgeSourceReadException {
        Integer constraintValue = null;
        if (instance != null && constraint != null) {
            constraintValue = (Integer) cm.getOwnSlotValue(instance, cm
                    .getSlot(constraint));
        }

        return constraintValue;
    }

    static Unit parseUnitsConstraint(Instance instance, String constraintUnits,
            ProtegeKnowledgeSourceBackend backend, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        String constraintUnitsValue = null;
        if (instance != null && constraintUnits != null) {
            constraintUnitsValue = (String) cm.getOwnSlotValue(instance, cm
                    .getSlot(constraintUnits));
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
            ProtegeKnowledgeSourceBackend backend, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Integer maxGap = (Integer) cm.getOwnSlotValue(instance, cm
                .getSlot("maxGap"));
        Unit maxGapUnits = Util.parseUnitsConstraint(instance, "maxGapUnits",
                backend, cm);
        d.setGapFunction(new SimpleGapFunction(maxGap, maxGapUnits));
    }

    static void setNames(Instance complexAbstractionInstance,
            AbstractPropositionDefinition cad, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        cad.setDisplayName((String) cm.getOwnSlotValue(
                complexAbstractionInstance, cm.getSlot("displayName")));
        cad.setAbbreviatedDisplayName((String) cm.getOwnSlotValue(
                complexAbstractionInstance, cm.getSlot("abbrevDisplayName")));
    }

    static void setInverseIsAs(Instance propInstance,
            AbstractPropositionDefinition propDef, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Collection<?> isas = propInstance.getDirectOwnSlotValues(cm
                .getSlot("inverseIsA"));
        if (isas != null) {
            String[] inverseIsAs = new String[isas.size()];
            int i = 0;
            for (Object isAInstance : isas) {
                inverseIsAs[i++] = ((Instance) isAInstance).getName();
            }
            propDef.setInverseIsA(inverseIsAs);
        }
    }

    static void setProperties(Instance propInstance,
            AbstractPropositionDefinition d, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Slot propertySlot = cm.getSlot("property");
        Slot valueTypeSlot = cm.getSlot("valueType");
        Collection<?> properties = cm.getOwnSlotValues(propInstance,
                propertySlot);
        PropertyDefinition[] propDefs = new PropertyDefinition[properties
                .size()];
        int i = 0;
        for (Object propertyInstance : properties) {
            Instance inst = (Instance) propertyInstance;
            Cls valueTypeCls = (Cls) cm.getOwnSlotValue(inst, valueTypeSlot);
            PropertyDefinition propDef = new PropertyDefinition(inst.getName(),
                    VALUE_CLASS_NAME_TO_VALUE_TYPE.get(valueTypeCls.getName()));
            propDefs[i] = propDef;
            i++;
        }
        d.setPropertyDefinitions(propDefs);
    }

    static void setTerms(Instance propInstance,
            AbstractPropositionDefinition d, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Slot termSlot = cm.getSlot("term");
        Collection<?> terms = cm.getOwnSlotValues(propInstance, termSlot);
        String[] termIds = new String[terms.size()];
        int i = 0;
        for (Object termInstance : terms) {
            Instance inst = (Instance) termInstance;
            String termId = (String) cm.getOwnSlotValue(inst, cm
                    .getSlot("termId"));
            termIds[i] = termId;
        }
        d.setTermIds(termIds);
    }
}
