package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.Constraint;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

abstract class WhereConstraintProcessor {

    private final ColumnSpec columnSpec;
    private final Constraint constraint;
    private final WhereClause whereClause;
    private final Object[] sqlCodes;
    private final TableAliaser referenceIndices;

    protected WhereConstraintProcessor(ColumnSpec columnSpec,
            Constraint constraint, WhereClause whereClause, Object[] sqlCodes,
            TableAliaser referenceIndices) {
        this.columnSpec = columnSpec;
        this.constraint = constraint;
        this.whereClause = whereClause;
        this.sqlCodes = sqlCodes;
        this.referenceIndices = referenceIndices;
    }

    static WhereConstraintProcessor getInstance(ColumnSpec columnSpec,
            Constraint constraint, WhereClause whereClause, Object[] sqlCodes,
            SelectClause selectClause,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues,
            TableAliaser referenceIndices) {
        switch (constraint) {
            case EQUAL_TO:
                return new EqualToWhereConstraintProcessor(columnSpec,
                        constraint, whereClause, sqlCodes, referenceIndices);
            case NOT_EQUAL_TO:
                return new NotEqualToWhereConstraintProcessor(columnSpec,
                        constraint, whereClause, sqlCodes, referenceIndices);
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL_TO:
                return new InequalityWhereConstraintProcessor(columnSpec,
                        constraint, whereClause, sqlCodes, referenceIndices);
            case LIKE:
                return new LikeWhereConstraintProcessor(columnSpec, constraint,
                        whereClause, sqlCodes, selectClause,
                        filteredConstraintValues, referenceIndices);
            default:
                throw new AssertionError("Invalid constraint: " + constraint);
        }
    }

    protected ColumnSpec getColumnSpec() {
        return columnSpec;
    }

    protected Constraint getConstraint() {
        return constraint;
    }

    protected WhereClause getWhereClause() {
        return whereClause;
    }

    protected Object[] getSqlCodes() {
        return sqlCodes;
    }

    protected TableAliaser getReferenceIndices() {
        return referenceIndices;
    }

    protected abstract String processConstraint();
}
