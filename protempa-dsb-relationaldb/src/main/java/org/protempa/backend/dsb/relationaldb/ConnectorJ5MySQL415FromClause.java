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

import org.protempa.backend.dsb.relationaldb.JoinSpec.JoinType;


final class ConnectorJ5MySQL415FromClause extends AbstractFromClause {

    ConnectorJ5MySQL415FromClause(EntitySpec currentSpec, List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices) {
        super(currentSpec, columnSpecs, referenceIndices);
    }

    @Override
    protected JoinClause getJoinClause(JoinType joinType) {
        return new DefaultJoinClause(joinType);
    }

    @Override
    protected OnClause getOnClause(JoinSpec joinSpec,
            TableAliaser referenceIndices) {
        return new DefaultOnClause(joinSpec, referenceIndices);
    }

    @Override
    public String generateFromTable(ColumnSpec columnSpec) {
        StringBuilder fromPart = new StringBuilder();

        if (columnSpec.getSchema() != null && !columnSpec.getSchema().isEmpty()) {
            throw new IllegalArgumentException("schema is not supported");
        }

        fromPart.append(columnSpec.getTable());
        fromPart.append(" ");
        fromPart.append(getReferenceIndices().generateTableReference(columnSpec));
        
        return fromPart.toString();
    }
}
