/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
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
import java.util.Set;

import org.protempa.backend.dsb.filter.Filter;

final class DefaultWhereClause extends AbstractWhereClause {

    public DefaultWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause) {
        super(propIds, info, entitySpecs, filters, referenceIndices, keyIds,
                order, resultProcessor, selectClause, new StagingSpec[0]);
    }

    @Override
    public InClause getInClause(ColumnSpec columnSpec, Object[] elements,
            boolean not) {
        return new DefaultInClause(columnSpec, elements, not,
                getReferenceIndices());
    }

    @Override
    public OrderByClause getOrderByClause(ColumnSpec keyIdSpec) {
        return new DefaultOrderByClause(keyIdSpec,
                getOrder(), getReferenceIndices());
    }

}
