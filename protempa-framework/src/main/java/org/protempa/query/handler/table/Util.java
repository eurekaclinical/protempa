package org.protempa.query.handler.table;

import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
public class Util {

    private Util() {}

    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(Util.class.getPackage().getName());
    }
}
