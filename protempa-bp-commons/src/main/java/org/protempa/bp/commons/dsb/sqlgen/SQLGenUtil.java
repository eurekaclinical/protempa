package org.protempa.bp.commons.dsb.sqlgen;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
class SQLGenUtil {

    static final String SYSTEM_PROPERTY_SKIP_EXECUTION =
            "protempa.dsb.relationaldatabase.skipexecution";

    static final String SYSTEM_PROPERTY_FORCE_SQL_GENERATOR =
            "protempa.dsb.relationaldatabase.sqlgenerator";

    private SQLGenUtil() {
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(SQLGenUtil.class.getPackage().getName());
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
