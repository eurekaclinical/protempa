package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class Ojdbc6OracleStagingCreateStatement extends
        AbstractCreateStatement {

    public Ojdbc6OracleStagingCreateStatement(SimpleStagingSpec stagingSpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        super(stagingSpec, referenceSpec, entitySpecs, filters, propIds,
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
