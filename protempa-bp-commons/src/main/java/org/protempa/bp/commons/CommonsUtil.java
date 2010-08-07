package org.protempa.bp.commons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.InvalidPropertyNameException;

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
