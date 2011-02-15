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
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        if (stmtPreparer != null) {
            stmtPreparer.prepare(preparedStmt);
        }

        preparedStmt.execute();
        SQLUtil.logger().log(Level.FINE, "Done executing SQL");

        if (resultProcessor != null) {
            ResultSet resultSet = preparedStmt.getResultSet();
            SQLUtil.logger().log(Level.FINE, "Processing result set");
            try {
                resultProcessor.process(resultSet);
                resultSet.close();
                resultSet = null;
                SQLUtil.logger().log(Level.FINE, "Done processing result set");
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException ex) {
                    }
                }
            }
        }
    }

    public static void executeSQL(Connection connection, String sql,
            ResultProcessor resultProcessor) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        Statement stmt = connection.createStatement();
        try {
            SQLUtil.logger().log(Level.FINE, "Executing SQL: " + sql);
            stmt.execute(sql);
            SQLUtil.logger().log(Level.FINE, "Done executing SQL");

            if (resultProcessor != null) {
                ResultSet resultSet = stmt.getResultSet();
                SQLUtil.logger().log(Level.FINE, "Processing result set");
                try {
                    resultProcessor.process(resultSet);
                    resultSet.close();
                    resultSet = null;
                    SQLUtil.logger().log(Level.FINE, "Done processing result set");
                } finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException ex) {
                        }
                    }
                }
            }
            stmt.close();
            stmt = null;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void executeSQL(Connection connection, String sql,
            ResultProcessor resultProcessor, int fetchSize) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        Statement stmt = connection.createStatement();
        try {
            SQLUtil.logger().log(Level.FINE, "executing SQL: " + sql);
            stmt.setFetchSize(fetchSize);
            stmt.execute(sql);
            SQLUtil.logger().log(Level.FINE, "Done executing SQL");

            if (resultProcessor != null) {
                ResultSet resultSet = stmt.getResultSet();
                SQLUtil.logger().log(Level.FINE, "Processing result set");
                try {
                    resultProcessor.process(resultSet);
                    resultSet.close();
                    resultSet = null;
                    SQLUtil.logger().log(Level.FINE, "Done processing result set");
                } finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException ex) {
                        }
                    }

                }
            }
            stmt.close();
            stmt = null;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void executeSQL(Connection connection, String sql,
            StatementPreparer stmtPreparer, ResultProcessor resultProcessor)
            throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        SQLUtil.logger().log(Level.FINE, "executing SQL: " + sql);
        PreparedStatement stmt = connection.prepareStatement(sql);
        try {
            executeSQL(connection, stmt, stmtPreparer, resultProcessor);
            stmt.close();
            stmt = null;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            ResultProcessor resultProcessor) throws SQLException {
        if (connectionCreator == null) {
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        }
        Connection con = connectionCreator.getOrCreate();
        try {
            executeSQL(con, sql, resultProcessor);
            con.close();
            con = null;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            ResultProcessor resultProcessor, boolean readOnly) throws SQLException {
        if (connectionCreator == null) {
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        }
        Connection con = connectionCreator.getOrCreate();
        con.setReadOnly(true);
        try {
            executeSQL(con, sql, resultProcessor);
            con.close();
            con = null;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            ResultProcessor resultProcessor, boolean readOnly, int fetchSize)
            throws SQLException {
        if (connectionCreator == null) {
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        }
        Connection con = connectionCreator.getOrCreate();
        con.setReadOnly(true);
        try {
            executeSQL(con, sql, resultProcessor, fetchSize);
            con.close();
            con = null;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            StatementPreparer stmtPreparer, ResultProcessor resultProcessor)
            throws SQLException {
        if (connectionCreator == null) {
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        }
        Connection con = connectionCreator.getOrCreate();
        try {
            executeSQL(con, sql, stmtPreparer, resultProcessor);
            con.close();
            con = null;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }
    }
}
