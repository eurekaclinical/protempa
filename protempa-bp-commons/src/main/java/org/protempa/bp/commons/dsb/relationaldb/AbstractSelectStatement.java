package org.protempa.bp.commons.dsb.relationaldb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

abstract class AbstractSelectStatement implements SelectStatement {

    private final EntitySpec entitySpec;
    private final ReferenceSpec referenceSpec;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Set<String> propIds;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;
    private final StagingSpec[] stagedTables;

    protected AbstractSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, StagingSpec[] stagedTables) {
        this.entitySpec = entitySpec;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = Collections.unmodifiableList(entitySpecs);
        this.filters = Collections.unmodifiableSet(filters);
        this.propIds = Collections.unmodifiableSet(propIds);
        this.keyIds = Collections.unmodifiableSet(keyIds);
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.stagedTables = stagedTables;
    }

    abstract SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec);

    abstract FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices, StagingSpec[] stagedTables);

    abstract WhereClause getWhereClause(Set<String> propIds,
            ColumnSpecInfo info, List<EntitySpec> entitySpecs,
            Set<Filter> filters, TableAliaser referenceIndices,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor, SelectClause selectClause);

    @Override
    public String generateStatement() {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(propIds,
                entitySpec, entitySpecs, filters, referenceSpec);
        TableAliaser referenceIndices = new TableAliaser(info.getColumnSpecs(), "a");
        
        SelectClause select = getSelectClause(info, referenceIndices,
                this.entitySpec);
        FromClause from = getFromClause(info.getColumnSpecs(), referenceIndices, this.stagedTables);
        WhereClause where = getWhereClause(propIds, info, this.entitySpecs,
                this.filters, referenceIndices, this.keyIds, this.order,
                this.resultProcessor, select);

        StringBuilder result = new StringBuilder(select.generateClause())
                .append(" ").append(from.generateClause()).append(" ")
                .append(where.generateClause());

        return result.toString();
    }
}
