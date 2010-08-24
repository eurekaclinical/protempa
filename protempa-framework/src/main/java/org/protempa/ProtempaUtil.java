package org.protempa;

import java.util.Comparator;
import java.util.logging.Logger;

/**
 * Utility methods for the PROTEMPA project. They are not intended to be
 * used outside of projects that use PROTEMPA.
 * 
 * @author Andrew Post
 */
public final class ProtempaUtil {

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

    /**
     * Comparator for comparing proposition definitions by display name.
     */
    public static class PropositionDefinitionDisplayNameComparator implements
            Comparator<PropositionDefinition> {

        /**
         * For specifying which display name style to use. Options are:
         * <ul>
         * <li>ABBREVIATED: as in
         * {@link PropositionDefinition#getAbbreviatedDisplayName()}
         * <li>REGULAR: as in {@link PropositionDefinition#getDisplayName()}
         * </ul>
         */
        public static enum Style {

            ABBREVIATED,
            REGULAR;
        }
        private final Style style;

        /**
         * Instantiates this comparator with the default display name style
         * ({@link Style.REGULAR}.
         */
        public PropositionDefinitionDisplayNameComparator() {
            this(Style.REGULAR);
        }

        /**
         * Instantiates this comparator to use the short or long display
         * names when doing comparisons.
         *
         * @param style a {@link DisplayNameFormat.Style} representing the
         * regular or abbrevaited display name.
         */
        public PropositionDefinitionDisplayNameComparator(
                Style style) {
            this.style = style;
        }

        @Override
        public int compare(PropositionDefinition k1,
                PropositionDefinition k2) {
            switch (this.style) {
                case REGULAR:
                    return k1.getDisplayName().compareTo(k2.getDisplayName());
                case ABBREVIATED:
                    return k1.getAbbreviatedDisplayName().compareTo(
                            k2.getAbbreviatedDisplayName());
                default:
                    throw new AssertionError("should not be reached");
            }
        }
    }

    /**
     * Checks if the specified array is null, has length zero or has null
     * values. If any of those criteria are me, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param array an {@link Object[]}.
     * @param arrayName the variable name {@link String} of the array (
     * used in the exception message). Should not be <code>null</code>, or the
     * exception message will not indicate which parameter was erroneously
     * <code>null</code>.
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
        checkArrayForNullElement(array, arrayName);
    }

    /**
     * Checks if the specified array has any null elements. If so, an
     * [@link IllegalArgumentException is thrown.
     *
     * @param array an {@link Object[]}. Must not be <code>null</code>, or
     * a {@link NullPointerException} will be thrown.
     * @param arrayName the variable name {@link String} of the array (used
     * in the exception message). Should not be <code>null</code>, or the
     * exception message will not indicate which parameter was erroneously
     * <code>null</code>.
     */
    public static void checkArrayForNullElement(Object[] array,
            String arrayName) {
        for (Object elt : array) {
            if (elt == null) {
                throw new IllegalArgumentException(arrayName
                        + " cannot contain null values");
            }
        }
    }
}
