package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionValueDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.SlidingWindowWidthMode;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueFormat;
import org.protempa.proposition.value.ValueType;

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

    @Override
    public LowLevelAbstractionDefinition convert(Instance lowLevelAbstractionInstance,
            org.protempa.KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {
        LowLevelAbstractionDefinition d = construct(
                lowLevelAbstractionInstance, protempaKnowledgeBase,
                backend);
        setGapBetweenValues(lowLevelAbstractionInstance, d, backend);
        ConnectionManager cm = backend.getConnectionManager();
        setPatternLength(lowLevelAbstractionInstance, d, cm);
        if (d != null) {
            for (Iterator<?> itr = cm.getOwnSlotValues(lowLevelAbstractionInstance,
                    cm.getSlot(
                    "allowedValues")).iterator(); itr.hasNext();) {

                Instance allowedValue = (Instance) itr.next();
                constructValue(d, allowedValue, cm);
            }
        }
        return d;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
        return protempaKnowledgeBase.getAbstractionDefinition(
                protegeProposition.getName());
    }

    /**
     * @param lowLevelAbstractionInstance
     * @param protempaKnowledgeBase
     * @param config
     * @param backend
     */
    private static LowLevelAbstractionDefinition construct(
            Instance lowLevelAbstractionInstance,
            org.protempa.KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {
        LowLevelAbstractionDefinition result = null;
        if (!protempaKnowledgeBase.hasAbstractionDefinition(lowLevelAbstractionInstance.getName())) {
            LowLevelAbstractionDefinition d = new LowLevelAbstractionDefinition(
                    protempaKnowledgeBase, lowLevelAbstractionInstance.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(lowLevelAbstractionInstance, d, cm);
            Util.setInverseIsAs(lowLevelAbstractionInstance, d, cm);
            Util.setGap(lowLevelAbstractionInstance, d, backend, cm);
            Util.setProperties(lowLevelAbstractionInstance, d, cm);
            Util.setTerms(lowLevelAbstractionInstance, d, cm);
            setDuration(lowLevelAbstractionInstance, d, backend, cm);
            setValueType(lowLevelAbstractionInstance, d, cm);
            Instance algoIntf = (Instance) cm.getOwnSlotValue(lowLevelAbstractionInstance, cm.getSlot("usingAlgorithm"));
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
            ProtegeKnowledgeSourceBackend backend, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        d.setMinimumDuration(Util.parseTimeConstraint(instance,
                "minDuration", cm));
        d.setMinimumDurationUnits(Util.parseUnitsConstraint(instance,
                "minDurationUnits", backend, cm));
        d.setMaximumDuration(Util.parseTimeConstraint(instance,
                "maxDuration", cm));
        d.setMaximumDurationUnits(Util.parseUnitsConstraint(instance,
                "maxDurationUnits", backend, cm));
    }

    /**
     * @param llad
     * @param allowedValue
     * @param config
     * @param backend
     */
    private static void constructValue(
            LowLevelAbstractionDefinition llad, Instance allowedValue,
            ConnectionManager cm) throws KnowledgeSourceReadException {
        if (llad != null && allowedValue != null) {
            LowLevelAbstractionValueDefinition d = new LowLevelAbstractionValueDefinition(
                    llad, allowedValue.getName());
            d.setValue(new NominalValue((String) cm.getOwnSlotValue(allowedValue, cm.getSlot(
                    "displayName"))));
            d.setParameterValue("minThreshold", ValueFormat.parse((String) cm.getOwnSlotValue(allowedValue, cm.getSlot("minValThreshold"))));
            d.setParameterValue("maxThreshold", ValueFormat.parse((String) cm.getOwnSlotValue(allowedValue, cm.getSlot("maxValThreshold"))));
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
            Collection<?> abstractedFroms, LowLevelAbstractionDefinition d,
            ConnectionManager cm) throws KnowledgeSourceReadException {
        Cls finalValueTypeAF = null;
        boolean valueTypeConsistent = true;
        for (Iterator<?> itr3 = abstractedFroms.iterator(); itr3.hasNext();) {
            Instance abstractedFrom = (Instance) itr3.next();
            String abstractedFromName = abstractedFrom.getName();
            d.addPrimitiveParameterId(abstractedFromName);
            Cls valueTypeAF =
                    (Cls) cm.getOwnSlotValue(abstractedFrom, valueTypeP);
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
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        d.setMinimumGapBetweenValues(Util.parseTimeConstraint(
                abstractParameter, "minGapValues", cm));
        d.setMinimumGapBetweenValuesUnits(Util.parseUnitsConstraint(
                abstractParameter, "minGapValuesUnits", backend, cm));
        d.setMaximumGapBetweenValues(Util.parseTimeConstraint(
                abstractParameter, "maxGapValues", cm));
        d.setMaximumGapBetweenValuesUnits(Util.parseUnitsConstraint(
                abstractParameter, "maxGapValuesUnits", backend, cm));
    }

    /**
     * @param abstractParameterValue
     * @param d
     */
    private static void setPatternLength(Instance abstractParameter,
            LowLevelAbstractionDefinition d, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Integer minNumVal = (Integer) cm.getOwnSlotValue(abstractParameter, cm.getSlot(
                "minValues"));
        Integer maxNumVal = (Integer) cm.getOwnSlotValue(abstractParameter, cm.getSlot(
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
            LowLevelAbstractionDefinition d, ConnectionManager cm) throws KnowledgeSourceReadException {
        Cls finalValueTypeAF = readAndSetTypes(cm.getSlot("valueType"), cm.getOwnSlotValues(instance, cm.getSlot("abstractedFrom")), d, cm);
        if (finalValueTypeAF != null) {
            d.setValueType((ValueType) Util.VALUE_CLASS_NAME_TO_VALUE_TYPE.get(finalValueTypeAF.getName()));
        }
    }
    private static final Map<String, ValueComparator> STRING_TO_VAL_COMP_MAP = new HashMap<String, ValueComparator>();

    static {
        STRING_TO_VAL_COMP_MAP.put("eq", ValueComparator.EQUAL_TO);
        STRING_TO_VAL_COMP_MAP.put("gt", ValueComparator.GREATER_THAN);
        STRING_TO_VAL_COMP_MAP.put("gte",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);
        STRING_TO_VAL_COMP_MAP.put("lt", ValueComparator.LESS_THAN);
        STRING_TO_VAL_COMP_MAP.put("lte", ValueComparator.LESS_THAN_OR_EQUAL_TO);
    }

    private static void setThresholdComps(LowLevelAbstractionValueDefinition d,
            Instance extendedParameterConstraint) {
        d.setParameterComp("minThreshold", STRING_TO_VAL_COMP_MAP.get(extendedParameterConstraint.getOwnSlotValue(extendedParameterConstraint.getKnowledgeBase().getSlot("minValComp"))));
        d.setParameterComp("maxThreshold", STRING_TO_VAL_COMP_MAP.get(extendedParameterConstraint.getOwnSlotValue(extendedParameterConstraint.getKnowledgeBase().getSlot("maxValComp"))));
    }
}
