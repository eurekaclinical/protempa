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
import java.util.ResourceBundle;

/**
 * Defines units of relative time. The <code>getName</code> method provides a
 * unique <code>String</code> for the unit. The base length is milliseconds.
 * 
 * @author Andrew Post
 */
public class RelativeHourUnit extends AbstractRelativeTimeUnit {

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
    public static final RelativeHourUnit HOUR = new RelativeHourUnit(NAMES[0],
            PLURAL_NAMES[0], ABBREV_NAMES[0], 
            shortRelativeTimeFormats[0],
            mediumRelativeTimeFormats[0], longRelativeTimeFormats[0],
            millisInHour, CALENDAR_TIME_UNITS[0]);
    private static final RelativeHourUnit[] VALUES = new RelativeHourUnit[]{HOUR};
    private static int nextOrdinal = 0;
    private int ordinal = nextOrdinal++;

    private RelativeHourUnit(String name, String pluralName,
            String abbreviation, String shortFormat,
            String mediumFormat,
            String longFormat, long length, int calUnits) {
        super(name, pluralName, abbreviation, shortFormat,
                mediumFormat, longFormat, length, calUnits);
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
    public int compareTo(Unit arg0) {
        RelativeHourUnit rtu = (RelativeHourUnit) arg0;
        return this.ordinal - rtu.ordinal;
    }
}
