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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.backend.dsb.filter.Filter;

/**
 * Generates SQL compatible with the H2 database engine 1.x
 * (http://www.h2database.com).
 * 
 * @author Michel Mansour
 */
public final class H2SQLGenerator extends AbstractSQLGenerator {

    @Override
    public boolean checkCompatibility(Connection connection)
            throws SQLException {
        if (!checkDriverCompatibilitiy(connection)) {
            return false;
        }
        if (!checkDatabaseCompatibility(connection)) {
            return false;
        }

        return true;
    }

    private boolean checkDriverCompatibilitiy(Connection connection)
            throws SQLException {
        Logger logger = SQLGenUtil.logger();
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        logger.log(Level.FINER, "Database driver name: {0}", name);
        if (!name.equals("H2 JDBC Driver")) {
            return false;
        }
        int databaseMajorVersion = metaData.getDatabaseMajorVersion();
        logger.log(Level.FINER, "Database major version: {0}", databaseMajorVersion);
        if (databaseMajorVersion != 1) {
            return false;
        }

        return true;
    }

    private boolean checkDatabaseCompatibility(Connection connection)
            throws SQLException {
        Logger logger = SQLGenUtil.logger();
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName();
        logger.log(Level.FINER, "Database product name: {0}", databaseProductName);
        if (!databaseProductName.equalsIgnoreCase("H2")) {
            return false;
        }
        int databaseMajorVersion = metaData.getDatabaseMajorVersion();
        logger.log(Level.FINER, "Database major version: {0}", databaseMajorVersion);
        if (databaseMajorVersion != 1) {
            return false;
        }

        return true;
    }

    @Override
    protected String getDriverClassNameToLoad() {
        return "org.h2.Driver";
    }

    @Override
    protected SelectStatement getSelectStatement(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            StagingSpec[] stagedTables, boolean wrapKeyId) {
        return new H2SelectStatement(entitySpec, referenceSpec, entitySpecs,
                filters, propIds, keyIds, order, resultProcessor,
                getStreamingMode(), wrapKeyId);
    }
}
