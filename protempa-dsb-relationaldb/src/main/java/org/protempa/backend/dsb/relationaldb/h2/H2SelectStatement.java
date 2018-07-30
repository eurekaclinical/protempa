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
package org.protempa.backend.dsb.relationaldb.h2;

import org.protempa.backend.dsb.filter.Filter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.dsb.relationaldb.AbstractSelectStatement;
import org.protempa.backend.dsb.relationaldb.ColumnSpec;
import org.protempa.backend.dsb.relationaldb.ColumnSpecInfo;
import org.protempa.backend.dsb.relationaldb.DefaultFromClause;
import org.protempa.backend.dsb.relationaldb.DefaultWhereClause;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.FromClause;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectClause;
import org.protempa.backend.dsb.relationaldb.TableAliaser;
import org.protempa.backend.dsb.relationaldb.WhereClause;

final class H2SelectStatement extends AbstractSelectStatement {

    H2SelectStatement(EntitySpec entitySpec, List<EntitySpec> entitySpecs,
            Map<String, ReferenceSpec> inboundRefSpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            boolean wrapKeyId) {
        super(entitySpec, entitySpecs, inboundRefSpecs, filters, propIds, keyIds,
                order, resultProcessor, wrapKeyId);
    }

    @Override
    protected SelectClause getSelectClause(ColumnSpecInfo info,
            TableAliaser referenceIndices, EntitySpec entitySpec,
            boolean wrapKeyId) {
        return new H2SelectClause(info, referenceIndices, entitySpec,
                wrapKeyId);
    }

    @Override
    protected FromClause getFromClause(List<ColumnSpec> columnSpecs,
            TableAliaser referenceIndices) {
        return new DefaultFromClause(getEntitySpec(), columnSpecs, referenceIndices);
    }

    @Override
    protected WhereClause getWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause) {
        return new DefaultWhereClause(propIds, info, entitySpecs, filters,
                referenceIndices, keyIds, order, resultProcessor, selectClause);
    }

}
