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
package org.protempa.proposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

/**
 * A factory for creating order lists of {@link CompoundValuedInterval}s based
 * on input propositions.
 */
public abstract class IntervalSectioner<E extends TemporalProposition, K extends CompoundInterval<E>> {

    private final static IntervalFactory intervalFactory = new IntervalFactory();

    IntervalSectioner() {
    }

    /**
     * Creates a chronologically ordered list of intervals and their associated
     * propositions and values. The intervals returned will not necessarily be
     * the same as those of the propositions. Multiple propositions could be
     * associated to each interval, and a given proposition could overlap
     * multiple intervals. The number of returned intervals will be at least as
     * big as the number of propositions.
     * 
     * The list will be sorted by {@link Interval}'s natural ordering.
     * 
     * @param propositions
     *            the list of {@link AbstractParameter}s to put into intervals
     * @return a list of {@link CompoundValuedInterval}, sorted chronologically
     *         based on {@link Interval}'s natural ordering
     */
    public List<K> buildIntervalList(List<E> propositions) {
        return orderIntervals(propositions);
    }

    /**
     * Creates the required intervals and orders them chronologically. Here, an
     * "interval" is actually a {@link CompoundValuedInterval}: a grouping of an
     * {@link Interval}, a set of {@link AbstractParameter}s, and the
     * {@link Value}s of those abstract parameters. Because we are dealing with
     * multiple types of propositions, there may be overlapping intervals, which
     * will result in additional intervals being created.
     */
    private List<K> orderIntervals(List<E> propositions) {
        Granularity startGran = propositions.get(0).getInterval()
                .getStartGranularity();
        Granularity finishGran = propositions.get(0).getInterval()
                .getFinishGranularity();
        List<K> result = new ArrayList<>();

        Set<Long> startBoundsSet = new HashSet<>();
        Set<Long> finishBoundsSet = new HashSet<>();
        SortedSet<Interval> intervalSet = new TreeSet<>();

        // determining the bounds of all of the abstract parameters will allows
        // us later to decide which new intervals each parameter overlaps
        // note that it doesn't matter whether the bound is a start or finish:
        // two start bounds between different parameters that are
        // chronologically adjacent will form an interval
        for (E p : propositions) {
            assert p.getInterval().getStartGranularity().equals(startGran)
                    && p.getInterval().getFinishGranularity()
                            .equals(finishGran) : "all intervals must have the same start and finish granularities to be combined";
            startBoundsSet.add(p.getInterval().getMinStart());
            finishBoundsSet.add(p.getInterval().getMaxFinish());

            // we must take care to include point intervals where the start and
            // finish are the same (ie, timestamps)
            // if this special case is ignored, then any abstract parameters
            // that are actually just timestamps will be left out of the final
            // interval set
            if (startBoundsSet.contains(p.getInterval().getMaxFinish())) {
                intervalSet.add(intervalFactory
                        .getInstance(p.getInterval().getMaxFinish(), p
                                .getInterval().getFinishGranularity()));
            }
            if (finishBoundsSet.contains(p.getInterval().getMinStart())) {
                intervalSet.add(intervalFactory.getInstance(p.getInterval()
                        .getMinStart(), p.getInterval().getStartGranularity()));
            }
        }

        // the base set of intervals consists the point intervals, which we
        // created earlier, and the intervals between adjacent bounds, which we
        // create here
        // these intervals will be further subdivided when we actually assign
        // the propositions
        // to intervals below
        List<Long> intervalBounds = new ArrayList<>(startBoundsSet);
        Collections.sort(intervalBounds);
        for (int i = 0; i < intervalBounds.size() - 1; i++) {
            intervalSet.add(intervalFactory.getInstance(intervalBounds.get(i),
                    startGran, intervalBounds.get(i + 1), finishGran));
        }

        SortedMap<Interval, Set<E>> fakeIntervals = new TreeMap<>();

        // this loop assigns each proposition to the interval(s) it overlaps,
        // creating new intervals as needed
        for (E p : propositions) {
            Long ivalStart = p.getInterval().getMinStart();

            // we use getMaximumFinish() instead of getMaxFinish() because for
            // point intervals we actually need the "made up" finish time that
            // getMaximumFinish() provides, which is one millisecond less than
            // the next value in the units of the interval (eg, if
            // the timestamp is January 1, 2012 12:00:00.000 PM and the units
            // are in minutes, then getMaximumFinish() will return January 1,
            // 2012 12:00:00.999 PM as opposed to getMaxFinish(), which will
            // return 12:00:00.000)
            //
            // this is because the overlapping intervals comparison checks that
            // the start of proposition interval is before or exactly the start
            // of the target interval and that the finish of the proposition
            // interval is after the start of the target interval
            // if we just used getMaxFinish() then the test would fail on all
            // point/timestamp intervals
            Long ivalFinish = p.getInterval().getMaximumFinish();
            for (Interval interval : intervalSet) {
                if (ivalStart <= interval.getMinStart()
                        && ivalFinish > interval.getMinStart()) {
                    // the finish of the new interval should be the finish of
                    // the target interval if the proposition's interval
                    // extends beyond it, and it should be the finish of the
                    // proposition's interval otherwise
                    Interval ival = intervalFactory.getInstance(
                            interval.getMinStart(),
                            startGran,
                            Math.min(p.getInterval().getMaxFinish(),
                                    interval.getMaxFinish()), finishGran);
                    if (!fakeIntervals.containsKey(ival)) {
                        fakeIntervals.put(ival, new HashSet<E>());
                    }
                    fakeIntervals.get(ival).add(p);
                }
                if (ivalFinish <= interval.getMinStart()) {
                    break;
                }
            }
        }
        for (Entry<Interval, Set<E>> e : fakeIntervals.entrySet()) {
            Set<E> props = new HashSet<>();
            for (E p : e.getValue()) {
                props.add(p);
            }
            result.add(newCompoundInterval(e.getKey(), props));
        }

        return result;
    }
    
    protected abstract K newCompoundInterval(Interval ival, Set<E> props);
}
