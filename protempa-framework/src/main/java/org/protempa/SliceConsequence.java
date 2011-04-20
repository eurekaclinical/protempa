package org.protempa;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.TemporalProposition;

/**
 * @author Andrew Post
 */
final class SliceConsequence implements Consequence {

    private static final long serialVersionUID = -7485083104777547624L;
    
    private final PropositionCopier copier;
    private int minIndex;
    private int maxIndex;

    SliceConsequence(SliceDefinition def, DerivationsBuilder listener) {
        assert def != null : "def cannot be null";
        assert listener != null : "listener cannot be null";
        this.minIndex = def.getMinIndex();
        this.maxIndex = def.getMaxIndex();
        this.copier = new PropositionCopier(def.getId(), listener);
    }

    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1) {
        @SuppressWarnings("unchecked")
        List<TemporalProposition> pl =
                (List<TemporalProposition>) arg0.get(
                arg0.getDeclaration("result"));
        if (minIndex < 0) {
            Collections.sort(pl, ProtempaUtil.REVERSE_TEMP_PROP_COMP);
            minIndex = -minIndex - 1;
            maxIndex = -maxIndex - 1;
        } else {
            Collections.sort(pl, ProtempaUtil.TEMP_PROP_COMP);
        }
        
        this.copier.setWorkingMemory(arg1);
        for (ListIterator<TemporalProposition> itr = pl.listIterator(minIndex);
                itr.hasNext() && itr.nextIndex() < maxIndex;) {
            TemporalProposition o = itr.next();
            o.accept(copier);
        }
        this.copier.setWorkingMemory(null);
    }
}
