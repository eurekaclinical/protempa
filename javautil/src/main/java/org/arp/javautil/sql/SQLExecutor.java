package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * Convenience class for executing SQL queries.
 * 
 * @author Andrew Post
 */
public final class SQLExecutor {

    public static interface StatementPreparer {

        void prepare(PreparedStatement stmt) throws SQLException;
    }

    public static interface ResultProcessor {

        void process(ResultSet resultSet) throws SQLException;
    }

    public static void executeSQL(Connection connection,
            PreparedStatement preparedStmt, StatementPreparer stmtPreparer,
            ResultProcessor resultProcessor) throws SQLException {
        if (connection == null)
            throw new IllegalArgumentException("connection cannot be null");
        if (stmtPreparer != null) {
            stmtPreparer.prepare(preparedStmt);
        }

        ResultSet resultSet = null;
        try {
            preparedStmt.execute();
            if (resultProcessor != null) {
                resultProcessor.process(preparedStmt.getResultSet());
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    public static void executeSQL(Connection connection, String sql,
            ResultProcessor resultProcessor) throws SQLException {
        if (connection == null)
            throw new IllegalArgumentException("connection cannot be null");
        Statement stmt = connection.createStatement();
        try {
            ResultSet resultSet = null;
            try {
                SQLUtil.logger().log(Level.FINE, "executing SQL: " + sql);
                stmt.execute(sql);
                if (resultProcessor != null) {
                    resultProcessor.process(stmt.getResultSet());
                }
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        } finally {
            stmt.close();
        }
    }

    public static void executeSQL(Connection connection, String sql,
            StatementPreparer stmtPreparer, ResultProcessor resultProcessor)
            throws SQLException {
        if (connection == null)
            throw new IllegalArgumentException("connection cannot be null");
        PreparedStatement stmt = null;
        try {
            SQLUtil.logger().log(Level.FINE, "executing SQL: " + sql);
            stmt = connection.prepareStatement(sql);
            executeSQL(connection, stmt, stmtPreparer, resultProcessor);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            ResultProcessor resultProcessor) throws SQLException {
        if (connectionCreator == null)
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        Connection con = null;
        try {
            con = connectionCreator.getOrCreate();
            executeSQL(con, sql, resultProcessor);
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }

    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            StatementPreparer stmtPreparer, ResultProcessor resultProcessor)
            throws SQLException {
        if (connectionCreator == null)
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        Connection con = null;
        try {
            con = connectionCreator.getOrCreate();
            executeSQL(con, sql, stmtPreparer, resultProcessor);
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
}
