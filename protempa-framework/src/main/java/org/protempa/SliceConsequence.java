package org.protempa;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.PropositionUtil;
import org.protempa.proposition.PropositionVisitable;
import org.protempa.proposition.TemporalProposition;


/**
 * @author Andrew Post
 */
final class SliceConsequence implements Consequence {

	private static final long serialVersionUID = -7485083104777547624L;

	private final SliceDefinition def;

	SliceConsequence(SliceDefinition def) {
        assert def != null : "def cannot be null";
		this.def = def;
	}

	public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1) {
		List<PropositionVisitable> l =
                (List<PropositionVisitable>)
                arg0.get(arg0.getDeclaration("result"));
        TemporalPropositionListCreator c = new TemporalPropositionListCreator();
        c.visit(l);
        List<TemporalProposition> pl = c.getTemporalPropositionList();
		int minIndex = def.getMinIndex();
		int maxIndex = def.getMaxIndex();
		if (minIndex < 0) {
			Collections.sort(pl,
                    PropositionUtil.REVERSE_TEMPORAL_PROPOSITION_COMPARATOR);
			minIndex = -minIndex - 1;
			maxIndex = -maxIndex - 1;
		} else {
            Collections.sort(pl,
                    PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);
        }

        PropositionCopier copier =
                new PropositionCopier(def.getId(), arg1);
		for (ListIterator<TemporalProposition> itr = pl.listIterator(minIndex);
            itr.hasNext() && itr.nextIndex() < maxIndex;) {
            TemporalProposition o = itr.next();
            o.accept(copier);
		}
	}

    

}