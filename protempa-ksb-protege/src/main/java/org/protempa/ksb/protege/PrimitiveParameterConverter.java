package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeBase;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.proposition.value.ValueFactory;

/**
 * @author Andrew Post
 */
class PrimitiveParameterConverter implements PropositionConverter {

	/**
	 * 
	 */
	PrimitiveParameterConverter() {

	}

	public void convert(Instance instance,
			org.protempa.KnowledgeBase protempaKnowledgeBase,
			KnowledgeSourceBackend backend) {

		if (instance != null
				&& protempaKnowledgeBase != null
				&& !protempaKnowledgeBase
						.hasPrimitiveParameterDefinition(instance.getName())) {

			PrimitiveParameterDefinition tc = new PrimitiveParameterDefinition(
					protempaKnowledgeBase, instance.getName());
			Util.setNames(instance, tc);
			Util.setInverseIsAs(instance, tc);
			Cls valueType = (Cls) instance.getOwnSlotValue(instance
					.getKnowledgeBase().getSlot("valueType"));
			if (valueType != null) {
				if (valueType.getName().equals("DoubleValue")) {
					tc.setValueFactory(ValueFactory.NUMBER);
				} else if (valueType.getName().equals("InequalityDoubleValue")) {
					tc.setValueFactory(ValueFactory.INEQUALITY);
				} else if (valueType.getName().equals("OrdinalValue")) {
					tc.setValueFactory(ValueFactory.ORDINAL);
				} else {
					tc.setValueFactory(ValueFactory.NOMINAL);
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
	public boolean protempaKnowledgeBaseHasProposition(
			Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
		return protegeParameter != null
				&& protempaKnowledgeBase != null
				&& protempaKnowledgeBase
						.hasPrimitiveParameterDefinition(protegeParameter
								.getName());
	}

}
