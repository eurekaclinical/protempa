package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;

final class ColumnSpecInfo {
    private boolean unique;
    private List<ColumnSpec> columnSpecs;
    private int startTimeIndex = -1;
    private int finishTimeIndex = -1;
    private Map<String, Integer> propertyIndices = null;
    private int valueIndex = -1;
    private int codeIndex = -1;
    private int[] uniqueIdIndices;
    private boolean usingKeyIdIndex;

    ColumnSpecInfo() {

    }

    boolean isUnique() {
        return unique;
    }

    void setUnique(boolean unique) {
        this.unique = unique;
    }

    int getCodeIndex() {
        return codeIndex;
    }

    void setCodeIndex(int codeIndex) {
        this.codeIndex = codeIndex;
    }

    List<ColumnSpec> getColumnSpecs() {
        return columnSpecs;
    }

    void setColumnSpecs(List<ColumnSpec> columnSpecs) {
        this.columnSpecs = columnSpecs;
    }

    int getFinishTimeIndex() {
        return finishTimeIndex;
    }

    void setFinishTimeIndex(int finishTimeIndex) {
        this.finishTimeIndex = finishTimeIndex;
    }

    int getStartTimeIndex() {
        return startTimeIndex;
    }

    void setStartTimeIndex(int startTimeIndex) {
        this.startTimeIndex = startTimeIndex;
    }

    Map<String, Integer> getPropertyIndices() {
        return propertyIndices;
    }

    void setPropertyIndices(Map<String, Integer> index) {
        this.propertyIndices = index;
    }

    int getValueIndex() {
        return valueIndex;
    }

    void setValueIndex(int valueIndex) {
        this.valueIndex = valueIndex;
    }

    void setUniqueIdIndices(int[] uniqueIdIndices) {
        this.uniqueIdIndices = uniqueIdIndices;
    }

    int[] getUniqueIdIndices() {
        return this.uniqueIdIndices;
    }

    boolean isUsingKeyIdIndex() {
        return this.usingKeyIdIndex;
    }

    void setUsingKeyIdIndex(boolean usingKeyIdIndex) {
        this.usingKeyIdIndex = usingKeyIdIndex;
    }
}
