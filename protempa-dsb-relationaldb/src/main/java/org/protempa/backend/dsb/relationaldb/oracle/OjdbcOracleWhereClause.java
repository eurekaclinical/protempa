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
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.relationaldb.AbstractWhereClause;
import org.protempa.backend.dsb.relationaldb.ColumnSpec;
import org.protempa.backend.dsb.relationaldb.ColumnSpecInfo;
import org.protempa.backend.dsb.relationaldb.DefaultOrderByClause;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.InClause;
import org.protempa.backend.dsb.relationaldb.OrderByClause;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectClause;
import org.protempa.backend.dsb.relationaldb.TableAliaser;

final class Ojdbc6OracleWhereClause extends AbstractWhereClause {

    Ojdbc6OracleWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause) {
        super(propIds, info, entitySpecs, filters, referenceIndices, keyIds,
                order, resultProcessor, selectClause);
    }

    @Override
    public InClause getInClause(ColumnSpec columnSpec, Object[] elements,
            boolean not) {
        return new Ojdbc6OracleInClause(columnSpec, elements, not, getReferenceIndices());
    }

    @Override
    public OrderByClause getOrderByClause(ColumnSpec keyIdSpec) {
        return new DefaultOrderByClause(keyIdSpec,
                getOrder(), getReferenceIndices());
    }
}
