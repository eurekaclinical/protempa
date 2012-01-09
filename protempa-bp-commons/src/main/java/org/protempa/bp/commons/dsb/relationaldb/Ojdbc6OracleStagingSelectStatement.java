package org.protempa.bp.commons.dsb.relationaldb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleStagingSelectStatement extends
        Ojdbc6OracleSelectStatement {

    private final EntitySpec entitySpec;
    private final ReferenceSpec referenceSpec;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Set<String> propIds;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;
    private final StagingSpec[] stagedTables;
    private final StagingSpec stagingSpec;

    Ojdbc6OracleStagingSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec[] stagedTables, StagingSpec stagingSpec) {
        super(entitySpec, referenceSpec, entitySpecs, filters, propIds, keyIds,
                order, resultProcessor, stagedTables);
        this.entitySpec = entitySpec;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = entitySpecs;
        this.filters = filters;
        this.propIds = propIds;
        this.keyIds = keyIds;
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.stagedTables = stagedTables.clone();
        this.stagingSpec = stagingSpec;
    }

    @Override
    public String generateStatement() {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(propIds,
                entitySpec, entitySpecs, filters, referenceSpec);

        List<ColumnSpec> plusStagedSpecs = new ArrayList<ColumnSpec>(
                info.getColumnSpecs());
        for (ColumnSpec spec : stagingSpec.getStagedColumns()) {
            plusStagedSpecs.add(spec);
        }
        TableAliaser referenceIndices = new TableAliaser(plusStagedSpecs, "a");

        SelectClause select = getSelectClause(info, referenceIndices,
                this.entitySpec);
        FromClause from = getFromClause(info.getColumnSpecs(),
                referenceIndices, this.stagedTables);
        WhereClause where = getWhereClause(propIds, info, this.entitySpecs,
                this.filters, referenceIndices, this.keyIds, this.order,
                this.resultProcessor, select);

        StringBuilder result = new StringBuilder(select.generateClause())
                .append(" ").append(from.generateClause()).append(" ")
                .append(where.generateClause());

        return result.toString();
    }

    @Override
    SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec) {
        return new StagingSelectClause(stagingSpec, referenceIndices);
    }

    @Override
    FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices, StagingSpec[] stagedTables) {
        return new Ojdbc6OracleStagingFromClause(columnSpecs, referenceIndices,
                stagedTables);
    }
}
