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
    private final boolean streamingMode;
    private final boolean wrapKeyId;

    protected AbstractSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec[] stagedTables, boolean streamingMode,
            boolean wrapKeyId) {
        this.entitySpec = entitySpec;
        this.referenceSpec = referenceSpec;
        this.entitySpecs = Collections.unmodifiableList(entitySpecs);
        this.filters = Collections.unmodifiableSet(filters);
        this.propIds = Collections.unmodifiableSet(propIds);
        this.keyIds = Collections.unmodifiableSet(keyIds);
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.stagedTables = stagedTables;
        this.streamingMode = streamingMode;
        this.wrapKeyId = wrapKeyId;
    }

    protected EntitySpec getEntitySpec() {
        return entitySpec;
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

    protected StagingSpec[] getStagedTables() {
        return stagedTables;
    }

    abstract SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec, boolean wrapKeyId);

    abstract FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices, StagingSpec[] stagedTables);

    abstract WhereClause getWhereClause(Set<String> propIds,
            ColumnSpecInfo info, List<EntitySpec> entitySpecs,
            Set<Filter> filters, TableAliaser referenceIndices,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor, SelectClause selectClause,
            StagingSpec[] stagedTables);

    @Override
    public String generateStatement() {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(propIds,
                entitySpec, entitySpecs, filters, referenceSpec,
                this.streamingMode);
        TableAliaser referenceIndices = new TableAliaser(info.getColumnSpecs(),
                "a");

        SelectClause select = getSelectClause(info, referenceIndices,
                this.entitySpec, wrapKeyId);
        FromClause from = getFromClause(info.getColumnSpecs(),
                referenceIndices, this.stagedTables);
        WhereClause where = getWhereClause(propIds, info, this.entitySpecs,
                this.filters, referenceIndices, this.keyIds, this.order,
                this.resultProcessor, select, this.stagedTables);

        StringBuilder result = new StringBuilder(select.generateClause())
                .append(" ").append(from.generateClause()).append(" ")
                .append(where.generateClause());

        return result.toString();
    }
}
