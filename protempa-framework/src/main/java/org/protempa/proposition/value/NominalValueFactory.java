package org.protempa.proposition.value;

/**
 * A factory for creating <code>NominalValue</code> objects.
 * 
 * @author Andrew Post
 */
public final class NominalValueFactory extends ValueFactory {

	private static final long serialVersionUID = 9116626325566355765L;

	/**
	 * Package-private constructor (use the constants defined in
	 * <code>ValueFactory</code>.
	 */
	NominalValueFactory(String str) {
		super(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
	 */
	@Override
	public Value getInstance(String val) {
		return new NominalValue(val);
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
			return val.getClass().equals(NominalValue.class);
		}
	}

}
