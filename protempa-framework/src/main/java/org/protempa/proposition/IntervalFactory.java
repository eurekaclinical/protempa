package org.protempa.proposition;

import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.Granularity;

/**
 * A factory for creating {@link Interval} objects. A subclass of
 * {@link Interval} will be returned that is optimized for the arguments
 * that are provided to this factory's <code>getInstance</code> methods.
 * 
 * @author Andrew Post
 */
public final class IntervalFactory {

    /**
     * Returns an interval specified by the given minimum start, maximum start,
     * minimum finish and maximum finish and corresponding granularities.
     * The interpretation of the starts and finishes depends on the
     * {@link Granularity} implementations specifiied by the
     * <code>startGran</code> and <code>finishGran</code> arguments. For
     * example, if <code>startGran</code> is an instance of 
     * {@link AbsoluteTimeGranularity}, <code>minStart</code> and
     * <code>maxStart</code> are interpreted as date/times.
     *
     * @param minStart the earliest start of the interval, represented as a
     * {@link Long}. If <code>null</code>, the earliest start will be
     * unbounded.
     * @param maxStart the latest start of the interval, represented as a
     * {@link Long}. if <code>null</code>, the latest start will be unbounded.
     * @param startGran the {@link Granularity} of the <code>minStart</code>
     * and <code>maxStart</code>.
     * @param minFinish the earliest finish of the interval, represented as a
     * {@link Long}. If <code>null</code>, the earliest finish will be
     * unbounded.
     * @param maxFinish the latest finish of the interval, represented as a
     * {@link Long}. If <code>null</code>, the latest finish will be
     * unbounded.
     * @param finishGran the {@link Granularity} of the <code>minFinish</code>
     * and <code>maxFinish</code>.
     * @return an {@link Interval}.
     */
    public Interval getInstance(Long minStart, Long maxStart,
            Granularity startGran, Long minFinish, Long maxFinish,
            Granularity finishGran) {
        if (minStart == null || maxStart == null || minFinish == null
                || maxFinish == null) {
            return new DefaultInterval(minStart, maxStart, startGran,
                    minFinish, maxFinish, finishGran, null, null, null);
        } else {
            return new SimpleInterval(minStart, maxStart,
                    startGran, minFinish, maxFinish, finishGran);
        }
    }

    /**
     * Returns at interval specified by the given start and finish and
     * granularities.
     *
     * @param start a {@link Long} representing the start of the interval. If
     * <code>null</code>, the <code>start</code> will be unbounded.
     * @param startGran the {@link Granularity} of the start of the interval.
     * The <code>start</code> parameter's interpretation depends on the
     * {@link Granularity} implementation provided.
     * @param finish a {@link Long} representing the finish of the interval.
     * If <code>null</code>, the <code>finish</code> will be unbounded.
     * @param finishGran the {@link Granularity} of the finish of the interval.
     * The <code>start</code> parameter's interpretation depends on the
     * {@link Granularity} implementation provided.
     * @return an {@link Interval}.
     */
    public Interval getInstance(Long start, Granularity startGran,
            Long finish, Granularity finishGran) {
        if (start == null || finish == null) {
            return new DefaultInterval(start, startGran, finish, finishGran);
        } else {
            return new SimpleInterval(start, startGran, finish, finishGran);
        }
    }

    /**
     * Returns an interval representing a position on the timeline or other
     * axis at a specified granularity. The interpretation of the
     * <code>position</code> parameter depends on what implementation of
     * {@link Granularity} is provided. For example, if the granularity
     * implementation is {@link AbsoluteTimeGranularity}, the
     * <code>position</code> is intepreted as a timestamp.
     *
     * @param position a {@ling Long} representing a single position on the
     * timeline or other axis. If <code>null</code>, the interval will be
     * unbounded.
     * @param gran a {@link Granularity}.
     * @return an {@link Interval}.
     */
    public Interval getInstance(Long position, Granularity gran) {
        if (position == null) {
            return new DefaultInterval(position, gran, position, gran);
        } else {
            return new SimpleInterval(position, gran);
        }
    }

    /**
     * Returns an unbounded interval.
     *
     * @return an {@link Interval}.
     */
    public Interval getInstance() {
        return new DefaultInterval();
    }
}
