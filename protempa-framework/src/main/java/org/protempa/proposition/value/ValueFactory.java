package org.protempa.proposition.value;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for factories that create <code>Value</code>s.
 * 
 * @author Andrew Post
 */
public class ValueFactory implements Serializable {

	private static final long serialVersionUID = 6429331726781423177L;

	public static final ValueFactory VALUE = new ValueFactory("VALUE");

	public static final ValueFactory NOMINAL = new NominalValueFactory(
			"NOMINALVALUE");

	public static final ValueFactory BOOLEAN = new BooleanValueFactory(
			"BOOLEANVALUE");

	public static final ValueFactory INEQUALITY = new InequalityNumberValueFactory(
			"INEQUALITYNUMBERVALUE");

	public static final ValueFactory NUMERICAL = new NumericalValueFactory(
			"NUMERICALVALUE");

	public static final ValueFactory ORDINAL = new OrdinalValueFactory(
			"ORDINALVALUE");

	public static final ValueFactory NUMBER = new NumberValueFactory(
			"NUMBERVALUE");

	public static final ValueFactory LIST = new ListValueFactory("LIST");

	private static int nextOrdinal = 0;

	private static final ValueFactory[] VALUES = { VALUE, NOMINAL, BOOLEAN,
			INEQUALITY, NUMERICAL, ORDINAL, NUMBER, LIST };

	private static final Map<String, ValueFactory> STR_TO_VALUE_FACTORY = new HashMap<String, ValueFactory>();
	static {
		STR_TO_VALUE_FACTORY.put(VALUE.toString(), VALUE);
		STR_TO_VALUE_FACTORY.put(NOMINAL.toString(), NOMINAL);
		STR_TO_VALUE_FACTORY.put(BOOLEAN.toString(), BOOLEAN);
		STR_TO_VALUE_FACTORY.put(INEQUALITY.toString(), INEQUALITY);
		STR_TO_VALUE_FACTORY.put(NUMERICAL.toString(), NUMERICAL);
		STR_TO_VALUE_FACTORY.put(ORDINAL.toString(), ORDINAL);
		STR_TO_VALUE_FACTORY.put(NUMBER.toString(), NUMBER);
		STR_TO_VALUE_FACTORY.put(LIST.toString(), LIST);
	}

	public static Set<String> allowedStr() {
		return STR_TO_VALUE_FACTORY.keySet();
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
	public static ValueFactory toValueFactory(String valueFactoryStr) {
		return STR_TO_VALUE_FACTORY.get(valueFactoryStr);
	}

	private int ordinal = nextOrdinal++;

	private String id;

	protected ValueFactory(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * Creates a <code>Value</code> instance by parsing the given string.
	 * 
	 * @param val
	 *            a <code>String</code>.
	 * @return a <code>Value</code>, or <code>null</code> if the given
	 *         string could not be parsed.
	 */
	public Value getInstance(String val) {
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
	public final Value parseRepr(String repr) {
		if (repr == null) {
			return null;
		}
		String[] reprSplit = repr.split(":", 2);
		ValueFactory vf = STR_TO_VALUE_FACTORY.get(reprSplit[0]);
		if (getClass().isInstance(vf)) {
			return vf.getInstance(reprSplit[1]);
		} else {
			throw new IllegalArgumentException("invalid repr for "
					+ this.getClass());
		}
	}

	/**
	 * Quick replacement for <code>Class.getInstance()</code>.
	 * 
	 * @param val
	 *            a <code>Value</code>.
	 * @return <code>true</code> if <code>value</code> is an instance of
	 *         this class.
	 * @see java.lang.Class#isInstance(Object)
	 */
	public boolean isInstance(Value val) {
		if (val == null) {
			return false;
		} else {
			Class<? extends Value> c = val.getClass();
			return c.equals(NumberValue.class) || c.equals(NominalValue.class)
					|| c.equals(OrdinalValue.class)
					|| c.equals(InequalityNumberValue.class);
		}
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
		return this.id;
	}
}
