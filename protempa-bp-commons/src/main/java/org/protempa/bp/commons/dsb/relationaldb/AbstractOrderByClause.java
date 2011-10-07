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
        StringBuilder result = new StringBuilder(" order by ");
        result.append(referenceIndices.generateColumnReference(startColumnSpec));
        if (referenceIndices.getIndex(finishColumnSpec) > 0) {
            result.append(',');
            result.append(referenceIndices
                    .generateColumnReference(finishColumnSpec));
        }
        result.append(' ');
        if (order == SQLOrderBy.ASCENDING) {
            result.append("ASC");
        } else {
            result.append("DESC");
        }

        return result.toString();
    }

}
