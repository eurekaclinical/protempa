package org.protempa;

import java.io.Serializable;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.Unit;

final class MinMaxGapBetweenValues implements Serializable {

    private static final long serialVersionUID = -8071552755205112189L;
    
    private int minimumGapBetweenValues;
    private Unit minimumGapBetweenValuesUnits;
    private Integer maximumGapBetweenValues;
    private Unit maximumGapBetweenValuesUnits;
    private MinMaxGapFunction gapFunction;

    Integer getMinimumGapBetweenValues() {
        return this.minimumGapBetweenValues;
    }

    Unit getMinimumGapBetweenValuesUnits() {
        return this.minimumGapBetweenValuesUnits;
    }

    void setMinimumGapBetweenValues(Integer minimumGapBetweenValues) {
        if (minimumGapBetweenValues == null
                || minimumGapBetweenValues.compareTo(0) < 0) {
            this.minimumGapBetweenValues = 0;
        } else {
            this.minimumGapBetweenValues = minimumGapBetweenValues;
        }
    }

    void setMinimumGapBetweenValuesUnits(Unit units) {
        this.minimumGapBetweenValuesUnits = units;
    }

    Integer getMaximumGapBetweenValues() {
        return this.maximumGapBetweenValues;
    }

    Unit getMaximumGapBetweenValuesUnits() {
        return this.maximumGapBetweenValuesUnits;
    }

    void setMaximumGapBetweenValues(Integer maximumGapBetweenValues) {
        if (maximumGapBetweenValues == null) {
            this.maximumGapBetweenValues = null;
        } else if (maximumGapBetweenValues.compareTo(0) < 0) {
            this.maximumGapBetweenValues = 0;
        } else {
            this.maximumGapBetweenValues = maximumGapBetweenValues;
        }
    }

    void setMaximumGapBetweenValuesUnits(Unit units) {
        this.maximumGapBetweenValuesUnits = units;
    }

    boolean satisfiesGap(PrimitiveParameter pp1, PrimitiveParameter pp2) {
        if (this.gapFunction == null) {
            this.gapFunction = new MinMaxGapFunction(
                    this.minimumGapBetweenValues,
                    this.minimumGapBetweenValuesUnits,
                    this.maximumGapBetweenValues,
                    this.maximumGapBetweenValuesUnits);
        }
        return this.gapFunction.execute(pp1.getInterval(), pp2.getInterval());
    }

    protected String debugMessage() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("minimumGapBetweenValues=" + this.minimumGapBetweenValues
                + " " + this.minimumGapBetweenValuesUnits + ", ");
        buffer.append("maximumGapBetweenValues=" + this.maximumGapBetweenValues
                + this.maximumGapBetweenValuesUnits + ", ");
        return buffer.toString();
    }
}
