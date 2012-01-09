package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleStagingCreateStatement extends
        AbstractCreateStatement {

    private final StagingSpec stagingSpec;
    private final StagingSpec[] stagedTables;

    public Ojdbc6OracleStagingCreateStatement(StagingSpec stagingSpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec[] stagedTables) {
        super(stagingSpec, referenceSpec, entitySpecs, filters, propIds,
                keyIds, order, resultProcessor, stagedTables);
        this.stagingSpec = stagingSpec;
        this.stagedTables = stagedTables;
    }

    @Override
    public SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        return new Ojdbc6OracleStagingSelectStatement(entitySpec,
                referenceSpec, entitySpecs, filters, propIds, keyIds, order,
                resultProcessor, this.stagedTables, this.stagingSpec);
    }

}
