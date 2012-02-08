/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.arp.javautil.io.Retryable;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;

class RetryableSQLExecutor implements
        Retryable<SQLException> {

    private static final long THREE_SECONDS = 3 * 1000L;

    private final ConnectionSpec connectionSpec;
    private final String query;
    private final ResultProcessor resultProcessor;

    RetryableSQLExecutor(ConnectionSpec connectionSpec, String query,
            ResultProcessor resultProcessor) {
        this.connectionSpec = connectionSpec;
        this.query = query;
        this.resultProcessor = resultProcessor;
    }

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

    @Override
    public void recover() {
        try {
            Thread.sleep(THREE_SECONDS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
