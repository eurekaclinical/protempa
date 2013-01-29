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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.io.Retryer;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.DataSourceReadException;

/**
 *
 * @author Andrew Post
 */
final class RetryingSQLExecutor extends SQLExecutor {
    private int retryCount;
    
    RetryingSQLExecutor(ConnectionSpec connectionSpec, 
            String backendNameForMessages, Integer timeout,
            int retryCount) {
        super(connectionSpec, backendNameForMessages, timeout);
        assert retryCount >= 0 : "retryCount must be >= 0";
        this.retryCount = retryCount;
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
            logQueryTimeout(logger);

            RetryableSQLExecutor operation = new RetryableSQLExecutor(
                    getConnectionSpec(), query, resultProcessor, getTimeout());
            Retryer<SQLException> retryer = new Retryer<SQLException>(
                    this.retryCount);
            if (!retryer.execute(operation)) {
                SQLException ex = 
                        org.arp.javautil.sql.SQLExecutor.assembleSQLException(
                        retryer.getErrors());
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
}
