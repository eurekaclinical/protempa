package org.arp.javautil.map;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
public class MapUtil {

    private static class LazyLoggerHolder {

        private static Logger instance = Logger.getLogger(MapUtil.class.getPackage().getName());
    }

    /**
     * Gets the logger for this package.
     *
     * @return a <code>Logger</code> object.
     */
    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    private MapUtil() {
    }
}
