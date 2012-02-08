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

import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

/**
 * @author Andrew Post
 */
final class SimpleSegment<T extends TemporalProposition> extends Segment<T> {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    SimpleSegment(Sequence<T> seq) {
        super(seq);
    }

    SimpleSegment(Sequence<T> seq, int firstIndex, int lastIndex) {
        super(seq, firstIndex, lastIndex);
    }

    SimpleSegment(SimpleSegment<T> simpleSegment) {
        super(simpleSegment);
    }

    @Override
    protected Interval intervalCreator() {
        if (isEmpty()) {
            return null;
        } else if (size() == 1) {
            Interval ival = first().getInterval();
            return intervalFactory.getInstance(ival.getMinStart(),
                    ival.getMaxStart(),
                    ival.getStartGranularity(), ival.getMinFinish(),
                    ival.getMaxFinish(), ival.getFinishGranularity());
        } else {
            return super.intervalCreator();
        }
    }
}
