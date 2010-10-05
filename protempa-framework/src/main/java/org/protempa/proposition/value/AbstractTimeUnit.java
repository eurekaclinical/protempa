package org.protempa.proposition.value;

import java.text.Format;

/**
 * Base class for defining time units.
 * 
 * @author Andrew Post
 */
public abstract class AbstractTimeUnit implements Unit {

    private transient String name;
    private transient String pluralName;
    private transient String abbreviation;
    private transient long length;
    private transient int calUnits;
    private transient Format shortFormat;
    private transient Format mediumFormat;
    private transient Format longFormat;

    AbstractTimeUnit(String name, String pluralName, String abbreviation,
            String shortFormat, String mediumFormat,
            String longFormat, long length, int calUnits) {
        this.name = name;
        this.pluralName = pluralName;
        this.abbreviation = abbreviation;
        this.length = length;
        this.calUnits = calUnits;
        this.shortFormat = new RelativeTimeUnitFormat(this, length,
                shortFormat);
        this.mediumFormat = new RelativeTimeUnitFormat(this, length,
                mediumFormat);
        this.longFormat = new RelativeTimeUnitFormat(this, length, longFormat);
    }

    @Override
    public String getPluralName() {
        return pluralName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbbreviatedName() {
        return abbreviation;
    }

    protected long getLength() {
        return length;
    }

    /**
     * The equivalent units in {@link java.util.Calendar}.
     *
     * @return a unit <code>int</code>
     */
    public int getCalendarUnits() {
        return this.calUnits;
    }

    @Override
    public Format getLongFormat() {
        return this.longFormat;
    }

    @Override
    public Format getMediumFormat() {
        return this.mediumFormat;
    }

    @Override
    public Format getShortFormat() {
        return this.shortFormat;
    }



    /**
     * Returns the unit's name.
     *
     * @see java.lang.Object#toString()
     * @see #getName()
     */
    @Override
    public String toString() {
        return name;
    }
}
