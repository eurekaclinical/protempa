/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.proposition.comparator;

import java.util.Comparator;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;

/**
 * For sorting propositions by interval, if they are temporal propositions.
 * Constant propositions are sorted to the beginning.
 * 
 * @author Andrew Post
 */
public class AllPropositionIntervalComparator 
        implements Comparator<Proposition> {

    /**
     * If comparing {@link TemporalProposition}s, compares their intervals;
     * otherwise, a constant proposition compared to a temporal proposition
     * will return <code>-1</code>, and a constant proposition compared to
     * another constant proposition will return <code>0</code>.
     *
     * @param o1 a {@link Proposition}.
     * @param o2 another {@link Proposition}.
     * @return if <code>o1</code> is before <code>o2</code> according to the
     * rules above return <code>-1</code>. If the same, return <code>0</code>.
     * If after, return <code>1</code>.
     */
    @Override
    public int compare(Proposition o1, Proposition o2) {
        boolean o1b = o1 instanceof TemporalProposition;
        boolean o2b = o2 instanceof TemporalProposition;
        if (o1b && o2b) {
            return ((TemporalProposition) o1).getInterval().compareTo(
                    ((TemporalProposition) o2).getInterval());
        } else if (o1b) {
            return -1;
        } else if (o2b) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
    	return obj instanceof AllPropositionIntervalComparator;
    }
}
