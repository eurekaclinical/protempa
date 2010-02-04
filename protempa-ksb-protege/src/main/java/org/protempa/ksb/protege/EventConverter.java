package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.EventDefinition;
import org.protempa.IntervalSide;
import org.protempa.KnowledgeBase;
import org.arp.javautil.graph.Weight;

class EventConverter implements PropositionConverter {

	public void convert(Instance protegeProposition,
			KnowledgeBase protempaKnowledgeBase, KnowledgeSourceBackend backend) {
		if (protegeProposition != null
				&& protempaKnowledgeBase != null
				&& !protempaKnowledgeBase.hasEventDefinition(protegeProposition
						.getName())) {

			EventDefinition eventDef = new EventDefinition(
					protempaKnowledgeBase, protegeProposition.getName());
			Util.setNames(protegeProposition, eventDef);
			
			Util.setInverseIsAs(protegeProposition, eventDef);

			Collection<?> hasParts = protegeProposition
					.getOwnSlotValues(protegeProposition.getKnowledgeBase()
							.getSlot("hasPart"));
			for (Iterator<?> itr = hasParts.iterator(); itr.hasNext();) {
				Instance hasPartInstance = (Instance) itr.next();
				Integer cst = Util.parseTimeConstraint(
						hasPartInstance, "offset");
				eventDef
						.addHasPart(new EventDefinition.HasPartOffset(
								((Instance) hasPartInstance
										.getOwnSlotValue(hasPartInstance
												.getKnowledgeBase().getSlot(
														"offsetEvent")))
										.getName(),
								IntervalSide
										.intervalSide((String) hasPartInstance
												.getOwnSlotValue(hasPartInstance
														.getKnowledgeBase()
														.getSlot("offsetSide"))),
								cst != null ? new Weight(cst) : null, Util
								.parseUnitsConstraint(hasPartInstance,
										"finishOffsetUnits", backend)));
			}
		}
	}

	public boolean protempaKnowledgeBaseHasProposition(
			Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
		return protegeProposition != null
				&& protempaKnowledgeBase != null
				&& protempaKnowledgeBase.hasEventDefinition(protegeProposition
						.getName());
	}

}
