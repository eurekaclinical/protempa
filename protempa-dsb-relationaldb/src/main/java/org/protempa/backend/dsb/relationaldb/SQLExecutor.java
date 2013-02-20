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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.DataSourceReadException;

/**
 *
 * @author Andrew Post
 */
abstract class SQLExecutor {
    private final ConnectionSpec connectionSpec;
    private final String backendNameForMessages;
    private final Integer timeout;
    
    SQLExecutor(ConnectionSpec connectionSpec, String backendNameForMessages, 
            Integer timeout) {
        this.backendNameForMessages = backendNameForMessages;
        this.connectionSpec = connectionSpec;
        this.timeout = timeout;
    }

    String getBackendNameForMessages() {
        return backendNameForMessages;
    }

    ConnectionSpec getConnectionSpec() {
        return connectionSpec;
    }

    Integer getTimeout() {
        return timeout;
    }
    
    abstract void executeSelect(String entitySpecName, String query,
            SQLGenResultProcessor resultProcessor)
            throws DataSourceReadException;
    
    protected void logQueryTimeout(Logger logger) {
        Level level = Level.FINER;
        if (logger.isLoggable(level)) {
            if (this.timeout != null) {
                logger.log(level,
                        "Data source backend {0} has query timeout set to {1,number,integer} seconds",
                        new Object[]{backendNameForMessages, this.timeout});
            } else {
                logger.log(level,
                        "Query timeout is not set for data source backend {0}",
                        new Object[]{backendNameForMessages});
            }
        }
    }
}
