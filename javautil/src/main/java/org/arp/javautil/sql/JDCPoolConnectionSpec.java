package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gets a connection from the JDC connection pool.
 * 
 * @author Andrew Post
 */
public class JDCPoolConnectionSpec implements ConnectionSpec {
    private static final String JDC_POOL_URL = "jdbc:jdc:jdcpool";

    public Connection getOrCreate() throws SQLException {
        return DriverManager.getConnection(JDC_POOL_URL);
    }

    public Driver getDriver() throws SQLException {
        return DriverManager.getDriver(JDC_POOL_URL);
    }

}
