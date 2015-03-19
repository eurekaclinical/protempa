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
import org.protempa.backend.dsb.relationaldb.AbstractStagingCreateStatement;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectStatement;
import org.protempa.backend.dsb.relationaldb.StagingSpec;

final class Ojdbc6OracleStagingCreateStatement extends
        AbstractStagingCreateStatement {

    public Ojdbc6OracleStagingCreateStatement(StagingSpec stagingSpec, EntitySpec currentSpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            boolean streamingMode) {
        super(stagingSpec, currentSpec, referenceSpec, entitySpecs, filters, propIds,
                keyIds, order, resultProcessor, streamingMode);
    }

    @Override
    public SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            boolean streamingMode) {
        return new Ojdbc6OracleStagingSelectStatement(entitySpec,
                referenceSpec, entitySpecs, filters, propIds, keyIds, order,
                resultProcessor, getStagingSpec(), streamingMode, false);
    }

}
