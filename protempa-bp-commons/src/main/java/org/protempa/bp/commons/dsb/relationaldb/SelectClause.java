package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

interface SelectClause extends SqlClause {
    void setCaseClause(Object[] sqlCodes, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues);
}