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
package org.protempa.backend.dsb.relationaldb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.DataSourceReadException;

/**
 *
 * @author Andrew Post
 */
public class StreamingSQLExecutor {

    private final Connection connection;
    private final String backendNameForMessages;

    StreamingSQLExecutor(Connection connection,
            String backendNameForMessages, Integer timeout) {
        this.connection = connection;
        this.backendNameForMessages = backendNameForMessages;
    }
    
    Connection getConnection() {
        return this.connection;
    }
    
    void executeSelect(String entitySpecName, String query,
            StreamingResultProcessor<?> resultProcessor)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        if (this.connection == null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Data source backend {0} is skipping query for {1}",
                        new Object[]{backendNameForMessages,
                            entitySpecName});
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Data source backend {0} is executing query for {1}",
                        new Object[]{backendNameForMessages,
                            entitySpecName});
            }

            try {
                Statement stmt = connection.createStatement();
                stmt.setFetchSize(AbstractSQLGenerator.FETCH_SIZE);
                ResultSet resultSet = stmt.executeQuery(query);
                resultProcessor.setStatement(stmt);
                resultProcessor.process(resultSet);
            } catch (SQLException ex) {
                throw new DataSourceReadException("Error retrieving "
                        + entitySpecName + " from data source backend "
                        + backendNameForMessages, ex);
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Query for {0} in data source backend {1} is complete",
                        new Object[]{entitySpecName,
                            backendNameForMessages});
            }
        }
    }
}
