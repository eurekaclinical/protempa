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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.arp.javautil.io.Retryable;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;

/**
 * Executes queries leveraging {@link Retryable} to attempt a specified number
 * of times before giving up.
 * 
 * @author Andrew Post
 */
class RetryableSQLExecutor implements
        Retryable<SQLException> {

    private static final long THREE_SECONDS = 3 * 1000L;

    private final ConnectionSpec connectionSpec;
    private final String query;
    private final ResultProcessor resultProcessor;
    private final Integer queryTimeout;

    /**
     * Initializes the executor with connection information, a SQL select
     * statement, a result processor and a query timeout.
     * 
     * @param connectionSpec a {@link ConnectionSpec}. Cannot be 
     * <code>null</code>.
     * @param query a SQL select statement {@link String}. Cannot be 
     * <code>null</code>.
     * @param resultProcessor a {@link ResultProcessor}.
     * @param queryTimeout a query timeout in seconds. If not <code>null</code>,
     * this causes {@link Statement#setQueryTimeout(int) } to be called with 
     * the specified timeout. If <code>null</code> is specified, no timeout is 
     * set.
     */
    RetryableSQLExecutor(ConnectionSpec connectionSpec, String query,
            ResultProcessor resultProcessor, Integer queryTimeout) {
        assert connectionSpec != null : "connectionSpec cannot be null";
        this.connectionSpec = connectionSpec;
        assert query != null : "query cannot be null";
        this.query = query;
        this.resultProcessor = resultProcessor;
        assert queryTimeout == null || queryTimeout >= 0 : 
                "queryTimeout cannot be negative";
        this.queryTimeout = queryTimeout;
    }
    
    /**
     * Initializes the executor with connection information, a SQL select
     * statement and a result processor. Query timeout is not set.
     * 
     * @param connectionSpec a {@link ConnectionSpec}. Cannot be 
     * <code>null</code>.
     * @param query a SQL select statement {@link String}. Cannot be 
     * <code>null</code>.
     * @param resultProcessor a {@link ResultProcessor}.
     */
    RetryableSQLExecutor(ConnectionSpec connectionSpec, String query,
            ResultProcessor resultProcessor) {
        this(connectionSpec, query, resultProcessor, null);
    }

    /**
     * Attempts executing the query specified in the constructor.
     * 
     * @return a {@link SQLException} if execution failed, or <code>null</code>
     * if execution was successful.
     */
    @Override
    public SQLException attempt() {
        try {
            Connection con = this.connectionSpec.getOrCreate();
            con.setReadOnly(true);
            try {
                Statement stmt = con.createStatement(
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
                stmt.setFetchSize(AbstractSQLGenerator.FETCH_SIZE);
                if (this.queryTimeout != null) {
                    stmt.setQueryTimeout(this.queryTimeout);
                }
                try {
                    SQLExecutor.executeSQL(con, stmt, this.query,
                            this.resultProcessor);
                    stmt.close();
                    stmt = null;
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                        }
                    }
                }
                con.close();
                con = null;
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                    }
                }
            }
            return null;
        } catch (SQLException ex) {
            return ex;
        }
    }

    /**
     * Waits three seconds before retrying in the event of a failure.
     */
    @Override
    public void recover() {
        try {
            Thread.sleep(THREE_SECONDS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
