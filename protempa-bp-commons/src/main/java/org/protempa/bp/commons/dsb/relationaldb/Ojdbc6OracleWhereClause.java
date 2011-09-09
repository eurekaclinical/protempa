package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleWhereClause extends WhereClause {

    Ojdbc6OracleWhereClause(ColumnSpecInfo info, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Map<ColumnSpec, Integer> referenceIndices,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor, SqlStatement stmt) {
        super(info, entitySpecs, filters, referenceIndices, keyIds, order,
                resultProcessor, stmt);
    }
}
