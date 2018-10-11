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

import java.util.List;
import java.util.Map;
import org.protempa.backend.dsb.relationaldb.AbstractFromClause;
import org.protempa.backend.dsb.relationaldb.AbstractJoinClause;
import org.protempa.backend.dsb.relationaldb.AbstractOnClause;
import org.protempa.backend.dsb.relationaldb.ColumnSpec;
import org.protempa.backend.dsb.relationaldb.DefaultJoinClause;
import org.protempa.backend.dsb.relationaldb.DefaultOnClause;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.JoinSpec;
import org.protempa.backend.dsb.relationaldb.JoinSpec.JoinType;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.TableAliaser;

class Ojdbc6OracleFromClause extends AbstractFromClause {


    Ojdbc6OracleFromClause(EntitySpec currentSpec,
            List<ColumnSpec> columnSpecs, TableAliaser referenceIndices) {
        super(currentSpec, columnSpecs, referenceIndices);
    }

    @Override
    protected String generateFromTable(ColumnSpec columnSpec) {
        StringBuilder fromPart = new StringBuilder();
        String schemaToAppend = null;
        String tableToAppend = "";

        if (columnSpec.getSchema() != null) {
            schemaToAppend = columnSpec.getSchema();
        }
        tableToAppend = columnSpec.getTable();

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
