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
package org.protempa.backend.dsb.relationaldb.oracle;

import org.protempa.backend.dsb.filter.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.protempa.backend.dsb.relationaldb.ColumnSpec;
import org.protempa.backend.dsb.relationaldb.ColumnSpecInfo;
import org.protempa.backend.dsb.relationaldb.ColumnSpecInfoFactory;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.FromClause;
import org.protempa.backend.dsb.relationaldb.IntColumnSpecWrapper;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectClause;
import org.protempa.backend.dsb.relationaldb.StagedColumnSpec;
import org.protempa.backend.dsb.relationaldb.StagingSelectClause;
import org.protempa.backend.dsb.relationaldb.StagingSpec;
import org.protempa.backend.dsb.relationaldb.TableAliaser;
import org.protempa.backend.dsb.relationaldb.WhereClause;

final class Ojdbc6OracleStagingSelectStatement extends
        Ojdbc6OracleSelectStatement {

    private final StagingSpec stagingSpec;
    private final boolean streamingMode;
    private final boolean wrapKeyId;

    // we are staging a table, so we don't want anything skipped based on what
    // tables are staged
    private static final StagingSpec[] EMPTY_SSPEC_ARR = new StagingSpec[0];

    Ojdbc6OracleStagingSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec stagingSpec, boolean streamingMode, 
            boolean wrapKeyId) {
        super(entitySpec, referenceSpec, entitySpecs, new TreeMap<String, ReferenceSpec>(), filters, propIds, keyIds,
                order, resultProcessor, EMPTY_SSPEC_ARR, streamingMode,
                wrapKeyId);
        this.stagingSpec = stagingSpec;
        this.streamingMode = streamingMode;
        this.wrapKeyId = wrapKeyId;
    }

    @Override
    public String generateStatement() {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(
                getPropIds(), getEntitySpec(), getEntitySpecs(), getInboundReferenceSpecs(),
                getFilters(), getReferenceSpec(), streamingMode);

        List<IntColumnSpecWrapper> plusStagedSpecs = new ArrayList<>(
                info.getColumnSpecs());
        for (StagedColumnSpec spec : stagingSpec.getStagedColumns()) {
            IntColumnSpecWrapper icsw = new IntColumnSpecWrapper(spec.toColumnSpec());
            icsw.setIsSameAs(getEntitySpec().getBaseSpec().getLastSpec());
            plusStagedSpecs.add(icsw);
        }
        TableAliaser referenceIndices = new TableAliaser(plusStagedSpecs, "a");

        SelectClause select = getSelectClause(info, referenceIndices,
                getEntitySpec(), this.wrapKeyId);
        FromClause from = getFromClause(toColumnSpecs(info.getColumnSpecs()),
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
    protected SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec,
            boolean wrapKeyId) {
        return new StagingSelectClause(stagingSpec, entitySpec, referenceIndices);
    }

    @Override
    protected FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices, StagingSpec[] stagedTables) {
        return new Ojdbc6OracleStagingFromClause(getEntitySpec(), 
                getInboundReferenceSpecs(), columnSpecs,
                referenceIndices, stagedTables);
    }

    @Override
    protected WhereClause getWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause, StagingSpec[] stagedTables) {
        return super.getWhereClause(propIds, info, entitySpecs, filters,
                referenceIndices, keyIds, order, resultProcessor, selectClause,
                stagedTables);
    }

}
