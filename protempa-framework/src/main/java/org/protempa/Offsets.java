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

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.Value;

/**
 * Defines offsets for the interval of an abstract parameter defined by a
 * complex abstract parameter definition.
 * 
 * @author Andrew Post
 * 
 */
public final class Offsets implements Serializable {

    private static final long serialVersionUID = -3143801827380255487L;
    private Value startAbstractParamValue;
    private Value finishAbstractParamValue;
    private int startOffset;
    private Unit startOffsetUnits;
    private int finishOffset;
    private Unit finishOffsetUnits;
    private IntervalSide startIntervalSide = IntervalSide.START;
    private IntervalSide finishIntervalSide = IntervalSide.FINISH;
    private TemporalExtendedPropositionDefinition startExtPropDef;
    private TemporalExtendedPropositionDefinition finishExtPropDef;

    public TemporalExtendedPropositionDefinition
            getFinishTemporalExtendedPropositionDefinition() {
        return this.finishExtPropDef;
    }

    public void setFinishTemporalExtendedPropositionDefinition(
            TemporalExtendedPropositionDefinition tepd) {
        this.finishExtPropDef = tepd;
    }

    public Value getFinishAbstractParamValue() {
        return finishAbstractParamValue;
    }

    public void setFinishAbstractParamValue(Value finishAbstractParamValue) {
        this.finishAbstractParamValue = finishAbstractParamValue;
    }

    public IntervalSide getFinishIntervalSide() {
        return finishIntervalSide;
    }

    public void setFinishIntervalSide(IntervalSide finishIntervalSide) {
        if (finishIntervalSide != null) {
            this.finishIntervalSide = finishIntervalSide;
        }
    }

    public int getFinishOffset() {
        return finishOffset;
    }

    public void setFinishOffset(int finishOffset) {
        this.finishOffset = finishOffset;
    }

    public Unit getFinishOffsetUnits() {
        return finishOffsetUnits;
    }

    public void setFinishOffsetUnits(Unit finishOffsetUnits) {
        this.finishOffsetUnits = finishOffsetUnits;
    }

    public TemporalExtendedPropositionDefinition
            getStartTemporalExtendedPropositionDefinition() {
        return this.startExtPropDef;
    }

    public void setStartTemporalExtendedPropositionDefinition(
            TemporalExtendedPropositionDefinition tepd) {
        this.startExtPropDef = tepd;
    }

    public Value getStartAbstractParamValue() {
        return startAbstractParamValue;
    }

    public void setStartAbstractParamValue(Value startAbstractParamValue) {
        this.startAbstractParamValue = startAbstractParamValue;
    }

    public IntervalSide getStartIntervalSide() {
        return startIntervalSide;
    }

    public void setStartIntervalSide(IntervalSide startIntervalSide) {
        if (startIntervalSide != null) {
            this.startIntervalSide = startIntervalSide;
        }
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public Unit getStartOffsetUnits() {
        return startOffsetUnits;
    }

    public void setStartOffsetUnits(Unit startOffsetUnits) {
        this.startOffsetUnits = startOffsetUnits;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
