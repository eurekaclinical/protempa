package org.protempa.proposition.value;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * An abstract class for factories that create <code>Value</code>s.
 * 
 * @author Andrew Post
 */
public class ValueFactory implements Serializable {

    private static final long serialVersionUID = 6429331726781423177L;
    public static final ValueFactory VALUE = new ValueFactory(
            ValueType.VALUE);
    public static final ValueFactory NOMINAL = new NominalValueFactory(
            ValueType.NOMINALVALUE);
    public static final ValueFactory BOOLEAN = new BooleanValueFactory(
            ValueType.BOOLEANVALUE);
    public static final ValueFactory INEQUALITY =
            new InequalityNumberValueFactory(
            ValueType.INEQUALITYNUMBERVALUE);
    public static final ValueFactory NUMERICAL = new NumericalValueFactory(
            ValueType.NUMERICALVALUE);
    public static final ValueFactory ORDINAL = new OrdinalValueFactory(
            ValueType.ORDINALVALUE);
    public static final ValueFactory NUMBER = new NumberValueFactory(
            ValueType.NUMBERVALUE);
    public static final ValueFactory LIST = new ListValueFactory(
            ValueType.LISTVALUE);
    private static int nextOrdinal = 0;
    private static final ValueFactory[] VALUES = {VALUE, NOMINAL, BOOLEAN,
        INEQUALITY, NUMERICAL, ORDINAL, NUMBER, LIST};
    private static final Map<ValueType, ValueFactory> VALUE_TYPE_TO_VALUE_FACTORY =
            new EnumMap<ValueType, ValueFactory>(ValueType.class);

    static {
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.VALUE, VALUE);
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.NOMINALVALUE, NOMINAL);
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.BOOLEANVALUE, BOOLEAN);
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.INEQUALITYNUMBERVALUE, INEQUALITY);
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.NUMERICALVALUE, NUMERICAL);
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.ORDINALVALUE, ORDINAL);
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.NUMBERVALUE, NUMBER);
        VALUE_TYPE_TO_VALUE_FACTORY.put(ValueType.LISTVALUE, LIST);
    }

    public static ValueType[] creatableTypes() {
        return VALUE_TYPE_TO_VALUE_FACTORY.keySet().toArray(
                new ValueType[VALUE_TYPE_TO_VALUE_FACTORY.size()]);
    }

    /**
     * Returns the value factory with the given id.
     *
     * @param valueFactoryStr
     *            a {@link ValueFactory} id {@link String}.
     * @return a {@link ValueFactory}, or <code>null</code> if
     *         <code>valueFactoryStr</code> is <code>null</code> or is not a
     *         value factory id.
     */
    public static ValueFactory get(ValueType valueType) {
        return VALUE_TYPE_TO_VALUE_FACTORY.get(valueType);
    }
    private int ordinal = nextOrdinal++;
    private ValueType type;

    ValueFactory(ValueType type) {
        assert type != null : "type cannot be null";
        this.type = type;
    }

    public ValueType getType() {
        return this.type;
    }

    /**
     * Creates a <code>Value</code> instance by parsing the given string.
     *
     * @param val
     *            a <code>String</code>.
     * @return a <code>Value</code>, or <code>null</code> if the given
     *         string could not be parsed.
     */
    public Value parseValue(String val) {
        return ValueFormat.parse(val);
    }

    /**
     * Parses strings obtained from {@link Value#getRepr()}.
     *
     * @param repr
     *            a {@link String}.
     * @throws IllegalArgumentException
     *             if the repr string is invalid.
     * @return a {@link Value}.
     */
    public static Value parseRepr(String repr) {
        if (repr == null) {
            return null;
        }
        String[] reprSplit = repr.split(":", 2);
        ValueFactory vf = VALUE_TYPE_TO_VALUE_FACTORY.get(
                ValueType.valueOf(reprSplit[0]));

        return vf.parseValue(reprSplit[1]);
    }

    /**
     * Used by built-in serialization.
     *
     * @return the unserialized object.
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return VALUES[ordinal];
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return this.type.name();
    }
}
