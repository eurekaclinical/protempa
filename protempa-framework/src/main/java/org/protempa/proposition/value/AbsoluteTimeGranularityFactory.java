package org.protempa.proposition.value;

/**
 * Access to {@link AbsoluteTimeGranularity} objects.
 * 
 * @author Andrew Post
 * 
 */
public final class AbsoluteTimeGranularityFactory implements GranularityFactory {
	
	public AbsoluteTimeGranularity toGranularity(String name) {
		return AbsoluteTimeGranularity.nameToGranularity(name);
	}

}
