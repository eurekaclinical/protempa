package org.protempa.proposition.value;

/**
 * A factory for creating <code>NumericalValue</code> objects.
 * 
 * @author Andrew Post
 */
public class NumericalValueFactory extends ValueFactory {
	
	private static final long serialVersionUID = 3863928880426027508L;

	/**
	 * Package-private constructor (use the constants defined in
	 * <code>ValueFactory</code>.
	 */
	NumericalValueFactory(String str) {
		super(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
	 */
	@Override
	public Value getInstance(String val) {
		Value result = NUMBER.getInstance(val);
		if (result == null) {
			return INEQUALITY.getInstance(val);
		} else {
			return result;
		}
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
			Class c = val.getClass();
			return c.equals(NumberValue.class)
					|| c.equals(InequalityNumberValue.class);
		}
	}

}
