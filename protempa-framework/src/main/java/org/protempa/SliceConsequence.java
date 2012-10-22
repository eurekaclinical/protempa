/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

/**
 * The consequence part of a rule for computing a temporal slice.
 * 
 * @author Andrew Post
 */
final class SliceConsequence implements Consequence {

    private static final long serialVersionUID = -7485083104777547624L;

    private final SliceDefinition def;

    private final PropositionCopier copier;
    
    private final DerivationsBuilder derivationsBuilder;

    /*
     * A sublist index range for performing the slice. This will not necessarily
     * be the same as the {@link SliceDefinition}'s index range. If the slice
     * definition's min index is negative, the input propositions are sorted in
     * reverse chronological order, and these two fields will store the index
     * range to be sliced for the reversed propositions. See the constructor for
     * the logic that is performed.
     */
    private final int minIndex;
    private final int maxIndex;

    /*
     * Whether or not to sort the propositions in reverse or forward
     * chronological order. We don't just store the comparator to use because
     * this class implements {@link Serializable} and the comparator may not be
     * serializable.
     */
    private final boolean reverse;

    /*
     * Whether or not the slice should be considered as a single merged interval
     * spanning from the min index to the max index.
     */
    private final boolean merged;

    /**
     * Constructs a consequence instance with a definition of the temporal slice
     * to compute and a derivations builder for recording links between computed
     * temporal slices and the intervals from which they were derived.
     * 
     * @param def
     *            a {@link SliceDefinition} corresponding to this rule.
     * @param listener
     *            a {@link DerivationsBuilder}.
     */
    SliceConsequence(SliceDefinition def, DerivationsBuilder listener) {
        assert def != null : "def cannot be null";
        assert listener != null : "listener cannot be null";
        
        this.def = def;
        this.derivationsBuilder = listener;
        
        int minInd = def.getMinIndex();
        int maxInd = def.getMaxIndex();
        if (minInd < 0) {
            this.minIndex = -maxInd;
            this.maxIndex = -minInd;
            this.reverse = true;
        } else {
            this.minIndex = minInd;
            this.maxIndex = maxInd;
            this.reverse = false;
        }
        this.merged = def.isMergedInterval();
        this.copier = new PropositionCopier(def.getId(), listener);
    }

    /**
     * Called when there exist the minimum necessary number of intervals with
     * the specified proposition id in order to compute the temporal slice
     * corresponding to this rule.
     * 
     * @param arg0
     *            a {@link KnowledgeHelper}
     * @param arg1
     *            a {@link WorkingMemory}
     * 
     * @see JBossRuleCreator
     */
    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1) {
        @SuppressWarnings("unchecked")
        List<TemporalProposition> pl = (List<TemporalProposition>) arg0
                .get(arg0.getDeclaration("result"));
        Comparator<TemporalProposition> comp;
        if (this.reverse) {
            comp = ProtempaUtil.REVERSE_TEMP_PROP_COMP;
        } else {
            comp = ProtempaUtil.TEMP_PROP_COMP;
        }
        Collections.sort(pl, comp);
        this.copier.grab(arg1);
        if (this.merged) {
            mergedInterval(arg0, pl);
        } else {
            for (ListIterator<TemporalProposition> itr = pl
                    .listIterator(this.minIndex); itr.hasNext()
                    && itr.nextIndex() < this.maxIndex;) {
                TemporalProposition o = itr.next();
                o.accept(this.copier);
            }
        }
        this.copier.release();
    }

    private void mergedInterval(KnowledgeHelper kh, List<TemporalProposition> pl) {
        List<TemporalProposition> slice = new ArrayList<TemporalProposition>();
        for (ListIterator<TemporalProposition> itr = pl.listIterator(); itr
                .hasNext() && itr.nextIndex() < this.maxIndex;) {
            slice.add(itr.next());
        }
        Segment<TemporalProposition> segment = new Segment<TemporalProposition>(
                new Sequence<TemporalProposition>(def.getId(), slice));
        AbstractParameter result = AbstractParameterFactory.getFromAbstraction(
                def.getId(), segment, slice, null, null, null);
        kh.getWorkingMemory().insert(result);
        for (Proposition p : segment) {
            this.derivationsBuilder.propositionAsserted(p, result);
        }
    }
}
