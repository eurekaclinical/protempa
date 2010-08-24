package org.protempa.proposition.value;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the possible valid comparisons between two {@link Value}
 * objects.
 * 
 * @author Andrew Post
 */
public enum ValueComparator {

    /**
     * The first value is greater than (>) the second.
     */
    GREATER_THAN(">", new CompatibleTypes(ValueType.ORDEREDVALUE,
        ValueType.ORDEREDVALUE)) {

        @Override
        public boolean subsumes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException(
                        "comparator cannot be null");
            return GREATER_THAN.equals(comparator);
        }
    },
    /**
     * The first value is less than (<) the second.
     */
    LESS_THAN("<", new CompatibleTypes(ValueType.ORDEREDVALUE,
        ValueType.ORDEREDVALUE)) {

        @Override
        public boolean subsumes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException(
                        "comparator cannot be null");
            return LESS_THAN.equals(comparator);
        }
    },
    /**
     * The two values are equal (=).
     */
    EQUAL_TO("=", new CompatibleTypes(ValueType.VALUE, ValueType.VALUE)) {

        @Override
        public boolean subsumes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException(
                        "comparator cannot be null");
            return EQUAL_TO.equals(comparator);
        }
    },
    /**
     * Unknown, meaning that the two values are not comparable, for example,
     * comparing a number to a string.
     */
    UNKNOWN("?", new CompatibleTypes(ValueType.VALUE, ValueType.VALUE)) {

        @Override
        public boolean subsumes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException(
                        "comparator cannot be null");
            return UNKNOWN.equals(comparator);
        }
    },
    /**
     * The first value is greater than or equal to (>=) the second.
     */
    GREATER_THAN_OR_EQUAL_TO(">=", new CompatibleTypes(
            ValueType.ORDEREDVALUE, ValueType.ORDEREDVALUE)) {

        @Override
        public boolean subsumes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException(
                        "comparator cannot be null");
            return EQUAL_TO.equals(comparator)
                    || GREATER_THAN.equals(comparator)
                    || GREATER_THAN_OR_EQUAL_TO.equals(comparator);
        }
    },
    /**
     * The first value is less than or equal to (<=) the second.
     */
    LESS_THAN_OR_EQUAL_TO("<=", new CompatibleTypes(
            ValueType.ORDEREDVALUE, ValueType.ORDEREDVALUE)) {

        @Override
        public boolean subsumes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException(
                        "comparator cannot be null");
            return EQUAL_TO.equals(comparator) || LESS_THAN.equals(comparator)
                    || LESS_THAN_OR_EQUAL_TO.equals(comparator);
        }
    },
    /**
     * The first value is in the second list.
     */
    IN("IN", new CompatibleTypes(ValueType.VALUE, ValueType.LISTVALUE)) {

        @Override
        public boolean subsumes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException(
                        "comparator cannot be null");
            return IN.equals(comparator);
        }
    };
    private static final Map<String, ValueComparator> compStringToComp =
            new HashMap<String, ValueComparator>();

    static {
        compStringToComp.put(LESS_THAN.getComparatorString(), LESS_THAN);
        compStringToComp.put(EQUAL_TO.getComparatorString(), EQUAL_TO);
        compStringToComp.put(GREATER_THAN.getComparatorString(), GREATER_THAN);
        compStringToComp.put(LESS_THAN_OR_EQUAL_TO.getComparatorString(),
                LESS_THAN_OR_EQUAL_TO);
        compStringToComp.put(GREATER_THAN_OR_EQUAL_TO.getComparatorString(),
                GREATER_THAN_OR_EQUAL_TO);
        compStringToComp.put(UNKNOWN.getComparatorString(), UNKNOWN);
    }

    /**
     * Gets the comparison object corresponding to the given string.
     * <ul>
     * <li>"&lt;" corresponds to <code>LESS_THAN</code>.</li>
     * <li>"&gt;" corresponds to <code>GREATER_THAN</code>.</li>
     * <li>"&lt;=" corresponds to <code>LESS_THAN_OR_EQUAL_TO</code>.</li>
     * <li>"&gt;=" corresponds to <code>GREATER_THAN_OR_EQUAL_TO</code>.</li>
     * <li>"=" corresponds to <code>EQUAL_TO</code>.</li>
     * <li>"!=" corresponds to <code>NOT_EQUAL_TO</code>.</li>
     * </ul>
     *
     * @param compString
     *            a <code>String</code> from the above list.
     * @return a <code>ValueComparator</code> corresponding to the given
     *         comparison string, or <code>null</code> if one of the above
     *         strings was not passed in.
     * @throws ValueComparatorFormatException
     *             if <code>compString</code> could not be parsed.
     */
    public static ValueComparator parse(String compString) {
        ValueComparator result = compStringToComp.get(compString);
        if (result == null) {
            throw new ValueComparatorFormatException();
        }
        return compStringToComp.get(compString);
    }

    private static class CompatibleTypes {
        ValueType type1;
        ValueType type2;

        CompatibleTypes(ValueType type1, ValueType type2) {
            this.type1 = type1;
            this.type2 = type2;
        }
    }

    /**
     * The string associated with this comparator object.
     */
    private final String name;

    /*
     * The types that are applicable to this comparator.
     */
    private final CompatibleTypes compatibleTypes;

    /**
     * Creates a comparison object with the given string.
     *
     * @param name
     *            a <code>String</code>.
     * @param compatibleTypes a {@link ValueFactory[]} for the types that are
     * applicable to this comparator..
     */
    private ValueComparator(String name, 
            CompatibleTypes compatibleTypes) {
        this.name = name;
        this.compatibleTypes = compatibleTypes;
    }

    /**
     * Returns whether this {@link ValueComparator} is subsumed by the
     * specified {@link ValueComparator}.
     *
     * @param comparator a {@link ValueComparator}.
     * @return <code>true</code> if this {@link ValueComparator} is subsumed
     * by the specified {@link ValueComparator}, <code>false</code> otherwise.
     */
    public abstract boolean subsumes(ValueComparator comparator);

    /**
     * Returns whether this {@link ValueComparator} is compatible with the
     * specified values.
     *
     * @param value1 the {@link Value} preceding the comparator.
     * @param value2 the {@link Value} following the comparator.
     * @return <code>true</code> if this {@link ValueComparator} is
     * compatible with the specified values, <code>false</code> if not.
     */
    public final boolean isCompatible(Value value1, Value value2) {
        if (value1 == null)
            throw new IllegalArgumentException("value1 cannot be null");
        if (value2 == null)
            throw new IllegalArgumentException("value2 cannot be null");

        return this.compatibleTypes.type1.isInstance(value1) &&
                this.compatibleTypes.type2.isInstance(value2);
    }

    /**
     * Gets the string associated with this comparator object.
     *
     * @return a {@link String}.
     */
    public final String getComparatorString() {
        return name;
    }
}
