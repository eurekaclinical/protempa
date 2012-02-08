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
        
        this.copier.grab(arg1);
        for (ListIterator<TemporalProposition> itr = pl.listIterator(minIndex);
                itr.hasNext() && itr.nextIndex() < maxIndex;) {
            TemporalProposition o = itr.next();
            o.accept(copier);
        }
        this.copier.release();
    }
}
