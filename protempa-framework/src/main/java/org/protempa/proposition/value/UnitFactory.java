package org.protempa.proposition.value;

/**
 * Access to the length units of data provided by a data source.
 * 
 * @author Andrew Post
 * @see org.protempa.DataSource#getUnitFactory()
 */
public interface UnitFactory {
	/**
	 * Translates the name of a length unit into a {@link Unit} object
	 * 
	 * @param name
	 *            the name {@link String} of a length unit.
	 * @return the {@link Unit} corresponding to the provided name, or
	 *         <code>null</code> if <code>name</code> is <code>null</code>
	 *         or no {@link Unit} could be found.
	 */
	Unit toUnit(String name);
}
