package org.protempa.ksb.protege;

import java.util.logging.Logger;

/**
 * Utilities for this project.
 * 
 * @author Andrew Post
 */
class HELLPUtil {

	private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(HELLPUtil.class
				.getPackage().getName());
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
