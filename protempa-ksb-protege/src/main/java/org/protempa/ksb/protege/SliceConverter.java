package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeBase;
import org.protempa.SliceDefinition;

final class SliceConverter implements
        PropositionConverter {

    /**
     *
     */
    SliceConverter() {
    }

    public void convert(Instance protegeParameter,
            KnowledgeBase protempaKnowledgeBase,
            KnowledgeSourceBackend backend) {
        assert protempaKnowledgeBase != null :
                "protempaKnowledgeBase cannot be null";

        SliceDefinition ad = new SliceDefinition(
                protempaKnowledgeBase, protegeParameter.getName());
        Util.setNames(protegeParameter, ad);
        Util.setInverseIsAs(protegeParameter, ad);
        Integer maxIndexInt =
                (Integer) protegeParameter.getOwnSlotValue(
                protegeParameter.getKnowledgeBase().getSlot("maxIndex"));
        if (maxIndexInt != null) {
            ad.setMaxIndex(maxIndexInt.intValue());
        }
        Integer minIndexInt =
                (Integer) protegeParameter.getOwnSlotValue(
                protegeParameter.getKnowledgeBase().getSlot("minIndex"));
        if (maxIndexInt != null) {
            ad.setMinIndex(minIndexInt.intValue());
        }

        Collection abstractedFromInstances =
                protegeParameter.getOwnSlotValues(
                protegeParameter.getKnowledgeBase().getSlot("abstractedFrom"));
        for (Iterator itr = abstractedFromInstances.iterator();
                itr.hasNext();) {
            ad.addAbstractedFrom(((Instance) itr.next()).getName());
        }
    }

    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
        String propId = protegeParameter.getName();
        return protempaKnowledgeBase.hasAbstractionDefinition(propId)
                || protempaKnowledgeBase.hasEventDefinition(propId)
                || protempaKnowledgeBase
                .hasPrimitiveParameterDefinition(propId);
    }
}
