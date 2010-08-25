package org.protempa;

import java.io.Serializable;

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
    private String startAbstractParamId;
    private Value startAbstractParamValue;
    private String finishAbstractParamId;
    private Value finishAbstractParamValue;
    private Integer startOffset;
    private Unit startOffsetUnits;
    private Integer finishOffset;
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

//    public String getFinishAbstractParamId() {
//        return finishAbstractParamId;
//    }
//
//    public void setFinishAbstractParamId(String finishAbstractParamId) {
//        this.finishAbstractParamId = finishAbstractParamId;
//    }

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

    public Integer getFinishOffset() {
        return finishOffset;
    }

    public void setFinishOffset(Integer finishOffset) {
        this.finishOffset = finishOffset;
    }

    public Unit getFinishOffsetUnits() {
        return finishOffsetUnits;
    }

    public void setFinishOffsetUnits(Unit finishOffsetUnits) {
        this.finishOffsetUnits = finishOffsetUnits;
    }

//    public String getStartAbstractParamId() {
//        return startAbstractParamId;
//    }
//
//    public void setStartAbstractParamId(String startAbstractParamId) {
//        this.startAbstractParamId = startAbstractParamId;
//    }

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

    public Integer getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
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
        StringBuilder buf = new StringBuilder("Offset start: ");
        buf.append(startAbstractParamId);
        buf.append("; ");
        buf.append(startAbstractParamValue);
        buf.append("; ");
        buf.append(startIntervalSide);
        buf.append("; ");
        buf.append(startOffset);
        buf.append("; ");
        buf.append(startOffsetUnits);
        buf.append("; offset finish: ");
        buf.append(finishAbstractParamId);
        buf.append("; ");
        buf.append(finishAbstractParamValue);
        buf.append("; ");
        buf.append(finishIntervalSide);
        buf.append("; ");
        buf.append(finishOffset);
        buf.append("; ");
        buf.append(finishOffsetUnits);
        return buf.toString();
    }
}
