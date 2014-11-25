/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.dsb.relationaldb;

import org.protempa.backend.dsb.relationaldb.Operator;

abstract class WhereConstraintProcessor {

    private final ColumnSpec columnSpec;
    private final Operator constraint;
    private final WhereClause whereClause;
    private final Object[] sqlCodes;
    private final TableAliaser referenceIndices;

    protected WhereConstraintProcessor(ColumnSpec columnSpec,
            Operator constraint, WhereClause whereClause, Object[] sqlCodes,
            TableAliaser referenceIndices) {
        this.columnSpec = columnSpec;
        this.constraint = constraint;
        this.whereClause = whereClause;
        this.sqlCodes = sqlCodes;
        this.referenceIndices = referenceIndices;
    }

    static WhereConstraintProcessor getInstance(ColumnSpec columnSpec,
            Operator constraint, WhereClause whereClause, Object[] sqlCodes,
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
                        whereClause, sqlCodes, referenceIndices);
            default:
                throw new AssertionError("Invalid constraint: " + constraint);
        }
    }

    protected ColumnSpec getColumnSpec() {
        return columnSpec;
    }

    protected Operator getConstraint() {
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
