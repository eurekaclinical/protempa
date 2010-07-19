package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Andrew Post
 */
public class DriverManagerConnectionSpec implements ConnectionSpec {
    private String url;
    private String user;
    private String password;

    public DriverManagerConnectionSpec(String url, String user,
            String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Driver getDriver() throws SQLException {
        return DriverManager.getDriver(this.url);
    }

    public Connection getOrCreate() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

}
