package org.protempa.backend.asb.java;

import java.util.logging.Logger;

class JavaAlgorithmUtil {
	private static class LazyLoggerHolder {
		private static Logger instance = Logger
				.getLogger(JavaAlgorithmUtil.class.getPackage().getName());
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
