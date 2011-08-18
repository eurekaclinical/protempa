package org.arp.javautil.datastore;

import java.util.logging.Logger;

/**
 * Utility class for the {@link org.arp.javautil.datastore} class. Contains
 * logging tools, constants, and other utility methods.
 * 
 * @author Michel Mansour
 * 
 */
public final class DataStoreUtil {

    private static class LazyLoggerHolder {
        private static final Logger instance = Logger.getLogger(DataStoreUtil.class
                .getPackage().getName());
    }
    
    /**
     * Gets the logger for this package.
     * 
     * @return a <code>Logger</code> object.
     */
    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    private DataStoreUtil() {} // to prevent instantiation
}
