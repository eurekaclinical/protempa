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
package org.protempa.proposition.comparator;

import java.util.Comparator;
import org.protempa.proposition.TemporalProposition;

/**
 * For sorting temporal propositions by time interval. It sorts by the
 * end of a pair of intervals.
 *
 * @author Andrew Post
 * @param <T> an instanceof {@link TemporalProposition}.
 */
public class MaxFinishTemporalPropositionComparator implements 
        Comparator<TemporalProposition> {

    @Override
    public int compare(TemporalProposition p0, TemporalProposition p1) {
        Long p0MaximumFinish = p0.getInterval().getMaximumFinish();
        Long p1MaximumFinish = p1.getInterval().getMaximumFinish();
        if (p0MaximumFinish == p1MaximumFinish) {
            return 0;
        } else if (p0MaximumFinish == null) {
            return 1;
        } else if (p1MaximumFinish == null) {
            return -1;
        } else {
            return p0MaximumFinish.compareTo(p1MaximumFinish);
        }
    }
}
