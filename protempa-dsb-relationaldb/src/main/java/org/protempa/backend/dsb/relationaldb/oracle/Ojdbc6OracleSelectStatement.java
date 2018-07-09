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

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.dsb.relationaldb.AbstractSelectStatement;
import org.protempa.backend.dsb.relationaldb.ColumnSpec;
import org.protempa.backend.dsb.relationaldb.ColumnSpecInfo;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.FromClause;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectClause;
import org.protempa.backend.dsb.relationaldb.TableAliaser;
import org.protempa.backend.dsb.relationaldb.WhereClause;

class Ojdbc6OracleSelectStatement extends AbstractSelectStatement {

    Ojdbc6OracleSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Map<String, ReferenceSpec> inboundRefSpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, 
            boolean streamingMode,
            boolean wrapKeyId) {
        super(entitySpec, referenceSpec, entitySpecs, inboundRefSpecs, filters, propIds, keyIds,
                order, resultProcessor, streamingMode, wrapKeyId);
    }

    @Override
    protected SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec,
            boolean wrapKeyId) {
        return new Ojdbc6SelectClause(info, referenceIndices, entitySpec,
                wrapKeyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getFromClause
     * (java.util.List, java.util.Map)
     */
    @Override
    protected FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices) {
        return new Ojdbc6OracleFromClause(getEntitySpec(), columnSpecs, referenceIndices);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.bp.commons.dsb.relationaldb.SelectStatement#getWhereClause
     * (org.protempa.bp.commons.dsb.relationaldb.ColumnSpecInfo, java.util.List,
     * java.util.Set, java.util.Map, java.util.Set,
     * org.protempa.bp.commons.dsb.relationaldb.SQLOrderBy,
     * org.protempa.bp.commons.dsb.relationaldb.SQLGenResultProcessor)
     */
    @Override
    protected WhereClause getWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, SelectClause selectClause) {
        return new Ojdbc6OracleWhereClause(propIds, info, entitySpecs, filters,
                referenceIndices, keyIds, order, resultProcessor, selectClause);
    }

}
