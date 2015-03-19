/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.dsb.relationaldb;

import org.protempa.backend.dsb.filter.Filter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class for creating staging areas
 */
public abstract class AbstractStagingCreateStatement implements CreateStatement {

    private final StagingSpec stagingSpec;

    // Fields required for select statement
    private final EntitySpec currentSpec;
    private final ReferenceSpec referenceSpec;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Set<String> propIds;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;
    private final boolean streamingMode;

    protected AbstractStagingCreateStatement(StagingSpec stagingSpec,
            EntitySpec currentSpec, ReferenceSpec referenceSpec,
            List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor, boolean streamingMode) {
        this.stagingSpec = stagingSpec;
        this.currentSpec = currentSpec;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = entitySpecs;
        this.filters = filters;
        this.propIds = propIds;
        this.keyIds = keyIds;
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.streamingMode = streamingMode;
    }

    protected StagingSpec getStagingSpec() {
        return stagingSpec;
    }

    protected EntitySpec getCurrentSpec() {
        return currentSpec;
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
                + getSelectStatement(currentSpec, referenceSpec,
                        Collections.singletonList(currentSpec),
                        filters, propIds, keyIds, order, resultProcessor, streamingMode)
                        .generateStatement();
    }

    @Override
    public abstract SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            boolean streamingMode);

}
