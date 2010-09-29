package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.proposition.value.ValueType;

/**
 * @author Andrew Post
 */
class PrimitiveParameterConverter implements PropositionConverter {

    /**
     *
     */
    PrimitiveParameterConverter() {
    }

    @Override
    public void convert(Instance instance,
            org.protempa.KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {

        if (instance != null
                && protempaKnowledgeBase != null
                && !protempaKnowledgeBase.hasPrimitiveParameterDefinition(instance.getName())) {

            PrimitiveParameterDefinition tc = new PrimitiveParameterDefinition(
                    protempaKnowledgeBase, instance.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(instance, tc, cm);
            Util.setInverseIsAs(instance, tc, cm);
            Util.setProperties(instance, tc, cm);
            Cls valueType = (Cls) cm.getOwnSlotValue(instance, cm.getSlot("valueType"));
            if (valueType != null) {
                if (valueType.getName().equals("DoubleValue")) {
                    tc.setValueType(ValueType.NUMBERVALUE);
                } else if (valueType.getName().equals("InequalityDoubleValue")) {
                    tc.setValueType(ValueType.INEQUALITYNUMBERVALUE);
                } else if (valueType.getName().equals("OrdinalValue")) {
                    tc.setValueType(ValueType.ORDINALVALUE);
                } else {
                    tc.setValueType(ValueType.NOMINALVALUE);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.virginia.pbhs.protempa.protege.ProtegeInstanceConverter#hasParameter(edu.stanford.smi.protege.model.Instance,
     *      edu.virginia.pbhs.protempa.KnowledgeBase)
     */
    @Override
    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
        return protegeParameter != null
                && protempaKnowledgeBase != null
                && protempaKnowledgeBase.hasPrimitiveParameterDefinition(protegeParameter.getName());
    }
}
