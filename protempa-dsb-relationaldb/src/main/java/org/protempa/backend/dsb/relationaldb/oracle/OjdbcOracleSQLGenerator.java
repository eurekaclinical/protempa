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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.sql.DatabaseMetaDataWrapper;
import org.arp.javautil.sql.DatabaseVersion;
import org.arp.javautil.sql.DriverVersion;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.relationaldb.AbstractSQLGenerator;
import org.protempa.backend.dsb.relationaldb.EntitySpec;
import org.protempa.backend.dsb.relationaldb.ReferenceSpec;
import org.protempa.backend.dsb.relationaldb.SQLGenResultProcessor;
import org.protempa.backend.dsb.relationaldb.SQLOrderBy;
import org.protempa.backend.dsb.relationaldb.SelectStatement;

/**
 * Generates SQL compatible with Oracle 11.x and 12.x 
 * 
 * @author Andrew Post, Nita Deshpande
 */
public class OjdbcOracleSQLGenerator extends AbstractSQLGenerator {
    
    private static final String DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver";
    private static final String DATABASE_PRODUCT_NAME = "Oracle";
    private static final String DRIVER_NAME = "Oracle JDBC driver";
    private static final DriverVersion MAX_DRIVER_VERSION = new DriverVersion(12, Integer.MAX_VALUE);
    
    private final String driverClassName;
    private final String driverName;
    private DriverVersion minDriverVersion;
    private final DriverVersion maxDriverVersion;
    private final String databaseProductName;
    private DatabaseVersion minDatabaseVersion;
    private DatabaseVersion maxDatabaseVersion;
    
    //for AIWD
    private static final DriverVersion ALT_MIN_DRIVER_VERSION = new DriverVersion(11, 0);
    private static final DatabaseVersion ALT_MIN_DATABASE_VERSION = new DatabaseVersion(10, 0);
    private static final DatabaseVersion ALT_MAX_DATABASE_VERSION = new DatabaseVersion(11, Integer.MAX_VALUE);
    //for CDWS and CDWP
    private static final DriverVersion MIN_DRIVER_VERSION = new DriverVersion(12, 0);
    //CDWP is now at Oracle 19
    private static final DatabaseVersion MIN_DATABASE_VERSION = new DatabaseVersion(19, 0);
    private static final DatabaseVersion MAX_DATABASE_VERSION = new DatabaseVersion(19, Integer.MAX_VALUE);
    
    private static Logger logger = Logger.getLogger(OjdbcOracleSQLGenerator.class.getName());

    
    public OjdbcOracleSQLGenerator() {
    	this.driverClassName = DRIVER_CLASS_NAME;
        this.driverName = DRIVER_NAME;
        this.minDriverVersion = MIN_DRIVER_VERSION;
        this.maxDriverVersion = MAX_DRIVER_VERSION;
        this.databaseProductName = DATABASE_PRODUCT_NAME;
        this.minDatabaseVersion = MIN_DATABASE_VERSION;
        this.maxDatabaseVersion = MAX_DATABASE_VERSION;
    }
    
    public final boolean checkCompatibility(Connection connection) throws SQLException {
        DatabaseMetaDataWrapper metaDataWrapper = new DatabaseMetaDataWrapper(connection.getMetaData());
        boolean isCompatible = false;
        
        logger.log(Level.FINE, "DB specs: {0}", connection.getMetaData().getDatabaseProductName()+
        		":"+ connection.getMetaData().getDatabaseMinorVersion() + ":"+ connection.getMetaData().getDatabaseMajorVersion());
        logger.log(Level.FINE, "My Specs: {0}", this.databaseProductName + ":" + this.minDatabaseVersion + ":" + this.maxDatabaseVersion);

        logger.log(Level.FINE, "Driver specs: {0}", connection.getMetaData().getDriverName() +
        		":"+ connection.getMetaData().getDriverMinorVersion() + ":"+ connection.getMetaData().getDriverMajorVersion());
        logger.log(Level.FINE, "My Specs: {0}", this.driverName + ":" + this.minDriverVersion + ":" + this.maxDriverVersion);
        
        if(!metaDataWrapper.isDatabaseCompatible(this.databaseProductName, this.minDatabaseVersion, this.maxDatabaseVersion)){
        	this.minDatabaseVersion = ALT_MIN_DATABASE_VERSION;
            this.maxDatabaseVersion = ALT_MAX_DATABASE_VERSION;
            if(metaDataWrapper.isDatabaseCompatible(this.databaseProductName, this.minDatabaseVersion, this.maxDatabaseVersion)) {
            	isCompatible = true;
            }
            else
            	return (isCompatible = false);
        }
        else
        	isCompatible = true;
        if(!metaDataWrapper.isDriverCompatible(this.driverName, this.minDriverVersion, this.maxDriverVersion)){
        	this.minDriverVersion = ALT_MIN_DRIVER_VERSION;
        	if(metaDataWrapper.isDriverCompatible(this.driverName, this.minDriverVersion, this.maxDriverVersion)) {
            	isCompatible = true;
            }
        	else
            	return (isCompatible = false);
        }
        else
        	isCompatible = true;
        
        logger.log(Level.FINE, "Compatible? {0}", isCompatible);
        return isCompatible;
    }

    @Override
    protected SelectStatement getSelectStatement(EntitySpec entitySpec,
            List<EntitySpec> entitySpecs,
            LinkedHashMap<String, ReferenceSpec> inboundRefSpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            boolean wrapKeyId) {
        return new Ojdbc6OracleSelectStatement(entitySpec,
                entitySpecs, inboundRefSpecs, filters, propIds, keyIds, order, resultProcessor,
                wrapKeyId);
    }
    
}
