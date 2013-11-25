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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Abstract class for constructing a SQL generator with useful compatibility
 * checks between the driver and the actual database instance.
 * 
 * @author Andrew Post
 */
public abstract class AbstractSQLGeneratorWithCompatChecks extends AbstractSQLGenerator {
    private final String driverClassName;
    private final String driverName;
    private final int[] driverMajorVersions;
    private final String databaseProductName;
    private final int[] databaseMajorVersions;

    /**
     * Creates a SQL generator with the specified driver class name and 
     * metadata for compatibility checking.
     * 
     * @param driverClassName the fully qualified class name. If 
     * <code>null</code>, no attempt to load the class will be made. This may 
     * be okay for JDBC version 4 drivers, which theoretically don't need to be
     * explicitly loaded.
     * @param driverName the value of {@link DatabaseMetaData#getDriverName() } 
     * to check. Cannot be <code>null</code>.
     * @param driverMajorVersions values of 
     * {@link DatabaseMetaData#getDriverMajorVersion() } to check. Cannot be 
     * <code>null</code>.
     * @param databaseProductName the value of 
     * {@link DatabaseMetaData#getDatabaseProductName() } to check. Cannot be 
     * <code>null</code>.
     * @param databaseMajorVersions values of 
     * {@link DatabaseMetaData#getDatabaseMajorVersion() } to check. Cannot be 
     * <code>null</code>.
     */
    public AbstractSQLGeneratorWithCompatChecks(String driverClassName, 
            String driverName, int[] driverMajorVersions, 
            String databaseProductName, int[] databaseMajorVersions) {
        if (driverName == null) {
            throw new IllegalArgumentException("driverName cannot be null");
        }
        if (driverMajorVersions == null) {
            throw new IllegalArgumentException("driverMajorVersions cannot be null");
        }
        if (databaseProductName == null) {
            throw new IllegalArgumentException("databaseProductName cannot be null");
        }
        if (databaseMajorVersions == null) {
            throw new IllegalArgumentException("databaseMajorVersions cannot be null");
        }
        
        this.driverClassName = driverClassName;
        this.driverName = driverName;
        this.driverMajorVersions = driverMajorVersions.clone();
        this.databaseProductName = databaseProductName;
        this.databaseMajorVersions = databaseMajorVersions.clone();
    }
    
    @Override
    public final boolean checkCompatibility(Connection connection) throws SQLException {
        if (!checkDriverCompatibilitiy(connection)) {
            return false;
        }
        if (!checkDatabaseCompatibility(connection)) {
            return false;
        }

        return true;
    }
    
    @Override
    protected final String getDriverClassNameToLoad() {
        return this.driverClassName;
    }
    
    private boolean checkDriverCompatibilitiy(Connection connection)
            throws SQLException {
        Logger logger = SQLGenUtil.logger();
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDriverName();
        logger.log(Level.FINER, "Database driver name: {0}", name);
        if (!name.equals(this.driverName)) {
            return false;
        }
        int driverMajorVersion = metaData.getDriverMajorVersion();
        logger.log(Level.FINER, "Driver version: {0}", driverMajorVersion);
        if (!ArrayUtils.contains(this.driverMajorVersions, driverMajorVersion)) {
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
        if (!databaseProductName.equalsIgnoreCase(this.databaseProductName)) {
            return false;
        }
        int databaseMajorVersion = metaData.getDatabaseMajorVersion();
        logger.log(Level.FINER, "Database major version: {0}", databaseMajorVersion);
        if (!ArrayUtils.contains(this.databaseMajorVersions, databaseMajorVersion)) {
            return false;
        }

        return true;
    }
}
