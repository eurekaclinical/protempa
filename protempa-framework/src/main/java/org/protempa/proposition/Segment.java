package org.protempa.proposition;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.RandomAccess;

import org.protempa.proposition.value.Granularity;

/**
 * A segment of a sequence of <code>Parameter</code> objects provided to
 * <code>Detector</code> instances' <code>satisfiedBy()</code> method. Do
 * not attempt to cache a <code>Segment</code> object! <code>Segment</code>
 * objects may be reused by PROTEMPA for performance reasons, and the value of a
 * <code>Segment</code> object is only guaranteed to be consistent until
 * <code>satisfiedBy()</code> ends.
 * 
 * @author Andrew Post
 */
public class Segment<T extends TemporalProposition> extends AbstractList<T>
        implements RandomAccess {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private Sequence<T> ts;
    private int x = -1;
    private int y = -1;
    protected int modCount = 0;
    private boolean intervalStale = true;
    private T maxFinishParam;
    private Interval interval;

    public Segment(Sequence<T> seq, int firstIndex, int lastIndex) {
        if (seq == null) {
            throw new IllegalArgumentException("seq cannot be null!");
        }
        resetState(seq, firstIndex, lastIndex);
    }

    public Segment(Sequence<T> seq) {
        this(seq, 0, seq != null ? seq.size() - 1 : 0);
    }

    public Segment(Segment<T> segment) {
        this(segment != null ? segment.ts : null, segment != null ? segment.getFirstIndex() : 0, segment != null ? segment.getLastIndex()
                : 0);
    }

    public Sequence<T> getSequence() {
        return ts;
    }

    public Interval getInterval() {
        if (intervalStale || interval == null) {
            interval = intervalCreator();
            intervalStale = false;
        }
        return interval;
    }

    protected Interval intervalCreator() {
        if (size() == 1) {
            return first().getInterval();
        } else {
            Long minStart = minimumStart(this);
            Long maxStart = maximumStart(this);
            Long minFinish = minimumFinish(this);
            Long maxFinish = maximumFinish(this);
            return intervalFactory.getInstance(minStart, maxStart,
                    getStartGranularity(),
                    minFinish, maxFinish, getFinishGranularity());
        }
    }

    public int getFirstIndex() {
        if (x == -1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return x;
    }

    public int getLastIndex() {
        if (y == -1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return y;
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof TemporalProposition)) {
            return -1;
        } else {
            return Collections.binarySearch(this, (TemporalProposition) o,
                    PropositionUtil.TEMP_PROP_COMP);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    public T first() {
        return ts.get(getFirstIndex());
    }

    public T last() {
        return ts.get(getLastIndex());
    }

    public Granularity getFinishGranularity() {
        calcMaxFinishParam();
        return maxFinishParam.getInterval().getFinishGranularity();
    }

    public Granularity getStartGranularity() {
        return first().getInterval().getStartGranularity();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Collection#size()
     */
    @Override
    public int size() {
        if (x == -1) {
            return 0;
        } else {
            return y - x + 1;
        }

    }

    private void calcMaxFinishParam() {
        if (maxFinishParam == null) {
            maxFinishParam = Collections.max(this,
                    PropositionUtil.MAX_FINISH_COMP);
        }
    }

    public Segment<T> resetState(Sequence<T> sequence) {
        if (sequence == null) {
            return null;
        } else {
            return resetState(sequence, 0, sequence.size() - 1);
        }
    }

    public Segment<T> resetState(Sequence<T> sequence, int firstIndex,
            int lastIndex) {

        if (sequence == null || firstIndex >= sequence.size()
                || lastIndex >= sequence.size() || firstIndex > lastIndex) {
            return null;
        }

        ts = sequence;
        x = firstIndex;
        y = lastIndex;
        modCount++;
        intervalStale = true;
        maxFinishParam = null;
        return this;

    }

    @Override
    public T get(int index) {
        return (T) ts.get(x + index);
    }

    /**
     * Unsupported operation.
     *
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    private Long maximumFinish(Segment<T> segment) {
        calcMaxFinishParam();
        return maxFinishParam.getInterval().getMaxFinish();
    }

    private Long minimumStart(Segment<T> segment) {
        /*
         * Returns the smallest minimum start, which is that of the first
         * element.
         */
        return segment.first().getInterval().getMinStart();
    }
    private Comparator<TemporalProposition> MAX_START_COMP =
            new Comparator<TemporalProposition>() {

                @Override
                public int compare(TemporalProposition p0, TemporalProposition p1) {
                    return p0.getInterval().getMaximumStart().compareTo(
                            p1.getInterval().getMaximumStart());
                }
            };

    private Long maximumStart(
            Segment<T> segment) {
        return Collections.min(segment, MAX_START_COMP).getInterval().getMaxStart();
    }
    private Comparator<TemporalProposition> MIN_FINISH_COMP =
            new Comparator<TemporalProposition>() {

                @Override
                public int compare(TemporalProposition p0, TemporalProposition p1) {

                    return p0.getInterval().getMinimumFinish().compareTo(
                            p1.getInterval().getMinimumFinish());
                }
            };

    private Long minimumFinish(
            Segment<T> segment) {
        return Collections.max(segment, MIN_FINISH_COMP).getInterval().getMinFinish();
    }
}
