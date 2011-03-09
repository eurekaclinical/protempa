package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import org.arp.javautil.graph.WeightFactory;
import org.protempa.EventDefinition;
import org.protempa.IntervalSide;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;

class EventConverter implements PropositionConverter {
    
    private static final WeightFactory weightFactory =
            new WeightFactory();

    @Override
    public EventDefinition convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {
        EventDefinition result =
                protempaKnowledgeBase.getEventDefinition(protegeProposition.getName());
        if (result == null) {
            result = new EventDefinition(
                    protempaKnowledgeBase, protegeProposition.getName());
            ConnectionManager cm = backend.getConnectionManager();
            Util.setNames(protegeProposition, result, cm);
            Util.setProperties(protegeProposition, result, cm);
            Util.setTerms(protegeProposition, result, cm);
            Util.setInverseIsAs(protegeProposition, result, cm);

            Collection<?> hasParts = cm.getOwnSlotValues(protegeProposition,
                    cm.getSlot("hasPart"));
            Slot offsetEventSlot = cm.getSlot("offsetEvent");
            Slot offsetSide = cm.getSlot("offsetSide");
            for (Iterator<?> itr = hasParts.iterator(); itr.hasNext();) {
                Instance hasPartInstance = (Instance) itr.next();
                Integer cst = Util.parseTimeConstraint(
                        hasPartInstance, "offset", cm);
                result.addHasPart(new EventDefinition.HasPartOffset(
                        ((Instance) cm.getOwnSlotValue(hasPartInstance, offsetEventSlot)).getName(),
                        IntervalSide.intervalSide((String) cm.getOwnSlotValue(hasPartInstance, offsetSide)),
                        cst != null ? weightFactory.getInstance(cst) : null, Util.parseUnitsConstraint(hasPartInstance,
                        "finishOffsetUnits", backend, cm)));
            }
        }
        return result;
    }

    @Override
    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase) {
        return protempaKnowledgeBase.hasEventDefinition(protegeProposition.getName());
    }
}
