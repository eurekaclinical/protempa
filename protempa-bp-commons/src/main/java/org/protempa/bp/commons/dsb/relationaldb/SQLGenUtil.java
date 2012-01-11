package org.protempa.bp.commons.dsb.relationaldb;

import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;

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

    static boolean somePropIdsMatch(EntitySpec es1, EntitySpec es2) {
        return !Collections.containsAny(Arrays.asSet(es1.getPropositionIds()),
                es2.getPropositionIds());
    }
}
