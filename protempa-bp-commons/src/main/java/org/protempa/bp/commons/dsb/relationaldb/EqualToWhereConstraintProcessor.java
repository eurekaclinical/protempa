/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.Constraint;

final class EqualToWhereConstraintProcessor extends WhereConstraintProcessor {

    EqualToWhereConstraintProcessor(ColumnSpec columnSpec,
            Constraint constraint, WhereClause whereClause, Object[] sqlCodes,
            TableAliaser referenceIndices) {
        super(columnSpec, constraint, whereClause, sqlCodes, referenceIndices);
    }

    @Override
    protected String processConstraint() {
        StringBuilder result = new StringBuilder();

        if (getSqlCodes().length > 1) {
            result.append(getWhereClause().getInClause(getColumnSpec(),
                    getSqlCodes(), false).generateClause());
        } else {
            assert getSqlCodes().length == 1 : "invalid sqlCodes length";
            result.append(getReferenceIndices()
                    .generateColumnReferenceWithOp(getColumnSpec()));
            result.append(getConstraint().getSqlOperator());
            result.append(SqlGeneratorUtil.prepareValue(getSqlCodes()[0]));
        }

        return result.toString();
    }

}