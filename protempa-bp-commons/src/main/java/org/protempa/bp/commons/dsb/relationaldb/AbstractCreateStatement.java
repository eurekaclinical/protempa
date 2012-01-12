package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

/**
 * Class for creating staging areas
 */
abstract class AbstractCreateStatement implements CreateStatement {

    private final SimpleStagingSpec stagingSpec;

    // Fields required for select statement
    private final ReferenceSpec referenceSpec;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Set<String> propIds;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;

    protected AbstractCreateStatement(SimpleStagingSpec stagingSpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor) {
        this.stagingSpec = stagingSpec;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = entitySpecs;
        this.filters = filters;
        this.propIds = propIds;
        this.keyIds = keyIds;
        this.order = order;
        this.resultProcessor = resultProcessor;
    }

    protected SimpleStagingSpec getStagingSpec() {
        return stagingSpec;
    }

    protected ReferenceSpec getReferenceSpec() {
        return referenceSpec;
    }

    protected List<EntitySpec> getEntitySpecs() {
        return entitySpecs;
    }

    protected Set<Filter> getFilters() {
        return filters;
    }

    protected Set<String> getPropIds() {
        return propIds;
    }

    protected Set<String> getKeyIds() {
        return keyIds;
    }

    protected SQLOrderBy getOrder() {
        return order;
    }

    protected SQLGenResultProcessor getResultProcessor() {
        return resultProcessor;
    }

    @Override
    public String generateStatement() {
        return "CREATE TABLE "
                + stagingSpec.getStagingArea().getSchema()
                + "."
                + stagingSpec.getStagingArea().getTable()
                + " AS "
                + getSelectStatement(stagingSpec.getEntitySpec(),
                        referenceSpec, entitySpecs, filters, propIds, keyIds,
                        order, resultProcessor).generateStatement();
    }

    @Override
    public abstract SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor);

}
