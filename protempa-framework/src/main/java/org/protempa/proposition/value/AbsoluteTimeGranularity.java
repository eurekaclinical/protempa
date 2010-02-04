package org.protempa.proposition.value;

import java.io.ObjectStreamException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Defines absolute time temporal granularities. The <code>getName</code>
 * method provides a unique <code>String</code> for the unit. The base length
 * is UTC milliseconds from the epoch.
 * 
 * @author Andrew Post
 */
public final class AbsoluteTimeGranularity implements Granularity {

	private static final long serialVersionUID = -4868042711375950931L;

	private static final ResourceBundle resourceBundle = ValueUtil
			.resourceBundle();

	private static String[] ABBREV_NAMES = {
			resourceBundle.getString("time_field_abbrev_sec"),
			resourceBundle.getString("time_field_abbrev_min"),
			resourceBundle.getString("time_field_abbrev_hr"),
			resourceBundle.getString("time_field_abbrev_day"),
			resourceBundle.getString("time_field_abbrev_month"),
			resourceBundle.getString("time_field_abbrev_yr") };

	private static String[] NAMES = {
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

	private static final DateFormat[] longDateFormats = {
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_sec")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_min")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_hr")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_day")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_month")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_yr")) };

	private static final DateFormat[] reprFormats = {
			new ReprSimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_sec")),
			new ReprSimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_min")),
			new ReprSimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_hr")),
			new ReprSimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_day")),
			new ReprSimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_month")),
			new ReprSimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_yr")) };

	private static final DateFormat[] longDateFormatsNoYear = {
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_sec_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_min_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_hr_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_day_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_month_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("long_date_format_gran_yr_no_yr")) };

	private static final DateFormat[] mediumDateFormats = {
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_sec")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_min")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_hr")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_day")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_month")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_yr")) };

	private static final DateFormat[] mediumDateFormatsNoYear = {
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_sec_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_min_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_hr_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_day_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_month_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("med_date_format_gran_yr_no_yr")) };

	private static DateFormat[] shortDateFormats = {
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_sec")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_min")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_hr")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_day")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_month")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_yr")) };

	private static DateFormat[] shortDateFormatsNoYear = {
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_sec_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_min_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_hr_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_day_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_month_no_yr")),
			new SimpleDateFormat(resourceBundle
					.getString("short_date_format_gran_yr_no_yr")) };

	private static DateFormat[] timeFormats = {
			new SimpleDateFormat(resourceBundle
					.getString("time_format_gran_sec")),
			new SimpleDateFormat(resourceBundle
					.getString("time_format_gran_min")),
			new SimpleDateFormat(resourceBundle
					.getString("time_format_gran_hr")),
			new SimpleDateFormat(resourceBundle
					.getString("time_format_gran_hr")),
			new SimpleDateFormat(resourceBundle
					.getString("time_format_gran_hr")),
			new SimpleDateFormat(resourceBundle
					.getString("time_format_gran_hr")),
			new SimpleDateFormat(resourceBundle
					.getString("time_format_gran_hr")) };

	private static final int[] CALENDAR_TIME_UNITS = { Calendar.MILLISECOND,
			Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY,
			Calendar.DATE, Calendar.MONTH, Calendar.YEAR };

	public static final AbsoluteTimeGranularity SECOND = new AbsoluteTimeGranularity(
			ABBREV_NAMES[0], NAMES[0], PLURAL_NAMES[0], 1, longDateFormats[0],
			longDateFormatsNoYear[0], mediumDateFormats[0],
			mediumDateFormatsNoYear[0], shortDateFormats[0],
			shortDateFormatsNoYear[0], timeFormats[0], reprFormats[0],
			AbsoluteTimeUnit.SECOND);

	public static final AbsoluteTimeGranularity MINUTE = new AbsoluteTimeGranularity(
			ABBREV_NAMES[1], NAMES[1], PLURAL_NAMES[1], 2, longDateFormats[1],
			longDateFormatsNoYear[1], mediumDateFormats[1],
			mediumDateFormatsNoYear[1], shortDateFormats[1],
			shortDateFormatsNoYear[1], timeFormats[1], reprFormats[1],
			AbsoluteTimeUnit.MINUTE);

	public static final AbsoluteTimeGranularity HOUR = new AbsoluteTimeGranularity(
			ABBREV_NAMES[2], NAMES[2], PLURAL_NAMES[2], 3, longDateFormats[2],
			longDateFormatsNoYear[2], mediumDateFormats[2],
			mediumDateFormatsNoYear[2], shortDateFormats[2],
			shortDateFormatsNoYear[2], timeFormats[2], reprFormats[2],
			AbsoluteTimeUnit.HOUR);

	public static final AbsoluteTimeGranularity DAY = new AbsoluteTimeGranularity(
			ABBREV_NAMES[3], NAMES[3], PLURAL_NAMES[3], 4, longDateFormats[3],
			longDateFormatsNoYear[3], mediumDateFormats[3],
			mediumDateFormatsNoYear[3], shortDateFormats[3],
			shortDateFormatsNoYear[3], timeFormats[3], reprFormats[3],
			AbsoluteTimeUnit.DAY);

	public static final AbsoluteTimeGranularity MONTH = new AbsoluteTimeGranularity(
			ABBREV_NAMES[4], NAMES[4], PLURAL_NAMES[4], 5, longDateFormats[4],
			longDateFormatsNoYear[4], mediumDateFormats[4],
			mediumDateFormatsNoYear[4], shortDateFormats[4],
			shortDateFormatsNoYear[4], timeFormats[4], reprFormats[4],
			AbsoluteTimeUnit.MONTH);

	public static final AbsoluteTimeGranularity YEAR = new AbsoluteTimeGranularity(
			ABBREV_NAMES[5], NAMES[5], PLURAL_NAMES[5], 6, longDateFormats[5],
			longDateFormatsNoYear[5], mediumDateFormats[5],
			mediumDateFormatsNoYear[5], shortDateFormats[5],
			shortDateFormatsNoYear[5], timeFormats[5], reprFormats[5],
			AbsoluteTimeUnit.YEAR);

	private static final AbsoluteTimeGranularity[] VALUES = { SECOND, MINUTE,
			HOUR, DAY, MONTH, YEAR };

	private static int nextOrdinal = 0;

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

	private transient final DateFormat longDateFormat;

	private transient final DateFormat longDateFormatNoYear;

	private transient final DateFormat mediumDateFormat;

	private transient final DateFormat mediumDateFormatNoYear;

	private transient final DateFormat shortDateFormat;

	private transient final DateFormat shortDateFormatNoYear;

	private transient final DateFormat timeFormat;

	private transient final DateFormat reprFormat;

	private transient final Calendar earliestCal;

	private transient final Calendar latestCal;

	private transient final Calendar minDistCal;

	private transient final Calendar maxDistCal;

	private transient final Calendar distToCal;

	private transient final Unit correspondingUnit;

	private int ordinal = nextOrdinal++;

	public AbsoluteTimeGranularity(String abbrev, String name,
			String pluralName, int calUnitIndex, DateFormat longDateFormat,
			DateFormat longDateFormatNoYear, DateFormat mediumDateFormat,
			DateFormat mediumDateFormatNoYear, DateFormat shortDateFormat,
			DateFormat shortDateFormatNoYear, DateFormat timeFormat,
			DateFormat reprFormat, Unit correspondingUnit) {
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
	public String getPluralName() {
		return this.pluralName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Unit#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Unit#getAbbrevation()
	 */
	public String getAbbrevatedName() {
		return this.abbrev;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public Format getReprFormat() {
		return this.reprFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Unit#getLongFormat()
	 */
	public Format getLongFormat() {
		return this.longDateFormat;
	}

	public DateFormat getLongDateFormatNoYear() {
		return this.longDateFormatNoYear;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Unit#getMediumFormat()
	 */
	public Format getMediumFormat() {
		return this.mediumDateFormat;
	}

	public DateFormat getMediumDateFormatNoYear() {
		return this.mediumDateFormatNoYear;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virginia.pbhs.parameters.Unit#getShortFormat()
	 */
	public Format getShortFormat() {
		return this.shortDateFormat;
	}

	public DateFormat getShortDateFormatNoYear() {
		return this.shortDateFormatNoYear;
	}

	public DateFormat getTimeFormat() {
		return this.timeFormat;
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

	public long earliest(long pos) {
		synchronized (this.earliestCal) {
			this.earliestCal.setTimeInMillis(pos);
			zeroCalendar(this.earliestCal);
			return this.earliestCal.getTimeInMillis();
		}
	}

	public long latest(long pos) {
		synchronized (this.latestCal) {
			this.latestCal.setTimeInMillis(pos);
			this.latestCal.add(CALENDAR_TIME_UNITS[this.calUnitIndex], 1);
			this.latestCal.add(CALENDAR_TIME_UNITS[0], -1);
			return this.latestCal.getTimeInMillis();
		}
	}

	public long maximumDistance(long position, long distance, Unit distanceUnit) {
		if (distance == 0)
			return 0L;
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

	public long minimumDistance(long position, long distance, Unit distanceUnit) {
		if (distance == 0)
			return 0L;
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
		for (int i = this.calUnitIndex - 1; i >= 0; i--)
			cal.set(CALENDAR_TIME_UNITS[i], cal
					.getActualMinimum(CALENDAR_TIME_UNITS[i]));
	}

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

	public int compareTo(Granularity o) {
		AbsoluteTimeGranularity other = (AbsoluteTimeGranularity) o;
		return this.ordinal - other.ordinal;
	}

	public Unit getCorrespondingUnit() {
		return this.correspondingUnit;
	}

}