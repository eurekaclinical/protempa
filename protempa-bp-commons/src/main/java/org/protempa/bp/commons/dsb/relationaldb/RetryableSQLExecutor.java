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