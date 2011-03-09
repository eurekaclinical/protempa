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
    public PrimitiveParameterDefinition convert(Instance instance,
            org.protempa.KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {
        PrimitiveParameterDefinition result =
                protempaKnowledgeBase.getPrimitiveParameterDefinition(
                instance.getName());
        if (result == null) {
            result = new PrimitiveParameterDefinition(
                    protempaKnowledgeBase, instance.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(instance, result, cm);
            Util.setInverseIsAs(instance, result, cm);
            Util.setProperties(instance, result, cm);
            Util.setTerms(instance, result, cm);
            Cls valueType = (Cls) cm.getOwnSlotValue(instance, cm.getSlot("valueType"));
            if (valueType != null) {
                result.setValueType(Util.parseValueType(valueType));
            }
        }
        return result;
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
        return protempaKnowledgeBase.hasPrimitiveParameterDefinition(protegeParameter.getName());
    }
}
