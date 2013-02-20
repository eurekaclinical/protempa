/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

/**
 * Defines units of absolute time. The <code>getName</code> method provides a
 * unique <code>String</code> for the unit. The base length is milliseconds.
 * 
 * @author Andrew Post
 */
public class AbsoluteTimeUnit extends AbstractTimeUnit {

    private static final long serialVersionUID = -6754830065091052862L;
    private static final ResourceBundle resourceBundle =
            ValueUtil.resourceBundle();
    private static final String[] ABBREV_NAMES = {
        resourceBundle.getString("rel_time_field_abbrev_sec"),
        resourceBundle.getString("rel_time_field_abbrev_min"),
        resourceBundle.getString("rel_time_field_abbrev_hr"),
        resourceBundle.getString("rel_time_field_abbrev_day"),
        resourceBundle.getString("rel_time_field_abbrev_wk"),
        resourceBundle.getString("rel_time_field_abbrev_month"),
        resourceBundle.getString("rel_time_field_abbrev_yr")};
    private static final String[] NAMES = {
        resourceBundle.getString("rel_time_field_singular_sec"),
        resourceBundle.getString("rel_time_field_singular_min"),
        resourceBundle.getString("rel_time_field_singular_hr"),
        resourceBundle.getString("rel_time_field_singular_day"),
        resourceBundle.getString("rel_time_field_singular_wk"),
        resourceBundle.getString("rel_time_field_singular_month"),
        resourceBundle.getString("rel_time_field_singular_yr")};
    private static final String[] PLURAL_NAMES = {
        resourceBundle.getString("rel_time_field_plural_sec"),
        resourceBundle.getString("rel_time_field_plural_min"),
        resourceBundle.getString("rel_time_field_plural_hr"),
        resourceBundle.getString("rel_time_field_plural_day"),
        resourceBundle.getString("rel_time_field_plural_wk"),
        resourceBundle.getString("rel_time_field_plural_month"),
        resourceBundle.getString("rel_time_field_plural_yr")};
    private static final String[] longRelativeTimeFormats = {
        resourceBundle.getString("long_rel_time_format_gran_sec"),
        resourceBundle.getString("long_rel_time_format_gran_min"),
        resourceBundle.getString("long_rel_time_format_gran_hr"),
        resourceBundle.getString("long_rel_time_format_gran_day"),
        resourceBundle.getString("long_rel_time_format_gran_wk"),
        resourceBundle.getString("long_rel_time_format_gran_month"),
        resourceBundle.getString("long_rel_time_format_gran_yr")};
    private static final String[] mediumRelativeTimeFormats = {
        resourceBundle.getString("med_rel_time_format_gran_sec"),
        resourceBundle.getString("med_rel_time_format_gran_min"),
        resourceBundle.getString("med_rel_time_format_gran_hr"),
        resourceBundle.getString("med_rel_time_format_gran_day"),
        resourceBundle.getString("med_rel_time_format_gran_wk"),
        resourceBundle.getString("med_rel_time_format_gran_month"),
        resourceBundle.getString("med_rel_time_format_gran_yr")};
    private static final String[] shortRelativeTimeFormats = {
        resourceBundle.getString("short_rel_time_format_gran_sec"),
        resourceBundle.getString("short_rel_time_format_gran_min"),
        resourceBundle.getString("short_rel_time_format_gran_hr"),
        resourceBundle.getString("short_rel_time_format_gran_day"),
        resourceBundle.getString("short_rel_time_format_gran_wk"),
        resourceBundle.getString("short_rel_time_format_gran_month"),
        resourceBundle.getString("short_rel_time_format_gran_yr")};
    private static final double avgDaysInMonth = 30.4375;
    private static final long millisInSecond = 1000;
    private static final long millisInMinute = millisInSecond * 60;
    private static final long millisInHour = millisInMinute * 60;
    private static final long millisInDay = millisInHour * 24;
    private static final long millisInWeek = millisInDay * 7;
    private static final long millisInMonth = Math.round(millisInDay
            * avgDaysInMonth);
    private static final long millisInYear = millisInMonth * 12;
    private static final int[] CALENDAR_TIME_UNITS = {Calendar.SECOND,
        Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DATE,
        Calendar.WEEK_OF_MONTH, Calendar.MONTH, Calendar.YEAR};
    public static final AbsoluteTimeUnit SECOND = new AbsoluteTimeUnit(
            NAMES[0], PLURAL_NAMES[0], ABBREV_NAMES[0],
            shortRelativeTimeFormats[0], mediumRelativeTimeFormats[0],
            longRelativeTimeFormats[0], millisInSecond, CALENDAR_TIME_UNITS[0],
            true);
    public static final AbsoluteTimeUnit MINUTE = new AbsoluteTimeUnit(
            NAMES[1], PLURAL_NAMES[1], ABBREV_NAMES[1],
            shortRelativeTimeFormats[1], mediumRelativeTimeFormats[1],
            longRelativeTimeFormats[1], millisInMinute, CALENDAR_TIME_UNITS[1],
            true);
    public static final AbsoluteTimeUnit HOUR = new AbsoluteTimeUnit(NAMES[2],
            PLURAL_NAMES[2], ABBREV_NAMES[2], shortRelativeTimeFormats[2],
            mediumRelativeTimeFormats[2], longRelativeTimeFormats[2],
            millisInHour, CALENDAR_TIME_UNITS[2], true);
    public static final AbsoluteTimeUnit DAY = new AbsoluteTimeUnit(NAMES[3],
            PLURAL_NAMES[3], ABBREV_NAMES[3], shortRelativeTimeFormats[3],
            mediumRelativeTimeFormats[3], longRelativeTimeFormats[3],
            millisInDay, CALENDAR_TIME_UNITS[3], false);
    public static final AbsoluteTimeUnit WEEK = new AbsoluteTimeUnit(NAMES[4],
            PLURAL_NAMES[4], ABBREV_NAMES[4], shortRelativeTimeFormats[4],
            mediumRelativeTimeFormats[4], longRelativeTimeFormats[4],
            millisInWeek, CALENDAR_TIME_UNITS[4], false);
    public static final AbsoluteTimeUnit MONTH = new AbsoluteTimeUnit(NAMES[5],
            PLURAL_NAMES[5], ABBREV_NAMES[5], shortRelativeTimeFormats[5],
            mediumRelativeTimeFormats[5], longRelativeTimeFormats[5],
            millisInMonth, CALENDAR_TIME_UNITS[5], false);
    public static final AbsoluteTimeUnit YEAR = new AbsoluteTimeUnit(NAMES[6],
            PLURAL_NAMES[6], ABBREV_NAMES[6], shortRelativeTimeFormats[6],
            mediumRelativeTimeFormats[6], longRelativeTimeFormats[6],
            millisInYear, CALENDAR_TIME_UNITS[6], false);
    private static final AbsoluteTimeUnit[] VALUES = new AbsoluteTimeUnit[]{
        SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR};
    private static int nextOrdinal = 0;
    private static final Calendar testLeapSecondCal = Calendar.getInstance();
    private static boolean fastDurationCalcsEnabled;

    static {
        if (testLeapSecondCal instanceof GregorianCalendar
                && testLeapSecondCal.getMaximum(Calendar.SECOND) == 59) {
            fastDurationCalcsEnabled = true;
            ValueUtil.logger().fine("Fast duration calculations enabled");
        } else {
            ValueUtil.logger().fine("Exact duration calulations enabled.");
        }
    }

    public static AbsoluteTimeUnit nameToUnit(String name) {
        int pos = validateName(name);
        if (pos == -1) {
            return null;
        } else {
            return VALUES[pos];
        }
    }

    private static int validateName(String unitString) {
        int pos = -1;
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(unitString)) {
                pos = i;
                break;
            }
        }

        return pos;
    }
    private transient final Calendar cal;
    private transient final boolean isUsingFastDurationCalculations;
    private int ordinal = nextOrdinal++;

    private AbsoluteTimeUnit(String name, String pluralName,
            String abbreviation, String shortFormat, String mediumFormat,
            String longFormat, long length, int calUnits,
            boolean canUseFastDistanceCalcs) {
        super(name, pluralName, abbreviation, shortFormat,
                mediumFormat, longFormat, length, calUnits);

        this.cal = Calendar.getInstance();

        if (fastDurationCalcsEnabled) {
            this.isUsingFastDurationCalculations = canUseFastDistanceCalcs;
        } else {
            this.isUsingFastDurationCalculations = false;
        }
    }

    boolean isUsingFastDurationCalculations() {
        return this.isUsingFastDurationCalculations;
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

    public Unit getUnits() {
        return this;
    }

    @Override
    public int compareTo(Unit arg0) {
        AbsoluteTimeUnit rtu = (AbsoluteTimeUnit) arg0;
        return this.ordinal - rtu.ordinal;
    }

    @Override
    public long addToPosition(long position, int duration) {
        if (this.isUsingFastDurationCalculations) {
            return position + duration * getLength();
        } else {
            synchronized (this.cal) {
                this.cal.setTimeInMillis(position);
                this.cal.add(getCalendarUnits(), duration);
                return this.cal.getTimeInMillis();
            }
        }
    }
}
