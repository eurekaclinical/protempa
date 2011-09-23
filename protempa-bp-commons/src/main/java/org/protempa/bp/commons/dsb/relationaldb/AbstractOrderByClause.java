package org.protempa.bp.commons.dsb.relationaldb;

abstract class AbstractOrderByClause implements OrderByClause {

    private final ColumnSpec startColumnSpec;
    private final ColumnSpec finishColumnSpec;
    private final SQLOrderBy order;
    private final TableAliaser referenceIndices;

    AbstractOrderByClause(ColumnSpec startColumnSpec,
            ColumnSpec finishColumnSpec, SQLOrderBy order,
            TableAliaser referenceIndices) {
        this.startColumnSpec = startColumnSpec;
        this.finishColumnSpec = finishColumnSpec;
        this.order = order;
        this.referenceIndices = referenceIndices;
    }

    @Override
    public String generateClause() {
        StringBuilder clause = new StringBuilder(" order by ");
        // clause.append(SqlGeneratorUtil.generateColumnReference(stmt,
        // startReferenceIndex,
        // startColumn));
        clause.append(referenceIndices.generateColumnReference(startColumnSpec));
        if (referenceIndices.getIndex(finishColumnSpec) > 0) {
            clause.append(',');
            // clause.append(SqlGeneratorUtil.generateColumnReference(stmt,
            // finishReferenceIndex, finishColumn));
            clause.append(referenceIndices
                    .generateColumnReference(finishColumnSpec));
        }
        clause.append(' ');
        if (order == SQLOrderBy.ASCENDING) {
            clause.append("ASC");
        } else {
            clause.append("DESC");
        }

        return clause.toString();
    }

}
