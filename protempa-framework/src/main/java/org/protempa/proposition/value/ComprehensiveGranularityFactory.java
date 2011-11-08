package org.protempa.proposition.value;

/**
 * Factory for creating {@link Granularity} objects from their name when you
 * don't know what kind of <tt>Granularity</tt> object is wanted. It consults
 * the type-specific {@link GranularityFactory} classes in no guaranteed order
 * to resolve a given name to a <code>Granularity</code> object.
 * 
 * @author mgrand
 */
public class ComprehensiveGranularityFactory implements GranularityFactory {
	public static final AbsoluteTimeGranularityFactory absolute = new AbsoluteTimeGranularityFactory();
	public static final RelativeDayGranularityFactory day = new RelativeDayGranularityFactory();
	public static final RelativeHourGranularityFactory hour = new RelativeHourGranularityFactory();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.protempa.proposition.value.GranularityFactory#toGranularity(java.
	 * lang.String)
	 */
	@Override
	public Granularity toGranularity(String name) {
		Granularity g = absolute.toGranularity(name);
		if (g != null) {
			return g;
		}
		g = day.toGranularity(name);
		if (g != null) {
			return g;
		}
		return hour.toGranularity(name);
	}

}
