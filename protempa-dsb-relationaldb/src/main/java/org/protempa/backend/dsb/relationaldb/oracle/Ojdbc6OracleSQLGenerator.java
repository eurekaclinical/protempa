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

import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.backend.dsb.filter.Filter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.arp.javautil.version.MajorMinorVersion;
import org.protempa.backend.dsb.relationaldb.AbstractSQLGeneratorWithCompatChecks;
import org.protempa.backend.dsb.relationaldb.DataStager;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectStatement;
import org.protempa.backend.dsb.relationaldb.StagingSpec;

/**
 * Generates SQL compatible with Oracle 10.x and 11.x 
 * 
 * @author Andrew Post
 */
public class Ojdbc6OracleSQLGenerator extends AbstractSQLGeneratorWithCompatChecks {
    
    private static final String DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver";
    private static final String DRIVER_NAME = "Oracle JDBC driver";
    private static final MajorMinorVersion MIN_DRIVER_VERSION = new MajorMinorVersion(11, 0);
    private static final MajorMinorVersion MAX_DRIVER_VERSION = new MajorMinorVersion(11, Integer.MAX_VALUE);
    private static final String DATABASE_PRODUCT_NAME = "Oracle";
    private static final MajorMinorVersion MIN_DATABASE_VERSION = new MajorMinorVersion(10, 0);
    private static final MajorMinorVersion MAX_DATABASE_VERSION = new MajorMinorVersion(11, Integer.MAX_VALUE);
    
    public Ojdbc6OracleSQLGenerator() {
        super(DRIVER_CLASS_NAME,
                DRIVER_NAME,
                MIN_DRIVER_VERSION, MAX_DRIVER_VERSION,
                DATABASE_PRODUCT_NAME,
                MIN_DATABASE_VERSION, MAX_DATABASE_VERSION);
    }

    @Override
    protected SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Map<String, ReferenceSpec> inboundRefSpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec[] stagedTables, boolean wrapKeyId) {
        return new Ojdbc6OracleSelectStatement(entitySpec, referenceSpec,
                entitySpecs, inboundRefSpecs, filters, propIds, keyIds, order, resultProcessor,
                stagedTables, getStreamingMode(), wrapKeyId);
    }

    @Override
    protected DataStager getDataStager(StagingSpec[] stagingSpecs,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, ConnectionSpec connectionSpec) {
        return new Ojdbc6OracleDataStager(stagingSpecs, referenceSpec,
                entitySpecs, filters, propIds, keyIds, order, connectionSpec,
                getStreamingMode());
    }
}
