package org.protempa.proposition.value;

/**
 * Numerical values.
 * 
 * @author Andrew Post
 */
public interface NumericalValue extends Value {
	/**
	 * Gets this value as a number.
	 * 
	 * @return a {@link Number}.
	 */
	Number getNumber();

	/**
	 * Gets this value as a double.
	 * 
	 * @return a {@link double}.
	 */
	double doubleValue();
}
