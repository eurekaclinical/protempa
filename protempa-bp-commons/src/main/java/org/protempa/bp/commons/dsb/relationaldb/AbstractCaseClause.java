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
