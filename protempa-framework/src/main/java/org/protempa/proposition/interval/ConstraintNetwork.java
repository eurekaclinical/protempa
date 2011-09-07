package org.protempa.proposition.interval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.arp.javautil.graph.BellmanFord;
import org.arp.javautil.graph.DirectedGraph;
import org.arp.javautil.graph.Weight;
import org.arp.javautil.graph.WeightFactory;

/**
 * Temporal constraint network for solving the simple temporal problem (STP) as
 * defined in Dechter, R. et al. Temporal Constraint Networks. Artif. Intell.
 * 1991;49:61-95.
 * 
 * @author Andrew Post
 */
final class ConstraintNetwork {

    private static final String timeZero = "0";
    private final List<Interval> intervals;
    private final DirectedGraph directedGraph;
    private Weight calcMinDuration;
    private Weight calcMaxDuration;
    private Weight calcMinFinish;
    private Weight calcMaxFinish;
    private Weight calcMinStart;
    private Weight calcMaxStart;
    private Map<?, Weight> shortestDistancesFromTimeZeroSource;
    private Map<?, Weight> shortestDistancesFromTimeZeroDestination;

    /**
     * Constructs an empty <code>ConstraintNetwork</code> with the default
     * initial interval capacity (10).
     */
    ConstraintNetwork() {
        this(10);
    }

    /**
     * Constructs an empty <code>ConstraintNetwork</code> with the specified
     * initial interval capacity.
     *
     * @param initialCapacity
     *            the initial interval capacity.
     */
    ConstraintNetwork(int initialCapacity) {
        directedGraph = new DirectedGraph(initialCapacity * 2 + 1);
        directedGraph.add(timeZero);
        intervals = new ArrayList<Interval>(initialCapacity);
    }

    void clear() {
        directedGraph.clear();

        intervals.clear();
        directedGraph.add(timeZero);
        calcMinDuration = null;
        calcMaxDuration = null;
        calcMinFinish = null;
        calcMaxFinish = null;
        calcMinStart = null;
        calcMaxStart = null;
        shortestDistancesFromTimeZeroSource = null;
        shortestDistancesFromTimeZeroDestination = null;
    }

    /**
     * Remove the distance relation between two intervals, if such a relation
     * exists.
     *
     * @param i1
     *            an interval.
     * @param i2
     *            another interval.
     * @return true if the graph changed as a result of this operation, false
     *         otherwise.
     */
    boolean removeRelation(Interval i1, Interval i2) {
        if (i1 == i2 || !containsInterval(i1) || !containsInterval(i2)) {
            return false;
        }

        Object i1Start = i1.getStart();
        Object i1Finish = i1.getFinish();
        Object i2Start = i2.getStart();
        Object i2Finish = i2.getFinish();

        directedGraph.setEdge(i1Start, i2Start, null);
        directedGraph.setEdge(i1Start, i2Finish, null);
        directedGraph.setEdge(i2Start, i1Start, null);
        directedGraph.setEdge(i2Start, i1Finish, null);
        directedGraph.setEdge(i1Finish, i2Start, null);
        directedGraph.setEdge(i1Finish, i2Finish, null);
        directedGraph.setEdge(i2Finish, i1Start, null);
        directedGraph.setEdge(i2Finish, i1Finish, null);

        calcMinDuration = null;
        calcMaxDuration = null;
        calcMinFinish = null;
        calcMaxFinish = null;
        calcMinStart = null;
        calcMaxStart = null;
        shortestDistancesFromTimeZeroSource = null;
        shortestDistancesFromTimeZeroDestination = null;

        return true;
    }

    /**
     * Remove an interval from this graph.
     *
     * @param i
     *            an interval.
     * @return true if the graph changed as a result of this operation, false
     *         otherwise.
     */
    boolean removeInterval(Interval i) {
        calcMinDuration = null;
        calcMaxDuration = null;
        calcMinFinish = null;
        calcMaxFinish = null;
        calcMinStart = null;
        calcMaxStart = null;
        shortestDistancesFromTimeZeroSource = null;
        shortestDistancesFromTimeZeroDestination = null;

        if (directedGraph.remove(i.getStart()) != null) {
            if (directedGraph.remove(i.getFinish()) == null) {
                throw new IllegalStateException();
            }
            intervals.remove(i);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determine if an interval is contained in this graph.
     *
     * @param i
     *            an interval.
     * @return <code>true</code> if the given interval is found,
     *         <code>false</code> otherwise.
     */
    private boolean containsInterval(Interval i) {
        if (i != null) {
            return directedGraph.contains(i.getStart())
                    && directedGraph.contains(i.getFinish());
        } else {
            return false;
        }
    }

    /**
     * Add an interval to this graph.
     *
     * @param i
     *            an interval.
     * @return <code>true</code> if successful, <code>false</code> if the
     *         interval could not be added. If there was a problem adding the
     *         interval, then the constraint network may be in an inconsistent
     *         state (e.g., part of the interval got added).
     */
    boolean addInterval(Interval i) {
        if (i == null || containsInterval(i) || !intervals.add(i)) {
            return false;
        }

        Object iStart = i.getStart();
        Object iFinish = i.getFinish();

        directedGraph.add(iStart);
        directedGraph.add(iFinish);

        Weight mindur = i.getSpecifiedMinimumLength();
        Weight maxdur = i.getSpecifiedMaximumLength();
        directedGraph.setEdge(iStart, iFinish, maxdur);
        directedGraph.setEdge(iFinish, iStart, mindur.invertSign());

        Weight minstart = i.getSpecifiedMinimumStart();
        Weight maxstart = i.getSpecifiedMaximumStart();
        directedGraph.setEdge(timeZero, iStart, maxstart);
        directedGraph.setEdge(iStart, timeZero, minstart.invertSign());

        Weight minfinish = i.getSpecifiedMinimumFinish();
        Weight maxfinish = i.getSpecifiedMaximumFinish();
        directedGraph.setEdge(timeZero, iFinish, maxfinish);
        directedGraph.setEdge(iFinish, timeZero, minfinish.invertSign());

        calcMinDuration = null;
        calcMaxDuration = null;
        calcMinFinish = null;
        calcMaxFinish = null;
        calcMinStart = null;
        calcMaxStart = null;
        shortestDistancesFromTimeZeroSource = null;
        shortestDistancesFromTimeZeroDestination = null;

        return true;
    }

    /**
     * Calculates and returns the minimum path from time zero to the start of an
     * interval.
     *
     * @return a <code>Weight</code> object.
     */
    Weight getMinimumStart() {
        if (calcMinStart == null) {
            // Find the shortest distance from a start to time zero.
            Weight result = WeightFactory.NEG_INFINITY;
            if (shortestDistancesFromTimeZeroDestination == null) {
                shortestDistancesFromTimeZeroDestination = BellmanFord.calcShortestDistances(timeZero, directedGraph,
                        BellmanFord.Mode.DESTINATION);

                if (shortestDistancesFromTimeZeroDestination == null) {
                    throw new IllegalStateException("Negative cycle detected!");
                }
            }
            for (int i = 0, n = intervals.size(); i < n; i++) {
                Object start = intervals.get(i).getStart();
                result = Weight.max(result,
                        (Weight) shortestDistancesFromTimeZeroDestination.get(start));
            }
            calcMinStart = result.invertSign();
        }

        return calcMinStart;
    }

    /**
     * Calculates and returns the maximum path from time zero to the start of an
     * interval.
     *
     * @return a <code>Weight</code> object.
     */
    Weight getMaximumStart() {
        if (calcMaxStart == null) {
            // Find the longest distance from time zero to a start.
            Weight result = WeightFactory.POS_INFINITY;
            if (shortestDistancesFromTimeZeroSource == null) {
                shortestDistancesFromTimeZeroSource = BellmanFord.calcShortestDistances(timeZero, directedGraph,
                        BellmanFord.Mode.SOURCE);

                if (shortestDistancesFromTimeZeroSource == null) {
                    throw new IllegalStateException("Negative cycle detected!");
                }
            }
            for (int i = 0, n = intervals.size(); i < n; i++) {
                Object start = intervals.get(i).getStart();
                result = Weight.min(result,
                        (Weight) shortestDistancesFromTimeZeroSource.get(start));
            }
            calcMaxStart = result;
        }

        return calcMaxStart;
    }

    /**
     * Calculates and returns the minimum path from time zero to the finish of
     * an interval.
     *
     * @return a <code>Weight</code> object.
     */
    Weight getMinimumFinish() {
        if (calcMinFinish == null) {
            // Find the shortest distance from a finish to time zero.
            Weight result = WeightFactory.POS_INFINITY;
            if (shortestDistancesFromTimeZeroDestination == null) {
                shortestDistancesFromTimeZeroDestination = BellmanFord.calcShortestDistances(timeZero, directedGraph,
                        BellmanFord.Mode.DESTINATION);
                if (shortestDistancesFromTimeZeroDestination == null) {
                    throw new IllegalStateException("Negative cycle detected!");
                }
            }
            for (int i = 0, n = intervals.size(); i < n; i++) {
                Object finish = intervals.get(i).getFinish();
                result = Weight.min(result,
                        (Weight) shortestDistancesFromTimeZeroDestination.get(finish));
            }
            calcMinFinish = result.invertSign();

        }

        return calcMinFinish;
    }

    /**
     * Calculates and returns the maximum path from time zero to the finish of
     * an interval.
     *
     * @return a <code>Weight</code> object.
     */
    Weight getMaximumFinish() {
        if (calcMaxFinish == null) {
            // Find the longest distance from time zero to a finish.
            Weight result = WeightFactory.NEG_INFINITY;
            if (shortestDistancesFromTimeZeroSource == null) {
                shortestDistancesFromTimeZeroSource = BellmanFord.calcShortestDistances(timeZero, directedGraph,
                        BellmanFord.Mode.SOURCE);
                if (shortestDistancesFromTimeZeroSource == null) {
                    throw new IllegalStateException("Negative cycle detected!");
                }
            }
            for (int i = 0, n = intervals.size(); i < n; i++) {
                Object finish = intervals.get(i).getFinish();
                result = Weight.max(result,
                        (Weight) shortestDistancesFromTimeZeroSource.get(finish));
            }
            calcMaxFinish = result;
        }

        return calcMaxFinish;
    }

    /**
     * Calculates and returns the maximum time distance from the start of an
     * interval to the finish of an interval.
     *
     * @return a <code>Weight</code> object.
     */
    Weight getMaximumDuration() {
        if (calcMaxDuration == null) {
            Weight max = WeightFactory.ZERO;
            for (int i = 0, n = intervals.size(); i < n; i++) {
                Object start = intervals.get(i).getStart();
                Map<?, Weight> d = BellmanFord.calcShortestDistances(start,
                        directedGraph, BellmanFord.Mode.SOURCE);
                if (d == null) {
                    throw new IllegalStateException("Negative cycle detected!");
                }
                for (int j = 0; j < n; j++) {
                    Object finish = intervals.get(j).getFinish();
                    max = Weight.max(max, d.get(finish));
                }
            }
            calcMaxDuration = max;
        }

        return calcMaxDuration;
    }

    /**
     * Calculates and returns the minimum time distance from the start of an
     * interval to the finish of an interval.
     *
     * @return a <code>Weight</code> object.
     */
    Weight getMinimumDuration() {
        if (calcMinDuration == null) {
            Weight min = WeightFactory.POS_INFINITY;
            for (int i = 0, n = intervals.size(); i < n; i++) {
                Object finish = intervals.get(i).getFinish();
                Map<?, Weight> d = BellmanFord.calcShortestDistances(finish,
                        directedGraph, BellmanFord.Mode.SOURCE);
                if (d == null) {
                    throw new IllegalStateException("Negative cycle detected!");
                }
                for (int j = 0; j < n; j++) {
                    Object start = intervals.get(j).getStart();
                    min = Weight.min(min, d.get(start));
                }
            }
            calcMinDuration = min.invertSign();
        }

        return calcMinDuration;
    }

    /**
     * Returns whether this constraint network is consistent. A constraint
     * network is consistent if and only if its distance graph has no negative
     * cycles.
     *
     * @return <code>true</code> if this network is consistent,
     *         <code>false</code> otherwise.
     */
    boolean getConsistent() {
        return DirectionalPathConsistency.getConsistent(directedGraph);
    }
}
