package org.protempa.proposition.value;

/**
 * Access to the granularity of data provided by a data source.
 * 
 * @author Andrew Post
 * @see org.protempa.DataSource#getGranularityFactory()
 */
public interface GranularityFactory {

	/**
	 * Translates the name of a granularity unit into a {@link Granularity}
	 * object.
	 * 
	 * @param name
	 *            the name {@link String} of a granularity unit.
	 * @return the {@link Granularity} corresponding to the provided name, or
	 *         <code>null</code> if <code>name</code> is <code>null</code>
	 *         or no {@link Granularity} could be found.
	 */
	Granularity toGranularity(String name);
}
