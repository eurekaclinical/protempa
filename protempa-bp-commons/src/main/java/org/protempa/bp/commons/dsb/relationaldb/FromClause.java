package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class FromClause implements SQLClause {
    private final List<ColumnSpec> columnSpecs;
    private final Map<ColumnSpec, Integer> referenceIndices;
    
    FromClause(List<ColumnSpec> columnSpecs, Map<ColumnSpec, Integer> referenceIndices) {
        this.columnSpecs = Collections.unmodifiableList(columnSpecs);
        this.referenceIndices = Collections.unmodifiableMap(referenceIndices);
    }
    
    public String generateClause() {
        return "";
    }
}
