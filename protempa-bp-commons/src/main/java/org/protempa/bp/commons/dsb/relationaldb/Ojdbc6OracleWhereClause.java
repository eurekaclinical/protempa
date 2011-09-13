package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

final class Ojdbc6OracleWhereClause extends AbstractWhereClause {

    private final AbstractSqlStatement stmt;

    Ojdbc6OracleWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Map<ColumnSpec, Integer> referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            AbstractSqlStatement stmt) {
        super(propIds, info, entitySpecs, filters, referenceIndices, keyIds,
                order, resultProcessor, stmt);
        this.stmt = stmt;
    }

    @Override
    public InClause getInClause(int tableNumber, String columnName,
            Object[] elements, boolean not) {
        return new DefaultInClause(tableNumber, columnName, elements, not,
                this.stmt);
    }

    @Override
    public CaseClause getCaseClause(Object[] sqlCodes,
            Map<ColumnSpec, Integer> referenceIndices, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        return new DefaultCaseClause(sqlCodes, referenceIndices, columnSpec,
                filteredConstraintValues, this.stmt);
    }

    @Override
    public OrderByClause getOrderByClause(int startReferenceIndex,
            String startColumn, int finishReferenceIndex, String finishColumn, SQLOrderBy order) {
        return new DefaultOrderByClause(startReferenceIndex, startColumn,
                finishReferenceIndex, finishColumn, order, this.stmt);
    }
}
