package org.protempa.bp.commons.dsb;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
class DSBUtil {

    private DSBUtil() {
    }

    private static class LazyLoggerHolder {

        private static final Logger instance =
                Logger.getLogger(DSBUtil.class.getPackage().getName());
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
