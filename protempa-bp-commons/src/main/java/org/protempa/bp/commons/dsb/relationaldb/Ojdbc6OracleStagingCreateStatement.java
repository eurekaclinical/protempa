package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleStagingCreateStatement extends
        AbstractStagingCreateStatement {

    public Ojdbc6OracleStagingCreateStatement(StagingSpec stagingSpec, EntitySpec currentSpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        super(stagingSpec, currentSpec, referenceSpec, entitySpecs, filters, propIds,
                keyIds, order, resultProcessor);
    }

    @Override
    public SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        return new Ojdbc6OracleStagingSelectStatement(entitySpec,
                referenceSpec, entitySpecs, filters, propIds, keyIds, order,
                resultProcessor, getStagingSpec());
    }

}
