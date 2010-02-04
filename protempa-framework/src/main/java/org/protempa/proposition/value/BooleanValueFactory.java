package org.protempa.proposition.value;

/**
 * A factory for creating <code>BooleanValue</code> objects.
 * 
 * @author Andrew Post
 */
public final class BooleanValueFactory extends ValueFactory {

	private static final long serialVersionUID = -4164229803309514271L;

	/**
	 * Package-private constructor (use the constants defined in
	 * <code>ValueFactory</code>.
	 */
	BooleanValueFactory(String str) {
		super(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
	 */
	@Override
	public Value getInstance(String val) {
		if ("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) {
			return Boolean.valueOf(val).booleanValue() ? BooleanValue.TRUE
					: BooleanValue.FALSE;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.value.ValueFactory#isInstance(org.virginia.pbhs.parameters.value.Value)
	 */
	@Override
	public boolean isInstance(Value val) {
		if (val == null) {
			return false;
		} else {
			return val.getClass().equals(BooleanValue.class);
		}
	}

}
