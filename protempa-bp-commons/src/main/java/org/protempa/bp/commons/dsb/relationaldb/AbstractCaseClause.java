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

import static org.protempa.bp.commons.dsb.relationaldb.SqlGeneratorUtil.prepareValue;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

abstract class AbstractCaseClause implements CaseClause {

    private final Object[] sqlCodes;
    private final TableAliaser referenceIndices;
    private final ColumnSpec columnSpec;
    private final KnowledgeSourceIdToSqlCode[] filteredConstraintValues;

    AbstractCaseClause(Object[] sqlCodes, TableAliaser referenceIndices,
            ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        this.sqlCodes = sqlCodes;
        this.referenceIndices = referenceIndices;
        this.columnSpec = columnSpec;
        this.filteredConstraintValues = filteredConstraintValues;
    }

    @Override
    public String generateClause() {
        StringBuilder selectPart = new StringBuilder();

        selectPart.append(", case ");
        for (int k = 0; k < sqlCodes.length; k++) {
            selectPart.append("when ");
            selectPart.append(referenceIndices
                    .generateColumnReferenceWithOp(columnSpec));
            selectPart.append(" like ");
            selectPart.append(prepareValue(sqlCodes[k]));
            selectPart.append(" then ");
            selectPart.append(prepareValue(filteredConstraintValues[k]
                    .getPropositionId()));
            if (k < sqlCodes.length - 1) {
                selectPart.append(" ");
            }
        }
        selectPart.append(" end ");

        return selectPart.toString();
    }

}
