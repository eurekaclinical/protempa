package org.protempa.bp.commons.dsb.relationaldb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleStagingSelectStatement extends
        Ojdbc6OracleSelectStatement {

    private final SimpleStagingSpec stagingSpec;

    // we are staging a table, so we don't want anything skipped based on what
    // tables are staged
    private static final StagingSpec[] EMPTY_SSPEC_ARR = new StagingSpec[0];

    Ojdbc6OracleStagingSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SimpleStagingSpec stagingSpec) {
        super(entitySpec, referenceSpec, entitySpecs, filters, propIds, keyIds,
                order, resultProcessor, EMPTY_SSPEC_ARR);
        this.stagingSpec = stagingSpec;
    }

    @Override
    public String generateStatement() {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(
                getPropIds(), getEntitySpec(), getEntitySpecs(), getFilters(),
                getReferenceSpec());

        List<ColumnSpec> plusStagedSpecs = new ArrayList<ColumnSpec>(
                info.getColumnSpecs());
        for (SimpleColumnSpec spec : stagingSpec.getStagedColumns()) {
            plusStagedSpecs.add(spec.toColumnSpec());
        }
        TableAliaser referenceIndices = new TableAliaser(plusStagedSpecs, "a");

        SelectClause select = getSelectClause(info, referenceIndices,
                getEntitySpec());
        FromClause from = getFromClause(info.getColumnSpecs(),
                referenceIndices, EMPTY_SSPEC_ARR);
        WhereClause where = getWhereClause(getPropIds(), info,
                getEntitySpecs(), getFilters(), referenceIndices, getKeyIds(),
                getOrder(), getResultProcessor(), select, EMPTY_SSPEC_ARR);

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
        return new Ojdbc6OracleStagingFromClause(getEntitySpec(), columnSpecs,
                referenceIndices, stagedTables);
    }

    @Override
    WhereClause getWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause, StagingSpec[] stagedTables) {
        return super.getWhereClause(propIds, info, entitySpecs, filters,
                referenceIndices, keyIds, order, resultProcessor, selectClause,
                stagedTables);
    }

}
