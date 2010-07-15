package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import java.util.Map;

public final class ColumnSpecInfo {

    private List<ColumnSpec> columnSpecs;
    private boolean distinct;
    private int startTimeIndex = -1;
    private int finishTimeIndex = -1;
    private Map<String, Integer> propertyValueIndices = null;
    private int codeIndex = -1;

    ColumnSpecInfo() {

    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public int getCodeIndex() {
        return codeIndex;
    }

    public void setCodeIndex(int codeIndex) {
        this.codeIndex = codeIndex;
    }

    public List<ColumnSpec> getColumnSpecs() {
        return columnSpecs;
    }

    public void setColumnSpecs(List<ColumnSpec> columnSpecs) {
        this.columnSpecs = columnSpecs;
    }

    public int getFinishTimeIndex() {
        return finishTimeIndex;
    }

    public void setFinishTimeIndex(int finishTimeIndex) {
        this.finishTimeIndex = finishTimeIndex;
    }

    public int getStartTimeIndex() {
        return startTimeIndex;
    }

    public void setStartTimeIndex(int startTimeIndex) {
        this.startTimeIndex = startTimeIndex;
    }

    public Map<String, Integer> getPropertyValueIndices() {
        return propertyValueIndices;
    }

    public void setPropertyValueIndices(Map<String, Integer> valueIndex) {
        this.propertyValueIndices = valueIndex;
    }
}
