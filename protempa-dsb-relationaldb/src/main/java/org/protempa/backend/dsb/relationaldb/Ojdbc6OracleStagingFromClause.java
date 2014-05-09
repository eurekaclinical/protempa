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

import java.util.List;
import java.util.Map;

final class Ojdbc6OracleStagingFromClause extends Ojdbc6OracleFromClause {

    Ojdbc6OracleStagingFromClause(EntitySpec entitySpec, Map<String, ReferenceSpec> inboundReferenceSpecs, List<ColumnSpec> columnSpecs, TableAliaser referenceIndices, StagingSpec[] stagedTables) {
        super(entitySpec, inboundReferenceSpecs, columnSpecs, referenceIndices, stagedTables);
    }

    @Override
    protected String generateFromTable(ColumnSpec columnSpec) {
        StringBuilder fromPart = new StringBuilder();

        if (columnSpec.getSchema() != null) {
            fromPart.append(columnSpec.getSchema());
            fromPart.append('.');
        }

        fromPart.append(columnSpec.getTable());
        fromPart.append(" ");
        fromPart.append(getReferenceIndices().generateTableReference(columnSpec));

        return fromPart.toString();
    }
}
