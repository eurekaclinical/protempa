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
package org.protempa.proposition.interval;

import java.io.Serializable;
import java.util.Arrays;

import org.protempa.proposition.value.Unit;

/**
 * A representation of the temporal relationship between two intervals, as
 * described in Stillman et al. Tachyon: A constraint-based temporal reasoning
 * model and its implementation. SIGART Bulletin. 1993;4(3):T1-T4, which was in
 * turn inspired by Dechter's simple temporal problem, as defined in Dechter, R.
 * et al. Temporal Constraint Networks. Artif. Intell. 1991;49:61-95.
 * 
 * @author Andrew Post
 */
public final class Relation implements Serializable {

    private static final long serialVersionUID = -7813328179876098366L;
    private final Integer[] intValues;
    private final Unit[] units;
    
    public static final Relation BEFORE = new Relation(null, null, null, null, 
                                        null, null, null, null, 
                                        1, null, null, null,
                                        null, null, null, null);
    public static final Relation AFTER = BEFORE.inverse();
    
    public static final Relation OVERLAPS = new Relation(1, null, null, null, 
            null, null, null, null, 
            null, null, -1, null, 
            1, null, null, null);
    public static final Relation OVERLAPPED_BY = OVERLAPS.inverse();
    
    public static final Relation MEETS = new Relation(null, null, null, null, 
            null, null, null, null, 
            0, null, 0, null, 
            null, null, null, null);
    public static final Relation MET_BY = MEETS.inverse();
    
    public static final Relation EQUALS = new Relation(0, null, 0, null, 
            null, null, null, null,
            null, null, null, null, 
            0, null, 0, null);
    
    public static final Relation CONTAINS = new Relation(
            1, null, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, -1, null);
    
    public static final Relation CONTAINS_OR_EQUALS = new Relation(
            0, null, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, 0, null);
    
    public static final Relation DURING = CONTAINS.inverse();
    
    public static final Relation STARTS = new Relation(
            0, null, 0, null,
            null, null, null, null,
            null, null, null, null,
            0, null, -1, null);
    
    public static final Relation STARTED_BY = STARTS.inverse();
    
    public static final Relation FINISHES = new Relation(
            1, null, null, null,
            null, null, null, null,
            null, null, null, null,
            0, null, 0, null);
    
    public static final Relation FINISHED_BY = STARTS.inverse();

    public Relation() {
        this(null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null);
    }

    /**
     * Constructs a new <code>Relation</code> between two intervals from
     * <code>Weight</code>s and <code>Unit</code>s.
     * 
     * @param minDistanceBetweenStarts
     *            the minimum distance between the starts of the intervals.
     * @param minDistanceBetweenStartsUnits
     *            units for the minimum distance between the starts of the
     *            intervals.
     * @param maxDistanceBetweenStarts
     *            the maximum distance between the starts of the intervals.
     * @param maxDistanceBetweenStartsUnits
     *            units for the maximum distance between the starts of the
     *            intervals.
     * @param minSpan
     *            the minimum distance between the start of interval 1 and the
     *            finish of interval 2.
     * @param minSpanUnits
     *            units for the minimum distance between the start of interval 1
     *            and the finish of interval 2.
     * @param maxSpan
     *            the maximum distance between the start of interval 1 and the
     *            finish of interval 2.
     * @param maxSpanUnits
     *            units for the maximum distance between the start of interval 1
     *            and the finish of interval 2.
     * @param minDistanceBetween
     *            the minimum distance between the finish of interval 1 and the
     *            start of interval 2.
     * @param minDistanceBetweenUnits
     *            units for the minimum distance between the finish of interval
     *            1 and the start of interval 2.
     * @param maxDistanceBetween
     *            the maximum distance between the finish of interval 1 and the
     *            start of interval 2.
     * @param maxDistanceBetweenUnits
     *            units for the maximum distance between the finish of interval
     *            1 and the start of interval 2.
     * @param minDistanceBetweenFinishes
     *            the minimum distance between the finish of interval 1 and the
     *            finish of interval 2.
     * @param minDistanceBetweenFinishesUnits
     *            units for the minimum distance between the finish of interval
     *            1 and the finish of interval 2.
     * @param maxDistanceBetweenFinishes
     *            the maximum distance between the finish of interval 1 and the
     *            finish of interval 2.
     * @param maxDistanceBetweenFinishesUnits
     *            units for the maximum distance between the finish of interval
     *            1 and the finish of interval 2.
     * @throws IllegalArgumentException
     *             if <code>max</code> gt; <code>min</code> for any of the
     *             constraints.
     */
    public Relation(Integer minDistanceBetweenStarts,
            Unit minDistanceBetweenStartsUnits,
            Integer maxDistanceBetweenStarts,
            Unit maxDistanceBetweenStartsUnits, Integer minSpan,
            Unit minSpanUnits, Integer maxSpan, Unit maxSpanUnits,
            Integer minDistanceBetween, Unit minDistanceBetweenUnits,
            Integer maxDistanceBetween, Unit maxDistanceBetweenUnits,
            Integer minDistanceBetweenFinishes,
            Unit minDistanceBetweenFinishesUnits,
            Integer maxDistanceBetweenFinishes,
            Unit maxDistanceBetweenFinishesUnits) {
        this(new Integer[]{minDistanceBetweenStarts,
                    maxDistanceBetweenStarts, minSpan, maxSpan, minDistanceBetween,
                    maxDistanceBetween, minDistanceBetweenFinishes,
                    maxDistanceBetweenFinishes},
                new Unit[]{minDistanceBetweenStartsUnits,
                    maxDistanceBetweenStartsUnits, minSpanUnits, maxSpanUnits,
                    minDistanceBetweenUnits, maxDistanceBetweenUnits,
                    minDistanceBetweenFinishesUnits,
                    maxDistanceBetweenFinishesUnits});

    }

    /**
     * Both public constructors call this constructor to do the real setup.
     * 
     * @param values
     *            a <code>Weight[]</code> of length 8 storing, in order,
     *            <code>minDistanceBetweenStarts</code>,
     *            <code>maxDistanceBetweenStarts</code>, <code>minSpan</code>,
     *            <code>maxSpan</code>, <code>minDistanceBetween</code>,
     *            <code>maxDistanceBetween</code>,
     *            <code>minDistanceBetweenFinishes</code>,
     *            <code>maxDistanceBetweenFinishes</code>, in the base units. Do
     *            not provide a <code>null</code> array or an array with
     *            <code>null</code> values or a wrong-sized array; this
     *            constructor doesn't check for any of those conditions.
     * @param units
     *            a {@link Unit[]} of length 8 storing the {@link Unit}s
     *            corresponding to the specified values.
     * @throws IllegalArgumentException
     *             if <code>max</code> gt; <code>min</code> for any of the
     *             constraints.
     */
    private Relation(Integer[] intValues, Unit[] units) {
        this.intValues = intValues;

        this.units = units;
    }

    public Relation inverse() {
        return new Relation(new Integer[]{this.intValues[1],
                    this.intValues[0], this.intValues[5], this.intValues[4],
                    this.intValues[3], this.intValues[2], this.intValues[7],
                    this.intValues[6]}, new Unit[]{this.units[1], this.units[0],
                    this.units[5], this.units[4], this.units[3], this.units[2],
                    this.units[7], this.units[6]});
    }

    public Integer getMinDistanceBetweenStarts() {
        return this.intValues[0];
    }

    public Unit getMinDistanceBetweenStartsUnits() {
        return this.units[0];
    }

    public Integer getMaxDistanceBetweenStarts() {
        return this.intValues[1];
    }

    public Unit getMaxDistanceBetweenStartsUnits() {
        return this.units[1];
    }

    public Integer getMinSpan() {
        return this.intValues[2];
    }

    public Unit getMinSpanUnits() {
        return this.units[2];
    }

    public Integer getMaxSpan() {
        return this.intValues[3];
    }

    public Unit getMaxSpanUnits() {
        return this.units[3];
    }

    public Integer getMinDistanceBetween() {
        return this.intValues[4];
    }

    public Unit getMinDistanceBetweenUnits() {
        return this.units[4];
    }

    public Integer getMaxDistanceBetween() {
        return this.intValues[5];
    }

    public Unit getMaxDistanceBetweenUnits() {
        return this.units[5];
    }

    public Integer getMinDistanceBetweenFinishes() {
        return this.intValues[6];
    }

    public Unit getMinDistanceBetweenFinishesUnits() {
        return this.units[6];
    }

    public Integer getMaxDistanceBetweenFinishes() {
        return this.intValues[7];
    }

    public Unit getMaxDistanceBetweenFinishesUnits() {
        return this.units[7];
    }

    /**
     * Determines whether the given intervals have this relation.
     * 
     * @param interval1
     *            the left-hand-side {@link Interval}.
     * @param interval2
     *            the right-hand-side {@link Interval}.
     * @return <code>true</code> if the intervals have this relation,
     *         <code>false</code> otherwise. Returns <code>false</code> if any
     *         <code>null</code> arguments are given.
     */
    public boolean hasRelation(Interval interval1, Interval interval2) {
        if (interval1 == null || interval2 == null) {
            return false;
        }
        Long minStart1 = interval1.getMinimumStart();
        Long maxStart1 = interval1.getMaximumStart();
        Long minFinish1 = interval1.getMinimumFinish();
        Long maxFinish1 = interval1.getMaximumFinish();
        Long minStart2 = interval2.getMinimumStart();
        Long maxStart2 = interval2.getMaximumStart();
        Long minFinish2 = interval2.getMinimumFinish();
        Long maxFinish2 = interval2.getMaximumFinish();
        return evenHasRelationCheck(0, minStart1, minStart2)
                && oddHasRelationCheck(1, maxStart1, maxStart2)
                && evenHasRelationCheck(2, minStart1, minFinish2)
                && oddHasRelationCheck(3, maxStart1, maxFinish2)
                && evenHasRelationCheck(4, minFinish1, minStart2)
                && oddHasRelationCheck(5, maxFinish1, maxStart2)
                && evenHasRelationCheck(6, minFinish1, minFinish2)
                && oddHasRelationCheck(7, maxFinish1, maxFinish2);
    }

    private boolean evenHasRelationCheck(int i, Long lhs, Long rhs) {
        if (this.units[i] != null && lhs != null && rhs != null
                && this.intValues[i] != null) {
            long lhsl = lhs;
            long rhsl = rhs;
            if (lhsl <= rhsl) {
                return isLessThanOrEqualToDuration(this.units[i], lhsl, rhsl,
                        this.intValues[i]);
            } else {
                return isGreaterThanOrEqualToDuration(this.units[i], rhsl,
                        lhsl, -this.intValues[i]);
            }
        } else if (lhs != null && rhs != null && this.intValues[i] != null) {
            return lhs + this.intValues[i] <= rhs;
        } else if ((rhs != null || lhs == null) && this.intValues[i] == null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean oddHasRelationCheck(int i, Long lhs, Long rhs) {
        if (this.units[i] != null && lhs != null && rhs != null
                && this.intValues[i] != null) {
            return isGreaterThanOrEqualToDuration(this.units[i], lhs, rhs,
                    this.intValues[i]);
        } else if (lhs != null && rhs != null && this.intValues[i] != null) {
            return lhs + this.intValues[i] >= rhs;
        } else if ((lhs != null || rhs == null) && this.intValues[i] == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines whether <code>position1</code> plus <code>distance</code> is
     * greater than or equal to <code>position2</code>.
     * 
     * @param position1
     *            a <code>long</code>.
     * @param position2
     *            a <code>long</code>.
     * @param distance
     *            a <code>long</code>.
     * @return <code>true</code> if <code>position1</code> plus
     *         <code>distance</code> is greater than or equal to
     *         <code>position2</code>, <code>false</code> if not.
     */
    static boolean isGreaterThanOrEqualToDuration(Unit unit,
            long position1, long position2, int duration) {
        return unit.addToPosition(position1, duration + 1) - 1 >= position2;
    }

    /**
     * Determines whether <code>position1</code> plus <code>distance</code> is
     * less than or equal to <code>position2</code>.
     * 
     * @param position1
     *            a <code>long</code>, guaranteed to be less than or equal to
     *            <code>position2</code>.
     * @param position2
     *            a <code>long</code>.
     * @param distance
     *            a <code>long</code>.
     * @return <code>true</code> if <code>position1</code> plus
     *         <code>distance</code> is greater than or equal to
     *         <code>position2</code>, <code>false</code> if not.
     */
    static boolean isLessThanOrEqualToDuration(Unit unit,
            long position1, long position2, int duration) {
        return unit.addToPosition(position1, duration) <= position2;
    }

    @Override
    public String toString() {
        return "RELATION: " + Arrays.asList(this.intValues) + "; "
                + Arrays.asList(this.units);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(intValues);
		result = prime * result + Arrays.hashCode(units);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Relation other = (Relation) obj;
		if (!Arrays.equals(intValues, other.intValues)) {
			return false;
		}
		if (!Arrays.equals(units, other.units)) {
			return false;
		}
		return true;
	}
}
