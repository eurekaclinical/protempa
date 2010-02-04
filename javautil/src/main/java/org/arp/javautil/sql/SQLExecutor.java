package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		Statement stmt = connection.createStatement();
		try {
			ResultSet resultSet = null;
			try {
				stmt.execute(sql);
				if (resultProcessor != null) {
					resultProcessor.process(stmt.getResultSet());
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			executeSQL(connection, stmt, stmtPreparer, resultProcessor);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static void executeSQL(ConnectionSpec connectionCreator,
			String sql, ResultProcessor resultProcessor) throws SQLException {
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

	public static void executeSQL(ConnectionSpec connectionCreator,
			String sql, StatementPreparer stmtPreparer,
			ResultProcessor resultProcessor) throws SQLException {
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
