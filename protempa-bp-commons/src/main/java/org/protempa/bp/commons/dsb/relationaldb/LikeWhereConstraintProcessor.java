package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.Constraint;

final class LikeWhereConstraintProcessor extends WhereConstraintProcessor {

    LikeWhereConstraintProcessor(ColumnSpec columnSpec, Constraint constraint,
            WhereClause whereClause, Object[] sqlCodes,
            TableAliaser referenceIndices) {
        super(columnSpec, constraint, whereClause, sqlCodes, referenceIndices);
    }

    @Override
    protected String processConstraint() {
        StringBuilder result = new StringBuilder();

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
