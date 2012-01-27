package org.protempa.bp.commons.dsb.relationaldb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

final class StagingSelectClause implements SelectClause {

    private final StagingSpec stagingSpec;
    private final EntitySpec entitySpec;
    private final TableAliaser referenceIndices;

    public StagingSelectClause(StagingSpec stagingSpec,
            EntitySpec entitySpec, TableAliaser referenceIndices) {
        this.stagingSpec = stagingSpec;
        this.entitySpec = entitySpec;
        this.referenceIndices = referenceIndices;
    }

    @Override
    public String generateClause() {
        StringBuilder result = new StringBuilder();

        result.append("SELECT ");

        List<String> cols = new ArrayList<String>();
        for (StagedColumnSpec columnSpec : this.stagingSpec.getStagedColumns()) {
            StringBuilder colStr = new StringBuilder(
                    referenceIndices.generateColumnReference(columnSpec
                            .toColumnSpec()));
            if (null != columnSpec.getAsName()
                    && !columnSpec.getAsName().isEmpty()) {
                if (isForEntitySpec(columnSpec, entitySpec)) {
                    String asCol = colStr.toString() + " AS " + columnSpec.getAsName();
                    cols.add(0, asCol);
                }
            }
            cols.add(colStr.toString());
        }
        result.append(StringUtils.join(cols, ','));

        return result.toString();
    }

    private static boolean isForEntitySpec(StagedColumnSpec columnSpec, EntitySpec entitySpec) {
        for (String esName : columnSpec.getForEntitySpecs()) {
            if (esName.equals(entitySpec.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCaseClause(Object[] sqlCodes, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        throw new UnsupportedOperationException();
    }

}
