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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Unit;

/**
 * @author Andrew Post
 */
public class TemporalExtendedPropositionDefinition extends ExtendedPropositionDefinition {

    private static final long serialVersionUID = -125025061319511802L;
    private Integer minLength = 0;
    private Unit minLengthUnit;
    private Integer maxLength;
    private Unit maxLengthUnit;

    public TemporalExtendedPropositionDefinition(String propositionId) {
        super(propositionId);
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public Unit getMaxLengthUnit() {
        return maxLengthUnit;
    }

    /**
     * @param maxLength The maxDuration to set.
     */
    public void setMaxLength(Integer maxLength) {
        if (this.maxLength != null && this.maxLength < 0) {
            this.maxLength = 0;
        } else {
            this.maxLength = maxLength;
        }
    }

    public void setMaxLengthUnit(Unit units) {
        this.maxLengthUnit = units;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.TemporalExtendedDefinition#getMinDurationTime()
     */
    public Integer getMinLength() {
        return minLength;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.TemporalExtendedDefinition#getMinDurationUnits()
     */
    public Unit getMinLengthUnit() {
        return minLengthUnit;
    }

    /**
     * @param minDuration The minDuration to set.
     */
    public void setMinLength(Integer minDuration) {
        if (minDuration == null || minDuration < 0) {
            this.minLength = 0;
        } else {
            this.minLength = minDuration;
        }
    }

    public void setMinLengthUnit(Unit units) {
        this.minLengthUnit = units;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.TemporalExtendedDefinition#getMatches(org.virginia.pbhs.parameters.Proposition)
     */
    @Override
    public boolean getMatches(Proposition proposition) {
        if (!super.getMatches(proposition)) {
            return false;
        }

        if (!(proposition instanceof TemporalProposition)) {
            return false;
        }

        TemporalProposition tp = (TemporalProposition) proposition;
        if (this.minLength != null
                && tp.getInterval().isLengthLessThan(this.minLength,
                this.minLengthUnit)) {
            return false;
        }
        if (this.maxLength != null
                && tp.getInterval().isLengthGreaterThan(this.maxLength,
                this.maxLengthUnit)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.TemporalExtendedDefinition#hasEqualFields(org.protempa.ExtendedPropositionDefinition)
     */
    @Override
    public boolean hasEqualFields(ExtendedPropositionDefinition obj) {
        if (!super.hasEqualFields(obj)) {
            return false;
        }

        if (!(obj instanceof TemporalExtendedPropositionDefinition)) {
            return false;
        }

        TemporalExtendedPropositionDefinition other = (TemporalExtendedPropositionDefinition) obj;
        boolean result = (minLength == null ? other.minLength == null
                : (minLength == other.minLength || minLength
                .equals(other.minLength)))
                && (minLengthUnit == null ? other.minLengthUnit == null
                : (minLengthUnit == other.minLengthUnit || minLengthUnit
                .equals(other.minLengthUnit)))
                && (maxLength == null ? other.maxLength == null
                : (maxLength == other.maxLength || maxLength
                .equals(other.maxLength)))
                && (maxLengthUnit == null ? other.maxLengthUnit == null
                : (maxLengthUnit == other.maxLengthUnit || maxLengthUnit
                .equals(other.maxLengthUnit)));
        return result;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
