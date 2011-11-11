package org.protempa.bp.commons;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
class CommonsUtil {
    private static class LazyResourceBundleHolder {
		private static ResourceBundle instance = ResourceBundle
				.getBundle("Messages");
	}

	/**
	 * Gets the messages for this project's resource bundle.
	 *
	 * @return a {@link ResourceBundle}.
	 */
	static ResourceBundle resourceBundle() {
		return LazyResourceBundleHolder.instance;
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
