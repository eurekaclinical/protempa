package org.protempa.proposition.value;

import java.io.Serializable;

/**
 * A value (number, string, category, etc.).
 * 
 * @author Andrew Post
 */
public interface Value extends Serializable {
	/**
	 * Returns a string representing this value for display purposes. This is
	 * NOT guaranteed to be a 100% faithful representation of this object's
	 * exact value (e.g., it could round to some significant digits).
	 * 
	 * @return a {@link String}.
	 */
	String getFormatted();

	/**
	 * Returns the canonical string representation of this value.
	 * 
	 * @return a {@link String}.
	 */
	String getRepr();

	/**
	 * Returns whether the given value is greater, less than, or equal to this
	 * value.
	 * 
	 * @param val
	 *            a {@link Value}. If <code>null</code>,
	 *            {@link ValueComparator#UNKNOWN} is returned.
	 * @return a {@link ValueComparator indicating that the given value is
	 *         greater, less than, or equal to this value.
	 */
	ValueComparator compare(Value val);

	/**
	 * Returns a string description of this object's type, guaranteed not
	 * <code>null</code>.
	 * 
	 * @return a {@link ValueFactory}.
	 */
	ValueFactory getValueFactory();
}
