package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import java.util.Map;

final class ColumnSpecInfo {
    private boolean unique;
    private List<ColumnSpec> columnSpecs;
    private int startTimeIndex = -1;
    private int finishTimeIndex = -1;
    private Map<String, Integer> propertyIndices = null;
    private int codeIndex = -1;
    private Map<String, List<ReferenceSpec>> references;

    ColumnSpecInfo() {

    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public Map<String, List<ReferenceSpec>> getReferences() {
        return references;
    }

    public void setReferences(Map<String, List<ReferenceSpec>> references) {
        this.references = references;
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

    public Map<String, Integer> getPropertyIndices() {
        return propertyIndices;
    }

    public void setPropertyIndices(Map<String, Integer> index) {
        this.propertyIndices = index;
    }
}
