package org.protempa;

import java.io.Serializable;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.Segment;

/**
 * A convenience class for implementing gap functions. In PROTEMPA, gap 
 * functions describe whether information inferred or data recorded during two
 * separate intervals should also be true during the gap between the 
 * two intervals. If a pair of intervals of the same type satisfy the 
 * gap function, PROTEMPA replaces them with a single interval that spans the 
 * two original intervals and the gap between them.
 * 
 * To implement a gap function, override
 * {@link #execute(org.protempa.proposition.interval.Interval, org.protempa.proposition.interval.Interval) }
 * with the implementation of your function. Gap functions only apply to
 * {@link AbstractParameter} propositions. A default concrete implementation is
 * provided, {@link SimpleGapFunction}, an instance of which is assigned to
 * the static variable {@link #DEFAULT}.
 * 
 * @author Andrew Post
 */
public abstract class GapFunction implements Serializable {
    
    private static final long serialVersionUID = -2463785054444632104L;
    public static final GapFunction DEFAULT = new SimpleGapFunction();

    /**
     * Returns whether two abstract parameters have a gap that satisfies
     * the constraints of the gap function. If either provided parameter is
     * <code>null</code>, this method returns <code>false</code>.
     * 
     * @param lhs the first {@link AbstractParameter}.
     * @param rhs the second {@link AbstractParameter}.
     * @return <code>true</code> or <code>false</code>.
     */
    public final boolean execute(AbstractParameter lhs, 
            AbstractParameter rhs) {
        if (lhs == null || rhs == null) {
            return false;
        } else {
            return execute(lhs.getInterval(), rhs.getInterval());
        }
    }

    /**
     * Returns whether two segments have a gap that satisfies
     * the constraints of the gap function. If either provided segment is
     * <code>null</code>, this method returns <code>false</code>.
     * 
     * @param lhs the first {@link Segment}.
     * @param rhs the second {@link Segment}.
     * @return <code>true</code> or <code>false</code>.
     */
    final boolean execute(Segment<? extends TemporalProposition> lhs, Segment<? extends TemporalProposition> rhs) {
        if (lhs == null || rhs == null) {
            return false;
        } else {
            return execute(lhs.getInterval(), rhs.getInterval());
        }
    }

    /**
     * Returns whether two intervals have a gap that satisfies
     * the constraints of the gap function. To be overridden by the actual
     * implementation of the gap function.
     * 
     * @param lhs the first {@link Interval}.
     * @param rhs the second {@link Interval}.
     * @return <code>true</code> or <code>false</code>.
     */
    public abstract boolean execute(Interval lhs, Interval rhs);
}
