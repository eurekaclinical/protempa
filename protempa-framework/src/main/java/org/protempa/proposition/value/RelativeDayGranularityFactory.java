package org.protempa.proposition.value;

/**
 * Access to {@link RelativeDayGranularity} objects.
 * 
 * @author Andrew Post
 * 
 */
public final class RelativeDayGranularityFactory implements GranularityFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.proposition.value.GranularityFactory#toGranularity(java.lang.String)
	 */
	@Override
    public RelativeDayGranularity toGranularity(String name) {
		if (RelativeDayGranularity.DAY.getName().equals(name)) {
			return RelativeDayGranularity.DAY;
		} else {
			return null;
		}
	}

}
