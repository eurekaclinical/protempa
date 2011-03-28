package org.protempa.proposition.value;

import java.util.logging.Logger;

public class Util {

    private Util() {
    }

    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    private static class LazyLoggerHolder {

        private static Logger instance = Logger.getLogger(Util.class
                .getPackage().getName());
    }
}
