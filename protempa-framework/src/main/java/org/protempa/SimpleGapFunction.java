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
package org.protempa;

import java.beans.PropertyChangeSupport;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.Unit;

/**
 * Implements a gap function with a simple max range: if one interval is before
 * the second and the distance between them is less than or equal to the
 * supplied maximum gap, then the function returns <code>true</code>, 
 * otherwise it returns <code>false</code>.
 * 
 * @author Andrew Post
 */
public final class SimpleGapFunction extends GapFunction {

    private static final long serialVersionUID = -6154012083447646091L;
    private static final Integer ZERO = Integer.valueOf(0);
    private Integer maximumGap;
    private Unit maximumGapUnits;
    private Relation relation;
    protected final PropertyChangeSupport changes;

    /**
     * Instantiates an instance with the default maximum gap and units. The
     * default maximum gap value is <code>null</code>, which means any gap will
     * cause the gap function to return <code>true</code>. The default units
     * is <code>null</code>, the meaning of which is defined by the 
     * {@link Granularity} of the data.
     */
    public SimpleGapFunction() {
        this(null, null);
    }

    /**
     * Initializes a gap function with a maximum gap and units.
     * 
     * @param maximumGap the maximum gap {@link Integer}. Must be non-negative.
     * If set to zero, the gap function always will return <code>false</code>.
     * @param maximumGapUnit a {@link Unit}. A value of <code>null</code> is
     * defined by the {@link Granularity} of the data.
     */
    public SimpleGapFunction(Integer maximumGap, Unit maximumGapUnit) {
        if (maximumGap != null && maximumGap.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    "maximumGap must be null or >= 0");
        }
        this.maximumGapUnits = maximumGapUnit;
        this.maximumGap = maximumGap;
        initRelation();
        this.changes = new PropertyChangeSupport(this);

    }

    /**
     * Executes the gap function. If the first interval (<code>lhs</code>) is 
     * before the second (<code>rhs</code>) and the distance between them is
     * less than or equal to the <code>maximumGap</code>, then
     * this method returns <code>true</code>, otherwise it returns 
     * <code>false</code>.
     * 
     * @param lhs the first {@link Interval}.
     * @param rhs the second {@link Interval}.
     * @return <code>true</code> or <code>false</code>.
     */
    @Override
    public boolean execute(Interval lhs, Interval rhs) {
        if (this.relation == null) {
            return false;
        } else {
            return this.relation.hasRelation(lhs, rhs);
        }
    }

    /**
     * Returns the gap function's maximum gap value. The default value is 
     * <code>null</code>, which means any gap.
     *
     * @return an {@link Integer}.
     */
    public Integer getMaximumGap() {
        return maximumGap;
    }

    /**
     * Returns the units of the maximum gap value. The default value is
     * <code>null</code>, which means milliseconds.
     * 
     * @return a {@link Unit}.
     */
    public Unit getMaximumGapUnit() {
        return maximumGapUnits;
    }

    /**
     * Sets the the gap function's maximum gap value. A value of 
     * <code>null</code> means any gap.
     *
     * @param maximumGap the maximum gap {@link Integer}. Must be non-negative.
     * If set to zero, the gap function always will return <code>false</code>.
     */
    public void setMaximumGap(Integer maximumGap) {
        if (maximumGap != null && maximumGap.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    "maximumGap must be null or >= 0");
        }
        Integer old = this.maximumGap;
        this.maximumGap = maximumGap;
        initRelation();
        this.changes.firePropertyChange("maximumGap", old, this.maximumGap);
    }

    /**
     * Sets the maximum gap value's units. A value of <code>null</code> means
     * milliseconds.
     * 
     * @param unit a {@link Unit} value.
     */
    public void setMaximumGapUnit(Unit unit) {
        Unit old = this.maximumGapUnits;
        this.maximumGapUnits = unit;
        initRelation();
        this.changes.firePropertyChange("maximumGapUnit", old,
                this.maximumGapUnits);
    }

    private void initRelation() {
        if (ZERO.equals(this.maximumGap)) {
            this.relation = null;
        } else {
            this.relation = new Relation(null, null, 
                    null, null, 
                    null, null, 
                    null, null, 
                    0, this.maximumGapUnits, 
                    this.maximumGap, this.maximumGapUnits, 
                    null, null, 
                    null, null);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("maximumGap", this.maximumGap)
                .append("maximumGapUnits", this.maximumGapUnits)
                .toString();
    }
}
