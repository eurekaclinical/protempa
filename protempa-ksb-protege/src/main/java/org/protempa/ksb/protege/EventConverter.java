package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.EventDefinition;
import org.protempa.IntervalSide;
import org.protempa.KnowledgeBase;
import org.arp.javautil.graph.Weight;
import org.protempa.KnowledgeSourceReadException;

class EventConverter implements PropositionConverter {

    @Override
    public void convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {
        if (protegeProposition != null
                && protempaKnowledgeBase != null
                && !protempaKnowledgeBase.hasEventDefinition(
                protegeProposition.getName())) {

            EventDefinition eventDef = new EventDefinition(
                    protempaKnowledgeBase, protegeProposition.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(protegeProposition, eventDef, cm);
            Util.setProperties(protegeProposition, eventDef, cm);
            Util.setInverseIsAs(protegeProposition, eventDef, cm);

            Collection<?> hasParts = cm.getOwnSlotValues(protegeProposition,
                    cm.getSlot("hasPart"));
            for (Iterator<?> itr = hasParts.iterator(); itr.hasNext();) {
                Instance hasPartInstance = (Instance) itr.next();
                Integer cst = Util.parseTimeConstraint(
                        hasPartInstance, "offset", cm);
                eventDef.addHasPart(new EventDefinition.HasPartOffset(
                        ((Instance) cm.getOwnSlotValue(hasPartInstance, cm.getSlot(
                        "offsetEvent"))).getName(),
                        IntervalSide.intervalSide((String) cm.getOwnSlotValue(hasPartInstance, cm.getSlot("offsetSide"))),
                        cst != null ? new Weight(cst) : null, Util.parseUnitsConstraint(hasPartInstance,
                        "finishOffsetUnits", backend, cm)));
            }
        }
    }

    @Override
    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
        return protegeProposition != null
                && protempaKnowledgeBase != null
                && protempaKnowledgeBase.hasEventDefinition(protegeProposition.getName());
    }
}
