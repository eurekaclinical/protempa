package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides an interface for getting a connection to a relational database.
 *
 * @author Andrew Post
 */
public interface ConnectionSpec {
    /**
     * Returns a connection to a SQL database that may be new or part of a
     * connection pool.
     *
     * @return a {@link Connection}.
     * @throws SQLException if an error occurs getting the connection.
     */
    Connection getOrCreate() throws SQLException;
}
