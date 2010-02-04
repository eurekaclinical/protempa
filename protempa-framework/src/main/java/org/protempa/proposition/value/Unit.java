package org.protempa.proposition.value;

import java.io.Serializable;

/**
 * The units for a length. It extends {@link Serializable} so that it can be
 * stored as part of {@link org.protempa.proposition.Proposition}
 * s.
 * 
 * @author Andrew Post
 */
public interface Unit extends Serializable, Comparable<Unit> {

	/**
	 * Returns the unit's singular name.
	 * 
	 * @return a {@link String}
	 */
	String getName();

	/**
	 * Returns a short version of the unit's singular name.
	 * 
	 * @return a {@link String}
	 */
	String getAbbreviatedName();

	/**
	 * Returns the unit's plural name.
	 * 
	 * @return a {@link String}
	 */
	String getPluralName();

	/**
	 * Returns this unit's length in the base unit. If the unit is not always
	 * the same length (e.g., days in time units), the average length is
	 * returned. This method is deprecated because time units may have variable
	 * length depending upon the year and other factors.
	 * 
	 * @return a <code>long</code>
	 */
	@Deprecated
	long getLength();

	/**
	 * Adds a length in these units to the given position.
	 * 
	 * @param position
	 *            a <code>long</code>
	 * @param length
	 *            an <code>int</code>
	 * @return the new position <code>long</code>
	 */
	long addToPosition(long position, int length);

}