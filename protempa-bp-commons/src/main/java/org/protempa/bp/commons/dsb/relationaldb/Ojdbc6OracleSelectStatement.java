package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleSelectStatement extends SelectStatement {

    Ojdbc6OracleSelectStatement(EntitySpec entitySpec, ReferenceSpec referenceSpec,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        super(entitySpec, referenceSpec, entitySpecs, filters, propIds, keyIds,
                order, resultProcessor);
    }

    @Override
    protected SelectClause getSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec) {
        return new Ojdbc6OracleSelectClause(info, referenceIndices, entitySpec);
    }

}
