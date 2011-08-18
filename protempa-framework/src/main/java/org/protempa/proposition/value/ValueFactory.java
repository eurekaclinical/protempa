package org.protempa.proposition.value;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * An abstract class for factories that create {@link Value}s.
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
    public static final ValueFactory DATE = new DateValueFactory(
            ValueType.DATEVALUE);
    private static int nextOrdinal = 0;
    private static final ValueFactory[] VALUES = {VALUE, NOMINAL, BOOLEAN,
        INEQUALITY, NUMERICAL, ORDINAL, NUMBER, LIST, DATE};
    
    private static final ValueFactory[] PARSE_FACTORY_ORDER = {
        ValueFactory.BOOLEAN, ValueFactory.NUMBER, ValueFactory.INEQUALITY,
        ValueFactory.DATE, ValueFactory.LIST, ValueFactory.NOMINAL};
    
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
     *            a <code>String</code>. May be <code>null</code>.
     * @return a <code>Value</code>, or <code>null</code> if the supplied
     *         string is <code>null</code> or has an invalid format.
     */
    public Value parse(String val) {
        Value result = null;

        for (int i = 0; i < PARSE_FACTORY_ORDER.length; i++) {
            if ((result = PARSE_FACTORY_ORDER[i].parse(val)) != null) {
                break;
            }
        }

        return result;
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
    
    @Override
    public final String toString() {
        return this.type.name();
    }
}
