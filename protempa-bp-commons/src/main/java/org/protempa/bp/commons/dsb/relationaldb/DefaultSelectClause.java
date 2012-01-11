package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;


final class DefaultSelectClause extends AbstractSelectClause {

    DefaultSelectClause(ColumnSpecInfo info, TableAliaser referenceIndices,
            EntitySpec entitySpec) {
        super(info, referenceIndices, entitySpec);
    }

    @Override
    protected CaseClause getCaseClause(Object[] sqlCodes, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        return new DefaultCaseClause(sqlCodes, getReferenceIndices(), columnSpec,
                filteredConstraintValues);
    }

}
