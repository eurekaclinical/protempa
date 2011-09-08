package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import org.arp.javautil.io.Retryable;
import org.arp.javautil.io.Retryer;

/**
 * Convenience class for executing SQL queries.
 * 
 * @author Andrew Post
 */
public final class SQLExecutor {
    
    private static final int RETRIES = 3;

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

    public static void executeSQL(Connection connection, Statement stmt,
            String query, ResultProcessor resultProcessor) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        SQLUtil.logger().log(Level.FINE, "Executing SQL: {0}", query);
        ResultSet resultSet = null;
        if (resultProcessor != null) {
            resultSet = stmt.executeQuery(query);
        } else {
            stmt.execute(query);
        }
        SQLUtil.logger().log(Level.FINE, "Done executing SQL");

        if (resultProcessor != null) {
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
            SQLUtil.logger().log(Level.FINE, "Executing SQL: {0}", sql);
            ResultSet resultSet = null;
            if (resultProcessor != null) {
                resultSet = stmt.executeQuery(sql);
            } else {
                stmt.execute(sql);
            }
            SQLUtil.logger().log(Level.FINE, "Done executing SQL");

            if (resultProcessor != null) {
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
        SQLUtil.logger().log(Level.FINE, "Executing SQL: {0}", sql);
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
        executeSQL(connectionCreator, sql, resultProcessor, 0);
    }
    
    private static abstract class RetryableExecutor implements 
            Retryable<SQLException> {
        private static final long THREE_SECONDS = 3 * 1000L;
        
        @Override
        public void recover() {
            try {
                Thread.sleep(THREE_SECONDS);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    private static class RetryableStatementExecutor extends RetryableExecutor {
        private final ConnectionSpec connectionSpec;
        private final String sql;
        private final ResultProcessor resultProcessor;
        
        RetryableStatementExecutor(ConnectionSpec connectionCreator, 
                String sql, ResultProcessor resultProcessor) {
            this.connectionSpec = connectionCreator;
            this.sql = sql;
            this.resultProcessor = resultProcessor;
        }

        @Override
        public SQLException attempt() {
            try {
                Connection con = this.connectionSpec.getOrCreate();
                try {
                    executeSQL(con, this.sql, this.resultProcessor);
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
                return null;
            } catch (SQLException sqle) {
                return sqle;
            }
        }
    }
    
    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            ResultProcessor resultProcessor, int retries) throws SQLException {
        if (connectionCreator == null) {
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        }
        RetryableStatementExecutor executor = 
                new RetryableStatementExecutor(connectionCreator, sql,
                        resultProcessor);
        Retryer<SQLException> retryer = new Retryer<SQLException>(RETRIES);
        if (!retryer.execute(executor)) {
            throw assembleSQLException(retryer.getErrors());
        }
    }
    
    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            StatementPreparer stmtPreparer, ResultProcessor resultProcessor) 
            throws SQLException {
        executeSQL(connectionCreator, sql, stmtPreparer, resultProcessor, 0);
    }
    
    private static class RetryablePreparedStatementExecutor 
            extends RetryableExecutor {
        private final ConnectionSpec connectionSpec;
        private final String sql;
        private final StatementPreparer stmtPreparer;
        private final ResultProcessor resultProcessor;
        
        RetryablePreparedStatementExecutor(ConnectionSpec connectionCreator, 
                String sql, StatementPreparer stmtPreparer,
                ResultProcessor resultProcessor) {
            this.connectionSpec = connectionCreator;
            this.sql = sql;
            this.resultProcessor = resultProcessor;
            this.stmtPreparer = stmtPreparer;
        }

        @Override
        public SQLException attempt() {
            try {
                Connection con = this.connectionSpec.getOrCreate();
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
                return null;
            } catch (SQLException sqle) {
                return sqle;
            }
        }
    }

    public static void executeSQL(ConnectionSpec connectionCreator, String sql,
            StatementPreparer stmtPreparer, ResultProcessor resultProcessor,
            int retries) throws SQLException {
        if (connectionCreator == null) {
            throw new IllegalArgumentException(
                    "connectionCreator cannot be null");
        }
        RetryableExecutor executor = 
                new RetryablePreparedStatementExecutor(connectionCreator, sql,
                        stmtPreparer, resultProcessor);
        Retryer<SQLException> retryer = new Retryer<SQLException>(RETRIES);
        if (!retryer.execute(executor)) {
            throw assembleSQLException(retryer.getErrors());
        }
    }
    
    public static SQLException assembleSQLException(
            List<SQLException> exceptions) {
        if (exceptions == null) {
            throw new IllegalArgumentException("exceptions cannot be null");
        }
        SQLException exceptionChain = null;
        SQLException currentException = null;
        for (SQLException sqle : exceptions) {
            if (exceptionChain == null) {
                exceptionChain = sqle;
                currentException = sqle;
            } else {
                currentException.setNextException(sqle);
                currentException = sqle;
            }
        }
        return exceptionChain;
    }
}
