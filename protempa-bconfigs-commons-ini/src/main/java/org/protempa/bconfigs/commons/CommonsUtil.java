package org.protempa.bconfigs.commons;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
class CommonsUtil {
    private CommonsUtil() {

    }

    private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(CommonsUtil.class
				.getPackage().getName());
	}

	/**
	 * Gets the logger for this package.
	 *
	 * @return a {@link Logger} object.
	 */
	static Logger logger() {
		return LazyLoggerHolder.instance;
	}
}
