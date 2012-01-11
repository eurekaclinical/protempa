package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class DefaultWhereClause extends AbstractWhereClause {

    public DefaultWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause) {
        super(propIds, info, entitySpecs, filters, referenceIndices, keyIds,
                order, resultProcessor, selectClause, new StagingSpec[0]);
    }

    @Override
    public InClause getInClause(ColumnSpec columnSpec, Object[] elements,
            boolean not) {
        return new DefaultInClause(columnSpec, elements, not,
                getReferenceIndices());
    }

    @Override
    public OrderByClause getOrderByClause(ColumnSpec startColumnSpec,
            ColumnSpec finishColumnSpec) {
        return new DefaultOrderByClause(startColumnSpec, finishColumnSpec,
                getOrder(), getReferenceIndices());
    }

}
