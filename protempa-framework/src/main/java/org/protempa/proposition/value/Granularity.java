package org.protempa.proposition.value;

import java.io.Serializable;
import java.text.Format;

/**
 * The units for a proposition's temporal or spatial position, represented as
 * the distance from some origin. Each unit has a "length" in an implied "base"
 * unit. For example, for units representing absolute time, the base unit could
 * be milliseconds since the epoch, and each unit would have a length in
 * milliseconds. It extends {@link Serializable} because
 * {@link org.protempa.proposition.Proposition}s are
 * serializable.
 * 
 * @author Andrew Post
 */
public interface Granularity extends Comparable<Granularity>, Serializable {

    /**
     * Returns the granularity's plural name.
     *
     * @return a {@link String}
     */
    String getPluralName();

    /**
     * Returns the granularity's singular name.
     *
     * @return a {@link String}
     */
    String getName();

    /**
     * Returns a shorter version of the granularity's name.
     *
     * @return a {@link String}
     */
    String getAbbrevatedName();

    Format getLongFormat();

    Format getMediumFormat();

    Format getShortFormat();

    long earliest(long pos);

    long latest(long pos);

    long minimumDistance(long position, long distance, Unit distanceUnit);

    long maximumDistance(long position, long distance, Unit distanceUnit);

    /**
     * Calculates the distance between two positions.
     *
     * @param start
     * @param startGranularity
     * @param finish
     * @param finishGranularity
     * @return
     */
    long distance(long start, long finish, Granularity finishGranularity,
            Unit distanceUnit);

    Unit getCorrespondingUnit();
}
