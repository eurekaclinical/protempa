package org.protempa;

import java.text.Format;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Andrew Post
 */
public final class ProtempaUtil {

    /**
     * Private constructor.
     */
    private ProtempaUtil() {
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(ProtempaUtil.class.getPackage().getName());
    }

    /**
     * Gets the logger for this package.
     *
     * @return a {@link Logger} object.
     */
    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    public static class PropositionDefinitionDisplayNameComparator implements
            Comparator<PropositionDefinition> {

        private final Format dnFormat;

        public PropositionDefinitionDisplayNameComparator() {
            this(null, null);
        }

        public PropositionDefinitionDisplayNameComparator(
                DisplayNameFormat.Style style) {
            this(style, null);
        }

        public PropositionDefinitionDisplayNameComparator(Locale locale) {
            this(null, locale);
        }

        public PropositionDefinitionDisplayNameComparator(
                DisplayNameFormat.Style style, Locale locale) {
            this.dnFormat = DisplayNameFormat.getInstance(style, locale);
        }

        @Override
        public int compare(PropositionDefinition k1, PropositionDefinition k2) {
            return dnFormat.format(k1).compareTo(dnFormat.format(k2));
        }
    }

    /**
     * Checks if the specified array is null, has length zero or has null
     * values. If any of those criteria are me, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param array an {@link Object[]}.
     * @param arrayName the name {@link String} used in the exception message.
     */
    public static void checkArray(Object[] array, String arrayName) {
        if (array == null) {
            throw new IllegalArgumentException(arrayName
                    + " cannot be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(arrayName
                    + " cannot be empty");
        }
        for (Object elt : array) {
            if (elt == null) {
                throw new IllegalArgumentException(arrayName
                        + " cannot contain null values");
            }
        }
    }
}
