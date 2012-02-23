package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class H2SelectStatement extends AbstractSelectStatement {

    H2SelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        super(entitySpec, referenceSpec, entitySpecs, filters, propIds, keyIds, order,
                resultProcessor, null);
    }

    @Override
    SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec) {
        return new DefaultSelectClause(info, referenceIndices, entitySpec);
    }

    @Override
    FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices, StagingSpec[] stagedTables) {
        return new DefaultFromClause(getEntitySpec(), columnSpecs, referenceIndices);
    }

    @Override
    WhereClause getWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause, StagingSpec[] stagedTables) {
        return new DefaultWhereClause(propIds, info, entitySpecs, filters,
                referenceIndices, keyIds, order, resultProcessor, selectClause);
    }

}
