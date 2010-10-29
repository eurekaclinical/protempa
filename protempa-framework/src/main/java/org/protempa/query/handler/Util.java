package org.protempa.query.handler;

import java.util.logging.Logger;

public class Util {

    private static Logger logger;

    static Logger logger() {
        if (logger == null) {
            logger = Logger.getLogger(Util.class.getPackage().getName());
        }
        return logger;
    }
}
