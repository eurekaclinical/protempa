package org.protempa.bp.commons.dsb.sqlgen;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
class SQLGenUtil {

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

    static boolean isInReferences(EntitySpec entitySpec,
            ReferenceSpec[] referenceSpecs) {
        assert entitySpec != null : "entitySpec cannot be null";
        assert referenceSpecs != null : "referenceSpecs cannot be null";
        
        boolean found = false;
        for (ReferenceSpec refSpec : referenceSpecs) {
            if (refSpec.getEntityName().equals(entitySpec.getName())) {
                found = true;
                continue;
            }
        }
        return found;
    }
}
