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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.protempa.backend.dsb.relationaldb.JoinSpec.JoinType;

class Ojdbc6OracleFromClause extends AbstractFromClause {

    private final StagingSpec[] stagedTables;
    private final Map<String, ReferenceSpec> inboundReferenceSpecs;

    Ojdbc6OracleFromClause(EntitySpec currentSpec,
            Map<String, ReferenceSpec> inboundReferenceSpecs,
            List<ColumnSpec> columnSpecs, TableAliaser referenceIndices,
            StagingSpec[] stagedTables) {
        super(currentSpec, columnSpecs, referenceIndices);
        this.stagedTables = stagedTables;
        this.inboundReferenceSpecs = inboundReferenceSpecs;
    }

    protected StagingSpec[] getStagedTables() {
        return stagedTables;
    }
    
    private static List<String> toEntitySpecNames(EntitySpec[] entitySpecs) {
        List<String> result = new ArrayList<>(entitySpecs.length);
        for (EntitySpec entitySpec : entitySpecs) {
            result.add(entitySpec.getName());
        }
        return result;
    }

    @Override
    protected String generateFromTable(ColumnSpec columnSpec) {
        StringBuilder fromPart = new StringBuilder();
        boolean foundStagedTable = false;
        String schemaToAppend = null;
        String tableToAppend = "";
        
        if (this.stagedTables != null) {
            List<String> entitySpecNames = new ArrayList<>(this.inboundReferenceSpecs.keySet());
            entitySpecNames.add(getCurrentSpec().getName());
            for (StagingSpec sspec : this.stagedTables) {
                if (!foundStagedTable
                        && !Collections.disjoint(toEntitySpecNames(sspec.getEntitySpecs()), entitySpecNames)
                        && columnSpec.isSameSchemaAndTable(sspec
                                .getReplacedTable())) {
                    foundStagedTable = true;
                    if (sspec.getStagingArea().getSchema() != null) {
                        schemaToAppend = sspec.getStagingArea().getSchema();
                    }
                    tableToAppend = sspec.getStagingArea().getTable();
                }
            }
        }
        if (!foundStagedTable) {
            if (columnSpec.getSchema() != null) {
                schemaToAppend = columnSpec.getSchema();
            }
            tableToAppend = columnSpec.getTable();
        }

        if (schemaToAppend != null) {
            fromPart.append(schemaToAppend);
            fromPart.append('.');
        }
        fromPart.append(tableToAppend);
        fromPart.append(" ");
        fromPart.append(getReferenceIndices()
                .generateTableReference(columnSpec));

        return fromPart.toString();
    }

    @Override
    protected AbstractJoinClause getJoinClause(JoinType joinType) {
        return new DefaultJoinClause(joinType);
    }

    @Override
    protected AbstractOnClause getOnClause(JoinSpec joinSpec,
            TableAliaser referenceIndices) {
        return new DefaultOnClause(joinSpec, referenceIndices);
    }

}
