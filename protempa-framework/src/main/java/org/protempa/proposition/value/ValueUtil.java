package org.protempa.proposition.value;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Utilities for the time units project.
 * 
 * @author Andrew Post
 */
final class ValueUtil {

    private ValueUtil() {
    }

    private static class LazyResourceBundleHolder {

        private static ResourceBundle instance =
                ResourceBundle.getBundle(
                "org.protempa.proposition.value.resources.bundles.Messages");
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(ValueUtil.class.getPackage().getName());
    }

    /**
     * Gets the messages for this project's resource bundle.
     *
     * @return a <code>ResourceBundle</code>.
     */
    static ResourceBundle resourceBundle() {
        return LazyResourceBundleHolder.instance;
    }

    static Logger logger() {
        return LazyLoggerHolder.instance;
    }
}
