package org.protempa.proposition.value;

import java.math.BigDecimal;

/**
 * A factory for creating <code>NumberValue</code> objects.
 * 
 * @author Andrew Post
 */
public final class NumberValueFactory extends NumericalValueFactory {

	private static final long serialVersionUID = 5725140172150432391L;

	/**
	 * Package-private constructor (use the constants defined in
	 * <code>ValueFactory</code>.
	 */
	NumberValueFactory(String str) {
		super(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.value.ValueFactory#getInstance(java.lang.String)
	 */
	@Override
	public Value getInstance(String val) {
		if (val != null) {
			try {
				return new NumberValue(new BigDecimal(val.trim()));
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return null;
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
			return val.getClass().equals(NumberValue.class);
		}
	}

}
