package org.protempa.proposition.value;

import java.io.Serializable;
import java.text.Format;

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

    Format getShortFormat();

    Format getMediumFormat();

    Format getLongFormat();

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
