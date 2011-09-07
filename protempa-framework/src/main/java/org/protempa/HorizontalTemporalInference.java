package org.protempa;

import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.Segment;
import org.protempa.proposition.TemporalProposition;

/**
 * Checks whether the union of two abstractions of the same type should be
 * taken based on their intervals.
 * 
 * @author Andrew Post
 * 
 */
final class HorizontalTemporalInference {

    /**
     * Computes whether the union of two segments of temporal propositions
     * should be taken.
     *
     * We assume that <code>tp1</code> is before or at the same time as
     * <code>tp2</code>, and that <code>tp1</code> and <code>tp2</code>
     * are instances of <code>propDef</code>.
     *
     * @param propDef
     *            a {@link PropositionDefinition}.
     * @param tp1
     *            a {@link Segment<? extends TemporalProposition>}.
     * @param tp2
     *            a {@link Segment<? extends TemporalProposition>}.
     * @return <code>true</code> if they should be combined,
     *         <code>false</code> otherwise.
     */
    <T extends TemporalProposition> boolean execute(
            PropositionDefinition propDef, Segment<T> tp1, Segment<T> tp2) {
        if (tp1 == null || tp2 == null) {
            return false;
        }

        Interval tp1Ival = null;
        Interval tp2Ival = null;
        if (propDef.isConcatenable()) {
            tp1Ival = tp1.getInterval();
            tp2Ival = tp2.getInterval();

            if (tp2Ival.getMinimumStart().equals(tp1Ival.getMinimumFinish())
                    && tp2Ival.getMaximumStart().equals(
                    tp1Ival.getMaximumFinish())) {
                return true;
            }
        }

        if (propDef.isSolid()) {
            if (tp1Ival == null) {
                tp1Ival = tp1.getInterval();
            }
            if (tp2Ival == null) {
                tp2Ival = tp2.getInterval();
            }

            if (tp2Ival.getMinimumStart().compareTo(
                    tp1Ival.getMinimumFinish()) < 0
                    || tp2Ival.getMaximumStart().compareTo(
                    tp1Ival.getMaximumFinish()) < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Computes whether the union of two segments of temporal propositions
     * should be taken.
     *
     * We assume that <code>tp1</code> is before or at the same time as
     * <code>tp2</code>, and that <code>tp1</code> and <code>tp2</code>
     * are instances of <code>propDef</code>.
     *
     * @param propDef
     *            a {@link PropositionDefinition}.
     * @param tp1
     *            a {@link TemporalProposition}.
     * @param tp2
     *            a {@link TemporalProposition}.
     * @return <code>true</code> if they should be combined,
     *         <code>false</code> otherwise.
     */
    <T extends TemporalProposition> boolean execute(
            PropositionDefinition propDef, T tp1, T tp2) {
        if (tp1 == null || tp2 == null) {
            return false;
        }

        Interval tp1Ival = null;
        Interval tp2Ival = null;
        if (propDef.isConcatenable()) {
            tp1Ival = tp1.getInterval();
            tp2Ival = tp2.getInterval();
            if (tp2Ival.getMinimumStart().equals(tp1Ival.getMinimumFinish())
                    && tp2Ival.getMaximumStart().equals(
                    tp1Ival.getMaximumFinish())) {
                return true;
            }
        }

        if (propDef.isSolid()) {
            if (tp1Ival == null) {
                tp1Ival = tp1.getInterval();
            }
            if (tp2Ival == null) {
                tp2Ival = tp2.getInterval();
            }
            if (tp2Ival.getMinimumStart().compareTo(
                    tp1Ival.getMinimumFinish()) < 0
                    || tp2Ival.getMaximumStart().compareTo(
                    tp1Ival.getMaximumFinish()) < 0) {
                return true;
            }
        }
        return false;
    }
}
