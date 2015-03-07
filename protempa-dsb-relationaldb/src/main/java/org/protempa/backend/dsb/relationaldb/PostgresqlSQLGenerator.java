package org.protempa.backend.dsb.relationaldb;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.dsb.filter.Filter;

/**
 * PostgreSQL driver for Protempa. Supports version 9.1 or greater because we
 * rely on COLLATE to ensure that key id sorting works correctly.
 * 
 * @author Andrew Post
 */
public class PostgresqlSQLGenerator 
        extends AbstractSQLGeneratorWithCompatChecks {
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String DRIVER_NAME = "PostgreSQL Native Driver";
    private static final int[] SUPPORTED_DRIVER_MAJOR_VERSIONS = {9};
    private static final String DATABASE_PRODUCT_NAME = "PostgreSQL";
    private static final int[] SUPPORTED_DATABASE_MAJOR_VERSIONS = {9};
    
    public PostgresqlSQLGenerator() {
        super(DRIVER_CLASS_NAME, 
                DRIVER_NAME, 
                SUPPORTED_DRIVER_MAJOR_VERSIONS, 
                DATABASE_PRODUCT_NAME, 
                SUPPORTED_DATABASE_MAJOR_VERSIONS);
    }

    @Override
    protected SelectStatement getSelectStatement(EntitySpec entitySpec, 
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs, 
            Map<String, ReferenceSpec> inboundRefSpecs, 
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds, 
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, 
            StagingSpec[] stagedTables, boolean wrapKeyId) {
        return new PostgresqlSelectStatement(entitySpec, referenceSpec, entitySpecs,
                inboundRefSpecs, filters, propIds, keyIds, order, resultProcessor,
                getStreamingMode(), wrapKeyId);
    }
    
}
