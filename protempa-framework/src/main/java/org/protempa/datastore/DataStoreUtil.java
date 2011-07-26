package org.protempa.datastore;

import java.util.logging.Logger;

final class DataStoreUtil {
    private DataStoreUtil() {
    }

    private static class LazyLoggerHolder {
        private static Logger instance = Logger.getLogger(DataStoreUtil.class
                .getPackage().getName());
    }

    static Logger logger() {
        return LazyLoggerHolder.instance;
    }
}
