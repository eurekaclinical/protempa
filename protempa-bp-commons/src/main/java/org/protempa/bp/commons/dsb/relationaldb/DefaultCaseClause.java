package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Map;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

class DefaultCaseClause extends AbstractCaseClause {

    public DefaultCaseClause(Object[] sqlCodes,
            Map<ColumnSpec, Integer> referenceIndices, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues,
            AbstractSqlStatement stmt) {
        super(sqlCodes, referenceIndices, columnSpec, filteredConstraintValues,
                stmt);
    }

}
