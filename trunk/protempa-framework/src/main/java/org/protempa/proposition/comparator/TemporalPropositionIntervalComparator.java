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
 * For sorting temporal propositions by interval. It sorts by the
 * beginning of a pair of intervals first, followed by the end of a pair of
 * intervals.
 *
 * @author Andrew Post
 */
public class TemporalPropositionIntervalComparator
        implements Comparator<TemporalProposition> {

    @Override
    public int compare(TemporalProposition o1, TemporalProposition o2) {
        return o1.getInterval().compareTo(o2.getInterval());
    }
}
