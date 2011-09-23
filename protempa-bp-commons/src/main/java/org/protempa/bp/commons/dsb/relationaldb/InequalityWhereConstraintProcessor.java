package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.Constraint;

class InequalityWhereConstraintProcessor extends WhereConstraintProcessor {

    InequalityWhereConstraintProcessor(ColumnSpec columnSpec,
            Constraint constraint, WhereClause whereClause, Object[] sqlCodes,
            TableAliaser referenceIndices) {
        super(columnSpec, constraint, whereClause, sqlCodes, referenceIndices);
    }

    @Override
    protected String processConstraint() {
        StringBuilder result = new StringBuilder();

        result.append(getReferenceIndices().generateColumnReferenceWithOp(
                getColumnSpec()));
        result.append(getConstraint().getSqlOperator());
        result.append(SqlGeneratorUtil.prepareValue(getSqlCodes()[0]));

        return result.toString();
    }

}
