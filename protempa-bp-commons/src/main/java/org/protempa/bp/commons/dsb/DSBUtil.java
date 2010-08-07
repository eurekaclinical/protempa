package org.protempa.bp.commons.dsb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.InvalidPropertyNameException;

/**
 *
 * @author Andrew Post
 */
class DSBUtil {
    private DSBUtil() {

    }

    private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(DSBUtil.class
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
