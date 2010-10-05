package org.protempa.proposition.value;

/**
 * Access to {@link RelativeHourGranularity} objects.
 * 
 * @author Andrew Post
 * 
 */
public final class RelativeHourGranularityFactory implements GranularityFactory {

    @Override
    public RelativeHourGranularity toGranularity(String id) {
        if (RelativeHourGranularity.HOUR.getName().equals(id)) {
            return RelativeHourGranularity.HOUR;
        } else {
            return null;
        }
    }
}
