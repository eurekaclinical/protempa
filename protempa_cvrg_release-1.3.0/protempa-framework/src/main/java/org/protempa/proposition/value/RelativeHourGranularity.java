/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.proposition.value;

import java.io.ObjectStreamException;
import java.text.Format;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Defines units of relative time. The <code>getName</code> method provides a
 * unique <code>String</code> for the unit. The base length is milliseconds.
 * 
 * @author Andrew Post
 */
public class RelativeHourGranularity implements Granularity {

    private static final long serialVersionUID = -6754830065091052862L;
    private static final ResourceBundle resourceBundle = ValueUtil.resourceBundle();
    private static String[] ABBREV_NAMES = {resourceBundle.getString("rel_time_field_abbrev_hr")};
    private static String[] NAMES = {resourceBundle.getString("rel_time_field_singular_hr")};
    private static final String[] PLURAL_NAMES = {resourceBundle.getString("rel_time_field_plural_hr")};
    private static final String[] longRelativeTimeFormats = {resourceBundle.getString("long_rel_time_format_gran_hr")};
    private static final String[] mediumRelativeTimeFormats = {resourceBundle.getString("med_rel_time_format_gran_hr")};
    private static String[] shortRelativeTimeFormats = {resourceBundle.getString("short_rel_time_format_gran_hr")};
    private static final long millisInSecond = 1000;
    private static final long millisInMinute = millisInSecond * 60;
    private static final long millisInHour = millisInMinute * 60;
    private static final int[] CALENDAR_TIME_UNITS = {Calendar.HOUR_OF_DAY};
    public static final RelativeHourGranularity HOUR = new RelativeHourGranularity(
            NAMES[0], PLURAL_NAMES[0], ABBREV_NAMES[0],
            shortRelativeTimeFormats[0], mediumRelativeTimeFormats[0],
            longRelativeTimeFormats[0], millisInHour, CALENDAR_TIME_UNITS[0]);
    private static final RelativeHourGranularity[] VALUES = new RelativeHourGranularity[]{HOUR};
    private static int nextOrdinal = 0;
    private transient final String pluralName;
    private transient final String name;
    private transient final String abbreviation;
    private transient final Format longFormat;
    private transient final Format mediumFormat;
    private transient final Format shortFormat;
    private transient long length;
    private transient int calUnits;
    private int ordinal = nextOrdinal++;

    private RelativeHourGranularity(String name, String pluralName,
            String abbreviation, String shortFormat, String mediumFormat,
            String longFormat, long length, int calUnits) {
        this.name = name;
        this.pluralName = pluralName;
        this.abbreviation = abbreviation;
        this.length = length;

        // Needs to be at end of initialization so that the other fields are
        // set.
        this.shortFormat = new RelativeTimeGranularityFormat(this,
                millisInHour, shortFormat);
        this.mediumFormat = new RelativeTimeGranularityFormat(this,
                millisInHour, mediumFormat);
        this.longFormat = new RelativeTimeGranularityFormat(this, millisInHour,
                longFormat);
        this.calUnits = calUnits;
    }

    @Override
    public String getPluralName() {
        return this.pluralName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAbbrevatedName() {
        return this.abbreviation;
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

    public long getLength() {
        return this.length;
    }

    public int getCalendarUnits() {
        return this.calUnits;
    }

    public long lengthInBaseUnit(long length) {
        return length * this.length;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Used by built-in serialization.
     *
     * @return the unserialized object.
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return VALUES[ordinal];
    }

    @Override
    public long earliest(long pos) {
        return pos;
    }

    @Override
    public long latest(long pos) {
        return pos + this.length - 1;
    }

    @Override
    public long maximumDistance(long position, long distance, Unit distanceUnit) {
        long d = distance * this.length;
        return distance == 0 ? 0 : d + this.length - 1;
    }

    @Override
    public long minimumDistance(long position, long distance, Unit distanceUnit) {
        long d = distance * this.length;
        return distance == 0 ? 0 : d - this.length + 1;
    }

    @Override
    public long distance(long start, long finish,
            Granularity finishGranularity, Unit distanceUnit) {
        return (finish - start) / this.length;
    }

    @Override
    public int compareTo(Granularity o) {
        @SuppressWarnings("unused")
        RelativeHourGranularity rdg = (RelativeHourGranularity) o;
        return 0;
    }

    @Override
    public Unit getCorrespondingUnit() {
        return RelativeHourUnit.HOUR;
    }

	@Override
	public int hashCode() {
		return ordinal;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelativeHourGranularity other = (RelativeHourGranularity) obj;
		if (ordinal != other.ordinal)
			return false;
		return true;
	}
    
}
