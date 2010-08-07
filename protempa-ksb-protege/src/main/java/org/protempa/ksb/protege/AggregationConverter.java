package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import org.protempa.AggregationDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.NumericalValue;
import org.protempa.proposition.value.OrdinalValue;
import org.protempa.proposition.value.Value;

/**
 * Converts an aggregation definition from a Protege knowledge base into
 * PROTEMPA's knowledge model.
 * 
 * @author Andrew Post
 */
public class AggregationConverter implements PropositionConverter {

	/**
	 * 
	 */
	AggregationConverter() {
	}

	public void convert(Instance protegeParameter,
			KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) {
		if (protegeParameter != null && protempaKnowledgeBase != null) {
			constructAggregation(protegeParameter, protempaKnowledgeBase,
					backend);
		}
	}

	private void constructAggregation(Instance protegeParameter,
			KnowledgeBase protempaKnowledgeBase,
			ProtegeKnowledgeSourceBackend backend) {
		if (protegeParameter != null && protempaKnowledgeBase != null) {
			AggregationDefinition ad = new AggregationDefinition(
					protempaKnowledgeBase, protegeParameter.getName());
			Util.setNames(protegeParameter, ad);
			Util.setGap(protegeParameter, ad, backend);

			Util.setInverseIsAs(protegeParameter, ad);

			setDuration(protegeParameter, ad, backend);
			setValueType(protegeParameter, ad);
			Instance algoIntf = (Instance) protegeParameter
					.getOwnSlotValue(protegeParameter.getKnowledgeBase()
							.getSlot("usingAlgorithm"));
			if (algoIntf != null) {
				ad.setAlgorithmId(algoIntf.getName());
			}

		}

	}

	/**
	 * @param instance
	 * @param d
	 */
	private static void setDuration(Instance instance, AggregationDefinition d,
			ProtegeKnowledgeSourceBackend backend) {
		d.setMinimumDuration(Util.parseTimeConstraint(instance, "minDuration"));
		d.setMinimumDurationUnits(Util.parseUnitsConstraint(instance,
				"minDurationUnits", backend));
		d.setMaximumDuration(Util.parseTimeConstraint(instance, "maxDuration"));
		d.setMaximumDurationUnits(Util.parseUnitsConstraint(instance,
				"maxDurationUnits", backend));
	}

	/**
	 * @param valueTypeP
	 * @param abstractedFroms
	 * @param d
	 * @return
	 */
	private static Cls readAndSetTypes(Slot valueTypeP,
			Collection<?> abstractedFroms, AggregationDefinition d) {
		Cls finalValueTypeAF = null;
		boolean valueTypeConsistent = true;
		for (Iterator<?> itr3 = abstractedFroms.iterator(); itr3.hasNext();) {
			Instance abstractedFrom = (Instance) itr3.next();
			String abstractedFromName = (String) abstractedFrom.getName();
			d.addPrimitiveParameter(abstractedFromName);
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
	 * @param model
	 * @param instance
	 * @param d
	 */
	private static void setValueType(Instance instance, AggregationDefinition d) {
		Cls finalValueTypeAF = readAndSetTypes(instance.getKnowledgeBase()
				.getSlot("valueType"), instance.getOwnSlotValues(instance
				.getKnowledgeBase().getSlot("abstractedFrom")), d);
		if (finalValueTypeAF.getName().equals(
				instance.getKnowledgeBase().getCls("Value").getName())) {
			d.setValueType(Value.class);
		} else if (finalValueTypeAF.getName().equals(
				instance.getKnowledgeBase().getCls("NominalValue").getName())) {
			d.setValueType(NominalValue.class);
		} else if (finalValueTypeAF.getName().equals(
				instance.getKnowledgeBase().getCls("OrdinalValue").getName())) {
			d.setValueType(OrdinalValue.class);
		} else if (finalValueTypeAF.getName().equals(
				instance.getKnowledgeBase().getCls("NumericalValue").getName())) {
			d.setValueType(NumericalValue.class);
		} else if (finalValueTypeAF.getName().equals(
				instance.getKnowledgeBase().getCls("DoubleValue").getName())) {
			d.setValueType(NumberValue.class);
		} else if (finalValueTypeAF.getName().equals(
				instance.getKnowledgeBase().getCls("InequalityDoubleValue")
						.getName())) {
			d.setValueType(InequalityNumberValue.class);
		}
	}

	public boolean protempaKnowledgeBaseHasProposition(
			Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
		boolean result = false;
		if (protegeParameter != null && protempaKnowledgeBase != null) {
			result = protempaKnowledgeBase
					.hasAbstractionDefinition(protegeParameter.getName());
		}
		return result;
	}

}
