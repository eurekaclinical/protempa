package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.Constraint;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

final class LikeWhereConstraintProcessor extends WhereConstraintProcessor {

    private final SelectClause selectClause;
    private final KnowledgeSourceIdToSqlCode[] filteredConstraintValues;

    LikeWhereConstraintProcessor(ColumnSpec columnSpec, Constraint constraint,
            WhereClause whereClause, Object[] sqlCodes,
            SelectClause selectClause,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues,
            TableAliaser referenceIndices) {
        super(columnSpec, constraint, whereClause, sqlCodes, referenceIndices);
        this.selectClause = selectClause;
        this.filteredConstraintValues = filteredConstraintValues;
    }

    @Override
    protected String processConstraint() {
        StringBuilder result = new StringBuilder();

        if (selectClause != null) {
            selectClause.setCaseClause(getSqlCodes(), getReferenceIndices(),
                    getColumnSpec(), filteredConstraintValues);
        }
        if (getSqlCodes().length > 1) {
            result.append('(');
        }
        for (int i = 0; i < getSqlCodes().length; i++) {
            result.append(getReferenceIndices().generateColumnReferenceWithOp(
                    getColumnSpec()));
            result.append(" LIKE ");
            result.append(SqlGeneratorUtil.prepareValue(getSqlCodes()[i]));
            if (i + 1 < getSqlCodes().length) {
                result.append(" OR ");
            }
        }
        if (getSqlCodes().length > 1) {
            result.append(')');
        }

        return result.toString();
    }
}
