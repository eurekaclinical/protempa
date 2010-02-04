package org.protempa.ksb;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
public class KSUtil {
    private KSUtil() {

    }

    private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(KSUtil.class
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
