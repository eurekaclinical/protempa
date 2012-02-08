/*
 * #%L
 * JavaUtil
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A wrapper driver that implements a connection pool. Adapted from the Java
 * Developer connection:
 * http://java.sun.com/developer/onlineTraining/Programming/JDCBook/conpool.html.
 */
public class JDCConnectionDriver implements Driver {

	public static final String URL_PREFIX = "jdbc:jdc:";

	private static final int MAJOR_VERSION = 1;

	private static final int MINOR_VERSION = 0;

	private static final DriverPropertyInfo[] NULL_DRIVER_PROPERTY_INFO
                = new DriverPropertyInfo[0];

	private JDCConnectionPool pool;

	/**
	 * Registers the driver.
	 * 
	 * @param driver
	 *            classname of a JDBC driver. If <code>null</code>, it will
	 *            try to use the "jdbc.drivers" system property to find a
	 *            suitable driver.
	 * @param url
	 *            a database JDBC url.
	 * @param user
	 *            a valid database user.
	 * @param password
	 *            a valid database password.
	 * @throws ClassNotFoundException
	 *             if the supplied driver classname is not on the classpath.
	 * @throws InstantiationException
	 *             if this {@link Class} represents an abstract class, an
	 *             interface, an array class, a primitive type, or void; or if
	 *             the class has no nullary constructor; or if the instantiation
	 *             fails for some other reason.
	 * @throws IllegalAccessException
	 *             if the class or its nullary constructor is not accessible.
	 * @throws SQLException
	 *             if the wrapper driver could not be registered with
	 *             {@link DriverManager}.
	 */
	public JDCConnectionDriver(String driver, String url, String user,
			String password) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, SQLException {
		DriverManager.registerDriver(this);
		if (driver != null) {
			Class.forName(driver).newInstance();
		}
		pool = new JDCConnectionPool(url, user, password);
	}

	public Connection connect(String url, Properties props)
                throws SQLException {
		if (!url.startsWith(URL_PREFIX)) {
			return null;
		}
		return pool.getConnection();
	}

	public void shutdown() throws SQLException {
		DriverManager.deregisterDriver(this);
		pool.closeConnections();
	}

	public boolean acceptsURL(String url) {
		return url.startsWith(URL_PREFIX);
	}

	public int getMajorVersion() {
		return MAJOR_VERSION;
	}

	public int getMinorVersion() {
		return MINOR_VERSION;
	}

	public DriverPropertyInfo[] getPropertyInfo(String str, Properties props) {
		return NULL_DRIVER_PROPERTY_INFO;
	}

	public boolean jdbcCompliant() {
		return false;
	}
}
