package org.protempa.ksb.protege;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.SliceDefinition;

final class SliceConverter implements
        PropositionConverter {
    
    SliceConverter() {
    }

    @Override
    public SliceDefinition convert(Instance protegeParameter,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend) 
            throws KnowledgeSourceReadException {
        assert protempaKnowledgeBase != null :
                "protempaKnowledgeBase cannot be null";

        SliceDefinition ad = new SliceDefinition(
                protempaKnowledgeBase, protegeParameter.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeParameter, ad, cm);
        Util.setInverseIsAs(protegeParameter, ad, cm);
        Util.setProperties(protegeParameter, ad, cm);
        Util.setTerms(protegeParameter, ad, cm);
        Integer maxIndexInt =
                (Integer) cm.getOwnSlotValue(protegeParameter,
                cm.getSlot("maxIndex"));
        if (maxIndexInt != null) {
            ad.setMaxIndex(maxIndexInt.intValue());
        }
        Integer minIndexInt =
                (Integer) cm.getOwnSlotValue(protegeParameter,
                cm.getSlot("minIndex"));
        if (maxIndexInt != null) {
            ad.setMinIndex(minIndexInt.intValue());
        }

        Collection abstractedFromInstances =
                cm.getOwnSlotValues(protegeParameter,
                cm.getSlot("abstractedFrom"));
        for (Iterator itr = abstractedFromInstances.iterator();
                itr.hasNext();) {
            ad.addAbstractedFrom(((Instance) itr.next()).getName());
        }
        return ad;
    }

    @Override
    public boolean protempaKnowledgeBaseHasProposition(
            Instance protegeParameter, KnowledgeBase protempaKnowledgeBase) {
        String propId = protegeParameter.getName();
        return protempaKnowledgeBase.hasAbstractionDefinition(propId)
                || protempaKnowledgeBase.hasEventDefinition(propId)
                || protempaKnowledgeBase
                .hasPrimitiveParameterDefinition(propId)
                || protempaKnowledgeBase.hasConstantDefinition(propId);
    }
}
