package org.protempa.dsb;

import java.lang.reflect.Field;
import java.util.logging.Logger;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.InvalidPropertyNameException;

/**
 *
 * @author Andrew Post
 */
public class DSUtil {
    private DSUtil() {

    }

    private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(DSUtil.class
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
