package org.arp.javautil.sql;

import java.util.logging.Logger;

public final class SQLUtil {
	private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(SQLUtil.class
				.getPackage().getName());
	}

	private SQLUtil() {

	}

	/**
	 * Gets the logger for this package.
	 * 
	 * @return a <code>Logger</code> object.
	 */
	static Logger logger() {
		return LazyLoggerHolder.instance;
	}

}
