package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Map;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

abstract class AbstractCaseClause implements CaseClause {

    private final Object[] sqlCodes;
    private final Map<ColumnSpec, Integer> referenceIndices;
    private final ColumnSpec columnSpec;
    private final KnowledgeSourceIdToSqlCode[] filteredConstraintValues;
    private final SqlStatement stmt;
    
    AbstractCaseClause(Object[] sqlCodes, Map<ColumnSpec, Integer> referenceIndices,
            ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues,
            SqlStatement stmt) {
        this.sqlCodes = sqlCodes;
        this.referenceIndices = referenceIndices;
        this.columnSpec = columnSpec;
        this.filteredConstraintValues = filteredConstraintValues;
        this.stmt = stmt;
    }

    @Override
    public String generateClause() {
        StringBuilder selectPart = new StringBuilder();
        
        selectPart.append(", case ");
        for (int k = 0; k < sqlCodes.length; k++) {
            selectPart.append("when ");
            selectPart.append(SqlGeneratorUtil.appendColumnRef(stmt, referenceIndices, columnSpec));
            selectPart.append(" like ");
            selectPart.append(SqlGeneratorUtil.appendValue(sqlCodes[k]));
            selectPart.append(" then ");
            selectPart.append(SqlGeneratorUtil.appendValue(filteredConstraintValues[k].getPropositionId()));
            if (k < sqlCodes.length - 1) {
                selectPart.append(" ");
            }
        }
        selectPart.append(" end ");
        
        return selectPart.toString();
    }

}
