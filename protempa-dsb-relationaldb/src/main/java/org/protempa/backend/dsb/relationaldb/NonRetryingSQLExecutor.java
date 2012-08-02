/*
 * #%L
 * Protempa Relational Database Data Source Backend
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.DataSourceReadException;

/**
 *
 * @author Andrew Post
 */
public class NonRetryingSQLExecutor extends SQLExecutor {

    private Connection connection;

    NonRetryingSQLExecutor(ConnectionSpec connectionSpec,
            String backendNameForMessages, Integer timeout)
            throws SQLException {
        super(connectionSpec, backendNameForMessages, timeout);
        this.connection = connectionSpec.getOrCreate();
        this.connection.setAutoCommit(false);
    }
    
    NonRetryingSQLExecutor(Connection connection, 
            String backendNameForMessages, Integer timeout) {
        super(null, backendNameForMessages, timeout);
        this.connection = connection;
    }

    @Override
    void executeSelect(String entitySpecName, String query,
            SQLGenResultProcessor resultProcessor)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        if (Boolean.getBoolean(SQLGenUtil.SYSTEM_PROPERTY_SKIP_EXECUTION)) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Data source backend {0} is skipping query for {1}",
                        new Object[]{getBackendNameForMessages(),
                            entitySpecName});
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Data source backend {0} is executing query for {1}",
                        new Object[]{getBackendNameForMessages(),
                            entitySpecName});
            }

            try {
                org.arp.javautil.sql.SQLExecutor.executeSQL(
                        this.connection, query, resultProcessor);
            } catch (SQLException ex) {
                throw new DataSourceReadException("Error retrieving "
                        + entitySpecName + " from data source backend "
                        + getBackendNameForMessages(), ex);
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Query for {0} in data source backend {1} is complete",
                        new Object[]{entitySpecName,
                            getBackendNameForMessages()});
            }
        }
    }

    void close() throws SQLException {
        boolean closedSuccessfully = false;
        try {
            this.connection.commit();
            
            this.connection.close();
            closedSuccessfully = true;
        } finally {
            if (!closedSuccessfully) {
                try {
                    this.connection.close();
                } catch (SQLException ignore) {
                }
            }

        }
    }
}
