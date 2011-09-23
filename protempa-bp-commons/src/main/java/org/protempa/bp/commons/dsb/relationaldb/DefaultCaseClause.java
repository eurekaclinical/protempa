package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

class DefaultCaseClause extends AbstractCaseClause {

    public DefaultCaseClause(Object[] sqlCodes,
            TableAliaser referenceIndices, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        super(sqlCodes, referenceIndices, columnSpec, filteredConstraintValues);
    }

}
