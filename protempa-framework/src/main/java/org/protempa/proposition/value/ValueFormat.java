package org.protempa.proposition.value;

/**
 * Parses strings into PROTEMPA value objects, and formats PROTEMPA value
 * objects as strings.
 * 
 * @author Andrew Post
 */
public class ValueFormat {

    private static final ValueFactory[] PARSE_FACTORY_ORDER = {
        ValueFactory.BOOLEAN, ValueFactory.NUMBER, ValueFactory.INEQUALITY,
        ValueFactory.LIST, ValueFactory.NOMINAL};

    private ValueFormat() {
    }

    /**
     * Returns the given PROTEMPA value formatted as a string.
     *
     * @param val
     *            a {@link Value}.
     * @return a {@link String} or <code>null</code> if <code>value</code>
     *         is <code>null</code>.
     */
    public static String format(Value val) {
        if (val != null) {
            return val.getFormatted();
        } else {
            return null;
        }
    }

    /**
     * Parses a string into one of the <code>Value</code> types. The type is
     * guessed by examining the string. Note, there is no way for this method to
     * distinguish between ordinal and plain text strings, so a
     * <code>NominalValue</code> object is returned for all strings.
     *
     * @param str
     *            a <code>String</code> object.
     * @return a <code>Value</code> object. If the <code>str</code>
     *         parameter cannot be parsed, it is returned in a
     *         <code>NominalValue</code> object.
     */
    public static Value parse(String str) {
        Value result = null;

        for (int i = 0; i < PARSE_FACTORY_ORDER.length; i++) {
            if ((result = PARSE_FACTORY_ORDER[i].parseValue(str)) != null) {
                break;
            }
        }

        return result;
    }

    /**
     * Parses a value string into a PROTEMPA value object.
     *
     * @param value
     *            a value {@link String}.
     * @param valueType the value's {@link ValueType}
     * (cannot be <code>null</code>).
     * @return a {@link Value} object, or <code>null</code> if a
     *         <code>null</code> <code>value</code> parameter is passed in.
     */
    public static Value parse(String value, ValueType valueType) {
        if (valueType == null) {
            throw new IllegalArgumentException("valueType cannot be null");
        }
        if (value != null) {
            return ValueFactory.get(valueType).parseValue(value);
        } else {
            return null;
        }
    }
}
