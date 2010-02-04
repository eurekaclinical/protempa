package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gets a connection from the JDC connection pool.
 * 
 * @author Andrew Post
 */
public class JDCPoolConnectionSpec implements ConnectionSpec {

	public Connection getOrCreate() throws SQLException {
		return DriverManager.getConnection("jdbc:jdc:jdcpool");
	}

}
