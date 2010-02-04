package org.protempa.backend;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
public class BackendUtil {
    private BackendUtil() {

    }

    private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(BackendUtil.class
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
