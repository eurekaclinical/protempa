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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Defines absolute time temporal granularities. The <code>getName</code> method
 * provides a unique <code>String</code> for the unit. The base length is UTC
 * milliseconds from the epoch.
 * 
 * @author Andrew Post
 */
public final class AbsoluteTimeGranularity implements Granularity {

    private static final long serialVersionUID = -4868042711375950931L;
    private static final ResourceBundle resourceBundle = ValueUtil
            .resourceBundle();
    private static final String[] ABBREV_NAMES = {
            resourceBundle.getString("time_field_abbrev_sec"),
            resourceBundle.getString("time_field_abbrev_min"),
            resourceBundle.getString("time_field_abbrev_hr"),
            resourceBundle.getString("time_field_abbrev_day"),
            resourceBundle.getString("time_field_abbrev_month"),
            resourceBundle.getString("time_field_abbrev_yr") };
    private static final String[] NAMES = {
            resourceBundle.getString("time_field_singular_sec"),
            resourceBundle.getString("time_field_singular_min"),
            resourceBundle.getString("time_field_singular_hr"),
            resourceBundle.getString("time_field_singular_day"),
            resourceBundle.getString("time_field_singular_month"),
            resourceBundle.getString("time_field_singular_yr") };
    private static final String[] PLURAL_NAMES = {
            resourceBundle.getString("time_field_plural_sec"),
            resourceBundle.getString("time_field_plural_min"),
            resourceBundle.getString("time_field_plural_hr"),
            resourceBundle.getString("time_field_plural_day"),
            resourceBundle.getString("time_field_plural_month"),
            resourceBundle.getString("time_field_plural_yr") };

    // private static final DateFormat[] longDateFormats = {
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_sec")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_min")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_hr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_day")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_month")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_yr"))};
    private static final List<ThreadLocal<DateFormat>> longDateFormats = new ArrayList<>();
    static {
        longDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_sec"));
            }
        });
        longDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_min"));

            }
        });
        longDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_hr"));
            }
        });
        longDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_day"));
            }
        });
        longDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_month"));
            }
        });
        longDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_yr"));
            }
        });
    }

    // private static final DateFormat[] reprFormats = {
    // new
    // ReprSimpleDateFormat(resourceBundle.getString("long_date_format_gran_sec")),
    // new
    // ReprSimpleDateFormat(resourceBundle.getString("long_date_format_gran_min")),
    // new
    // ReprSimpleDateFormat(resourceBundle.getString("long_date_format_gran_hr")),
    // new
    // ReprSimpleDateFormat(resourceBundle.getString("long_date_format_gran_day")),
    // new
    // ReprSimpleDateFormat(resourceBundle.getString("long_date_format_gran_month")),
    // new
    // ReprSimpleDateFormat(resourceBundle.getString("long_date_format_gran_yr"))};
    private static final List<ThreadLocal<DateFormat>> reprFormats = new ArrayList<>();
    static {
        reprFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new ReprSimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_sec"));
            }
        });
        reprFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new ReprSimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_min"));
            }
        });
        reprFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new ReprSimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_hr"));
            }
        });
        reprFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new ReprSimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_day"));
            }
        });
        reprFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new ReprSimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_month"));
            }
        });
        reprFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new ReprSimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_yr"));
            }
        });
    }

    // private static final DateFormat[] longDateFormatsNoYear = {
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_sec_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_min_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_hr_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_day_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_month_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("long_date_format_gran_yr_no_yr"))};
    private static final List<ThreadLocal<DateFormat>> longDateFormatsNoYear = new ArrayList<>();
    static {
        longDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_sec_no_yr"));
            }
        });
        longDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_min_no_yr"));
            }
        });
        longDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_hr_no_yr"));
            }
        });
        longDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_day_no_yr"));
            }
        });
        longDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_month_no_yr"));
            }
        });
        longDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("long_date_format_gran_yr_no_yr"));
            }
        });
    }

    // private static final DateFormat[] mediumDateFormats = {
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_sec")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_min")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_hr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_day")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_month")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_yr"))};
    private static final List<ThreadLocal<DateFormat>> mediumDateFormats = new ArrayList<>();
    static {
        mediumDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_sec"));
            }
        });
        mediumDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_min"));
            }
        });
        mediumDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_hr"));
            }
        });
        mediumDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_day"));
            }
        });
        mediumDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_month"));
            }
        });
        mediumDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_yr"));
            }
        });
    }

    // private static final DateFormat[] mediumDateFormatsNoYear = {
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_sec_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_min_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_hr_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_day_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_month_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("med_date_format_gran_yr_no_yr"))};
    private static final List<ThreadLocal<DateFormat>> mediumDateFormatsNoYear = new ArrayList<>();
    static {
        mediumDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_sec_no_yr"));
            }
        });
        mediumDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_min_no_yr"));
            }
        });
        mediumDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_hr_no_yr"));
            }
        });
        mediumDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_day_no_yr"));
            }
        });
        mediumDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_month_no_yr"));
            }
        });
        mediumDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("med_date_format_gran_yr_no_yr"));
            }
        });
    }

    // private static final DateFormat[] shortDateFormats = {
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_sec")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_min")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_hr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_day")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_month")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_yr"))};
    private static final List<ThreadLocal<DateFormat>> shortDateFormats = new ArrayList<>();
    static {
        shortDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_sec"));
            }
        });
        shortDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_min"));
            }
        });
        shortDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_hr"));
            }
        });
        shortDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_day"));
            }
        });
        shortDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_month"));
            }
        });
        shortDateFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_yr"));
            }
        });
    }

    // private static final DateFormat[] shortDateFormatsNoYear = {
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_sec_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_min_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_hr_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_day_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_month_no_yr")),
    // new
    // SimpleDateFormat(resourceBundle.getString("short_date_format_gran_yr_no_yr"))};
    private static final List<ThreadLocal<DateFormat>> shortDateFormatsNoYear = new ArrayList<>();
    static {
        shortDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_sec_no_yr"));
            }
        });
        shortDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_min_no_yr"));
            }
        });
        shortDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_hr_no_yr"));
            }
        });
        shortDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_day_no_yr"));
            }
        });
        shortDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_month_no_yr"));
            }
        });
        shortDateFormatsNoYear.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("short_date_format_gran_yr_no_yr"));
            }
        });
    }

    // private static final DateFormat[] timeFormats = {
    // new SimpleDateFormat(resourceBundle.getString("time_format_gran_sec")),
    // new SimpleDateFormat(resourceBundle.getString("time_format_gran_min")),
    // new SimpleDateFormat(resourceBundle.getString("time_format_gran_hr")),
    // new SimpleDateFormat(resourceBundle.getString("time_format_gran_hr")),
    // new SimpleDateFormat(resourceBundle.getString("time_format_gran_hr")),
    // new SimpleDateFormat(resourceBundle.getString("time_format_gran_hr")),
    // new SimpleDateFormat(resourceBundle.getString("time_format_gran_hr"))};
    private static final List<ThreadLocal<DateFormat>> timeFormats = new ArrayList<>();
    static {
        timeFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("time_format_gran_sec"));
            }
        });
        timeFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("time_format_gran_min"));
            }
        });
        timeFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("time_format_gran_hr"));
            }
        });
        timeFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("time_format_gran_hr"));
            }
        });
        timeFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("time_format_gran_hr"));
            }
        });
        timeFormats.add(new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(resourceBundle
                        .getString("time_format_gran_hr"));
            }
        });
    }

    private static final int[] CALENDAR_TIME_UNITS = { Calendar.MILLISECOND,
            Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY,
            Calendar.DATE, Calendar.MONTH, Calendar.YEAR };

    public static final AbsoluteTimeGranularity SECOND = new AbsoluteTimeGranularity(
            ABBREV_NAMES[0], NAMES[0], PLURAL_NAMES[0], 1,
            longDateFormats.get(0), longDateFormatsNoYear.get(0),
            mediumDateFormats.get(0), mediumDateFormatsNoYear.get(0),
            shortDateFormats.get(0), shortDateFormatsNoYear.get(0),
            timeFormats.get(0), reprFormats.get(0), AbsoluteTimeUnit.SECOND);
    public static final AbsoluteTimeGranularity MINUTE = new AbsoluteTimeGranularity(
            ABBREV_NAMES[1], NAMES[1], PLURAL_NAMES[1], 2,
            longDateFormats.get(1), longDateFormatsNoYear.get(1),
            mediumDateFormats.get(1), mediumDateFormatsNoYear.get(1),
            shortDateFormats.get(1), shortDateFormatsNoYear.get(1),
            timeFormats.get(1), reprFormats.get(1), AbsoluteTimeUnit.MINUTE);
    public static final AbsoluteTimeGranularity HOUR = new AbsoluteTimeGranularity(
            ABBREV_NAMES[2], NAMES[2], PLURAL_NAMES[2], 3,
            longDateFormats.get(2), longDateFormatsNoYear.get(2),
            mediumDateFormats.get(2), mediumDateFormatsNoYear.get(2),
            shortDateFormats.get(2), shortDateFormatsNoYear.get(2),
            timeFormats.get(2), reprFormats.get(2), AbsoluteTimeUnit.HOUR);
    public static final AbsoluteTimeGranularity DAY = new AbsoluteTimeGranularity(
            ABBREV_NAMES[3], NAMES[3], PLURAL_NAMES[3], 4,
            longDateFormats.get(3), longDateFormatsNoYear.get(3),
            mediumDateFormats.get(3), mediumDateFormatsNoYear.get(3),
            shortDateFormats.get(3), shortDateFormatsNoYear.get(3),
            timeFormats.get(3), reprFormats.get(3), AbsoluteTimeUnit.DAY);
    public static final AbsoluteTimeGranularity MONTH = new AbsoluteTimeGranularity(
            ABBREV_NAMES[4], NAMES[4], PLURAL_NAMES[4], 5,
            longDateFormats.get(4), longDateFormatsNoYear.get(4),
            mediumDateFormats.get(4), mediumDateFormatsNoYear.get(4),
            shortDateFormats.get(4), shortDateFormatsNoYear.get(4),
            timeFormats.get(4), reprFormats.get(4), AbsoluteTimeUnit.MONTH);
    public static final AbsoluteTimeGranularity YEAR = new AbsoluteTimeGranularity(
            ABBREV_NAMES[5], NAMES[5], PLURAL_NAMES[5], 6,
            longDateFormats.get(5), longDateFormatsNoYear.get(5),
            mediumDateFormats.get(5), mediumDateFormatsNoYear.get(5),
            shortDateFormats.get(5), shortDateFormatsNoYear.get(5),
            timeFormats.get(5), reprFormats.get(5), AbsoluteTimeUnit.YEAR);
    private static final AbsoluteTimeGranularity[] VALUES = { SECOND, MINUTE,
            HOUR, DAY, MONTH, YEAR };
    private static volatile int nextOrdinal = 0;

    /**
     * Convenience method to translate a timestamp into the format expected by
     * SQL.
     * 
     * @param position a date/time in milliseconds since the epoch.
     * @return a {@link String} in JDBC timestamp escape format.
     */
    public static String toSQLString(Long position) {
        java.util.Date date = AbsoluteTimeGranularityUtil.asDate(position);
        return new Timestamp(date.getTime()).toString();
    }

    public static AbsoluteTimeGranularity granularityStringToGranularity(
            String string) {
        int pos = validatePluralName(string);
        if (pos == -1) {
            return null;
        } else {
            return VALUES[pos];
        }
    }

    public static AbsoluteTimeGranularity nameToGranularity(String name) {
        int pos = validateName(name);
        if (pos == -1) {
            return null;
        } else {
            return VALUES[pos];
        }
    }

    private static int validatePluralName(String unitString) {
        int pos = -1;
        for (int i = 0; i < PLURAL_NAMES.length; i++) {
            if (PLURAL_NAMES[i].equals(unitString)) {
                pos = i;
                break;
            }
        }

        return pos;
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

    // Make all fields transient except ordinal, since we use readResolve to
    // get the static
    // member class.
    private transient final String abbrev;
    private transient final String name;
    private transient final String pluralName;
    private transient final int calUnitIndex;
    private transient final ThreadLocal<DateFormat> longDateFormat;
    private transient final ThreadLocal<DateFormat> longDateFormatNoYear;
    private transient final ThreadLocal<DateFormat> mediumDateFormat;
    private transient final ThreadLocal<DateFormat> mediumDateFormatNoYear;
    private transient final ThreadLocal<DateFormat> shortDateFormat;
    private transient final ThreadLocal<DateFormat> shortDateFormatNoYear;
    private transient final ThreadLocal<DateFormat> timeFormat;
    private transient final ThreadLocal<DateFormat> reprFormat;
    private transient final Calendar earliestCal;
    private transient final Calendar latestCal;
    private transient final Calendar minDistCal;
    private transient final Calendar maxDistCal;
    private transient final Calendar distToCal;
    private transient final Unit correspondingUnit;
    private int ordinal = nextOrdinal++;

    private AbsoluteTimeGranularity(String abbrev, String name,
            String pluralName, int calUnitIndex,
            ThreadLocal<DateFormat> longDateFormat,
            ThreadLocal<DateFormat> longDateFormatNoYear,
            ThreadLocal<DateFormat> mediumDateFormat,
            ThreadLocal<DateFormat> mediumDateFormatNoYear,
            ThreadLocal<DateFormat> shortDateFormat,
            ThreadLocal<DateFormat> shortDateFormatNoYear,
            ThreadLocal<DateFormat> timeFormat,
            ThreadLocal<DateFormat> reprFormat, Unit correspondingUnit) {
        this.abbrev = abbrev;
        this.name = name;
        this.pluralName = pluralName;
        this.calUnitIndex = calUnitIndex;
        this.longDateFormat = longDateFormat;
        this.longDateFormatNoYear = longDateFormatNoYear;
        this.mediumDateFormat = mediumDateFormat;
        this.mediumDateFormatNoYear = mediumDateFormatNoYear;
        this.shortDateFormat = shortDateFormat;
        this.shortDateFormatNoYear = shortDateFormatNoYear;
        this.timeFormat = timeFormat;
        this.reprFormat = reprFormat;
        this.earliestCal = Calendar.getInstance();
        this.latestCal = Calendar.getInstance();
        this.minDistCal = Calendar.getInstance();
        this.maxDistCal = Calendar.getInstance();
        this.distToCal = Calendar.getInstance();
        this.correspondingUnit = correspondingUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.virginia.pbhs.parameters.Unit#getPluralName()
     */
    @Override
    public String getPluralName() {
        return this.pluralName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.virginia.pbhs.parameters.Unit#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.virginia.pbhs.parameters.Unit#getAbbrevation()
     */
    @Override
    public String getAbbrevatedName() {
        return this.abbrev;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Format getReprFormat() {
        return this.reprFormat.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.virginia.pbhs.parameters.Unit#getLongFormat()
     */
    @Override
    public DateFormat getLongFormat() {
        return this.longDateFormat.get();
    }

    public DateFormat getLongDateFormatNoYear() {
        return this.longDateFormatNoYear.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.virginia.pbhs.parameters.Unit#getMediumFormat()
     */
    @Override
    public DateFormat getMediumFormat() {
        return this.mediumDateFormat.get();
    }

    public DateFormat getMediumDateFormatNoYear() {
        return this.mediumDateFormatNoYear.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.virginia.pbhs.parameters.Unit#getShortFormat()
     */
    @Override
    public DateFormat getShortFormat() {
        return this.shortDateFormat.get();
    }

    public DateFormat getShortDateFormatNoYear() {
        return this.shortDateFormatNoYear.get();
    }

    public DateFormat getTimeFormat() {
        return this.timeFormat.get();
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
        synchronized (this.earliestCal) {
            this.earliestCal.setTimeInMillis(pos);
            zeroCalendar(this.earliestCal);
            return this.earliestCal.getTimeInMillis();
        }
    }

    @Override
    public long latest(long pos) {
        synchronized (this.latestCal) {
            this.latestCal.setTimeInMillis(pos);
            this.latestCal.add(CALENDAR_TIME_UNITS[this.calUnitIndex], 1);
            this.latestCal.add(CALENDAR_TIME_UNITS[0], -1);
            return this.latestCal.getTimeInMillis();
        }
    }

    @Override
    public long maximumDistance(long position, long distance, Unit distanceUnit) {
        if (distance == 0) {
            return 0L;
        }
        synchronized (this.maxDistCal) {
            this.maxDistCal.setTimeInMillis(position);
            long initial = this.maxDistCal.getTimeInMillis();
            int calUnits;
            if (distanceUnit == null) {
                calUnits = CALENDAR_TIME_UNITS[0];
            } else {
                calUnits = ((AbsoluteTimeUnit) distanceUnit).getCalendarUnits();
            }
            for (long d = 0; d < distance; d += Integer.MAX_VALUE) {
                int dAsInt = (int) Math.min(Integer.MAX_VALUE, distance - d);
                this.maxDistCal.add(calUnits, dAsInt);
            }
            this.maxDistCal.add(calUnits, 1);
            this.maxDistCal.add(CALENDAR_TIME_UNITS[0], -1);
            return this.maxDistCal.getTimeInMillis() - initial;
        }
    }

    @Override
    public long minimumDistance(long position, long distance, Unit distanceUnit) {
        if (distance == 0) {
            return 0L;
        }
        synchronized (this.minDistCal) {
            this.minDistCal.setTimeInMillis(position);
            long initial = this.minDistCal.getTimeInMillis();
            int calUnits;
            if (distanceUnit == null) {
                calUnits = CALENDAR_TIME_UNITS[0];
            } else {
                calUnits = ((AbsoluteTimeUnit) distanceUnit).getCalendarUnits();
            }
            for (long d = 0; d < distance; d += Integer.MAX_VALUE) {
                int dAsInt = (int) Math.min(Integer.MAX_VALUE, distance - d);
                this.minDistCal.add(calUnits, +dAsInt);
            }
            this.minDistCal.add(calUnits, -1);
            this.minDistCal.add(CALENDAR_TIME_UNITS[0], 1);
            return this.minDistCal.getTimeInMillis() - initial;
        }
    }

    private void zeroCalendar(Calendar cal) {
        for (int i = this.calUnitIndex - 1; i >= 0; i--) {
            cal.set(CALENDAR_TIME_UNITS[i],
                    cal.getActualMinimum(CALENDAR_TIME_UNITS[i]));
        }
    }

    @Override
    public long distance(long start, long finish,
            Granularity finishGranularity, Unit distanceUnit) {
        if (distanceUnit == null) {
            return finish - start;
        }
        AbsoluteTimeUnit du = (AbsoluteTimeUnit) distanceUnit;
        if (du.isUsingFastDurationCalculations()) {
            return (finish - start) / du.getLength();
        } else {
            synchronized (this.distToCal) {
                this.distToCal.setTimeInMillis(start);
                int calUnits = du.getCalendarUnits();
                int returnValue = 0;
                while (true) {
                    this.distToCal.add(calUnits, 1);
                    if (this.distToCal.getTimeInMillis() > finish) {
                        break;
                    } else {
                        returnValue++;
                    }
                }
                return returnValue;
            }
        }
    }

    @Override
    public int compareTo(Granularity o) {
        AbsoluteTimeGranularity other = (AbsoluteTimeGranularity) o;
        return this.ordinal - other.ordinal;
    }

    @Override
    public Unit getCorrespondingUnit() {
        return this.correspondingUnit;
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
        AbsoluteTimeGranularity other = (AbsoluteTimeGranularity) obj;
        if (ordinal != other.ordinal)
            return false;
        return true;
    }

}
