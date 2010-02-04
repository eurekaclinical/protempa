package org.protempa.proposition.value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Post
 */
abstract class ValueImpl implements Value {

	private static final long serialVersionUID = -502865387002393093L;

	private static interface ValueComparatorClass {
		ValueComparator compare(ValueImpl val1, Value val2);
	}

	private static class NumberValueComparatorClass implements
			ValueComparatorClass {

		public ValueComparator compare(ValueImpl val1, Value val2) {
			return val1.compareNumberValue((NumberValue) val2);
		}

	}

	private static class InequalityNumberValueComparatorClass implements
			ValueComparatorClass {

		public ValueComparator compare(ValueImpl val1, Value val2) {
			return val1
					.compareInequalityNumberValue((InequalityNumberValue) val2);
		}

	}

	private static class NominalValueComparatorClass implements
			ValueComparatorClass {

		public ValueComparator compare(ValueImpl val1, Value val2) {
			return val1.compareNominalValue((NominalValue) val2);
		}

	}

	private static class OrdinalValueComparatorClass implements
			ValueComparatorClass {

		public ValueComparator compare(ValueImpl val1, Value val2) {
			return val1.compareOrdinalValue((OrdinalValue) val2);
		}

	}

	private static class BooleanValueComparatorClass implements
			ValueComparatorClass {

		public ValueComparator compare(ValueImpl val1, Value val2) {
			return val1.compareBooleanValue((BooleanValue) val2);
		}

	}

	private static final Map<Class<? extends Value>, ValueComparatorClass> classSpecificCompares = new HashMap<Class<? extends Value>, ValueComparatorClass>();
	static {
		classSpecificCompares.put(NumberValue.class,
				new NumberValueComparatorClass());
		classSpecificCompares.put(InequalityNumberValue.class,
				new InequalityNumberValueComparatorClass());
		classSpecificCompares.put(NominalValue.class,
				new NominalValueComparatorClass());
		classSpecificCompares.put(OrdinalValue.class,
				new OrdinalValueComparatorClass());
		classSpecificCompares.put(BooleanValue.class,
				new BooleanValueComparatorClass());
	}

	private final ValueFactory valueFactory;

	/**
	 * 
	 */
	ValueImpl(ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}

	protected ValueComparator compareNumberValue(NumberValue val) {
		return ValueComparator.UNKNOWN;
	}

	protected ValueComparator compareInequalityNumberValue(
			InequalityNumberValue val) {
		return ValueComparator.UNKNOWN;
	}

	protected ValueComparator compareNominalValue(NominalValue val) {
		return ValueComparator.UNKNOWN;
	}

	protected ValueComparator compareOrdinalValue(OrdinalValue val) {
		return ValueComparator.UNKNOWN;
	}

	protected ValueComparator compareBooleanValue(BooleanValue val) {
		return ValueComparator.UNKNOWN;
	}

	public ValueComparator compare(Value val) {
		if (val == null) {
			return ValueComparator.UNKNOWN;
		} else {
			return classSpecificCompares.get(val.getClass()).compare(this, val);
		}
	}

	protected String reprType() {
		return reprType(this.valueFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.proposition.value.Value#getValueFactory()
	 */
	public final ValueFactory getValueFactory() {
		return this.valueFactory;
	}

	/**
	 * Returns the same string as {@link #getRepr()}.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return toString(this);
	}

	static String reprType(ValueFactory valueFactory) {
		if (valueFactory == null) {
			throw new IllegalArgumentException("valueFactory cannot be null!");
		}
		return valueFactory + ":";
	}
	
	static String toString(Value value) {
		return value.getRepr();
	}
}
