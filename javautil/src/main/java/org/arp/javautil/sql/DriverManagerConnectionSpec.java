package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements getting a connection to a relational database using Java's
 * {@link DriverManager} API.
 * 
 * @author Andrew Post
 */
public class DriverManagerConnectionSpec implements ConnectionSpec {
    private String url;
    private String user;
    private String password;

    /**
     * Creates an instance with a specified JDBC URL, and a username and
     * password.
     * 
     * @param url a JDBC URL {@link String}.
     * @param user a username {@link String}.
     * @param password a password {@link String}.
     */
    public DriverManagerConnectionSpec(String url, String user, String password)
    {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Creates a database connection or gets an existing connection with
     * the JDBC URL, username and password specified in the constructor.
     *
     * @return a {@link Connection}.
     *
     * @throws SQLException if an error occurred creating/getting a
     * {@link Connection}, possibly because the JDBC URL, username and/or
     * password are invalid.
     */
    @Override
    public Connection getOrCreate() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

}
