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
import org.arp.javautil.sql.DatabaseMetaDataWrapper;
import org.arp.javautil.version.MajorMinorVersion;

/**
 * Abstract class for constructing a SQL generator with useful compatibility
 * checks between the driver and the actual database instance.
 * 
 * @author Andrew Post
 */
public abstract class AbstractSQLGeneratorWithCompatChecks extends AbstractSQLGenerator {
    private final String driverClassName;
    private final String driverName;
    private final MajorMinorVersion minDriverVersion;
    private final MajorMinorVersion maxDriverVersion;
    private final String databaseProductName;
    private final MajorMinorVersion minDatabaseVersion;
    private final MajorMinorVersion maxDatabaseVersion;

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
            String driverName, MajorMinorVersion minDriverVersion,
            MajorMinorVersion maxDriverVersion,
            String databaseProductName, MajorMinorVersion minDatabaseVersion,
            MajorMinorVersion maxDatabaseVersion) {
        if (driverName == null) {
            throw new IllegalArgumentException("driverName cannot be null");
        }
        if (databaseProductName == null) {
            throw new IllegalArgumentException("databaseProductName cannot be null");
        }        
        this.driverClassName = driverClassName;
        this.driverName = driverName;
        this.minDriverVersion = minDriverVersion;
        this.maxDriverVersion = maxDriverVersion;
        this.databaseProductName = databaseProductName;
        this.minDatabaseVersion = minDatabaseVersion;
        this.maxDatabaseVersion = maxDatabaseVersion;
    }
    
    @Override
    public final boolean checkCompatibility(Connection connection) throws SQLException {
        DatabaseMetaDataWrapper metaDataWrapper = new DatabaseMetaDataWrapper(connection.getMetaData());
        return metaDataWrapper.isDatabaseCompatible(this.databaseProductName, this.minDatabaseVersion, this.maxDatabaseVersion) && 
                metaDataWrapper.isDriverCompatible(this.driverName, this.minDriverVersion, this.maxDriverVersion);
    }
    
    @Override
    protected final String getDriverClassNameToLoad() {
        return this.driverClassName;
    }
    
}
