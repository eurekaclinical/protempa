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
public class RelativeDayGranularity implements Granularity {

	private static final long serialVersionUID = -6754830065091052862L;

	private static final ResourceBundle resourceBundle = ValueUtil
			.resourceBundle();

	private static String[] ABBREV_NAMES = { resourceBundle
			.getString("rel_time_field_abbrev_day") };

	private static String[] NAMES = { resourceBundle
			.getString("rel_time_field_singular_day") };

	private static final String[] PLURAL_NAMES = { resourceBundle
			.getString("rel_time_field_plural_day") };

	private static final String[] longRelativeTimeFormats = { resourceBundle
			.getString("long_rel_time_format_gran_day") };

	private static final String[] mediumRelativeTimeFormats = { resourceBundle
			.getString("med_rel_time_format_gran_day") };

	private static String[] shortRelativeTimeFormats = { resourceBundle
			.getString("short_rel_time_format_gran_day") };

	private static final int[] CALENDAR_TIME_UNITS = { Calendar.DATE };

	public static final RelativeDayGranularity DAY = new RelativeDayGranularity(
			NAMES[0], PLURAL_NAMES[0], ABBREV_NAMES[0],
			shortRelativeTimeFormats[0], mediumRelativeTimeFormats[0],
			longRelativeTimeFormats[0], 1L, CALENDAR_TIME_UNITS[0]);

	private static final RelativeDayGranularity[] VALUES = new RelativeDayGranularity[] { DAY };

	private static int nextOrdinal = 0;

	private transient final String pluralName;

	private transient final String name;

	private transient final String abbreviation;

	private transient final Format longFormat;

	private transient final Format mediumFormat;

	private transient final Format shortFormat;

	private transient final Format reprFormat;

	private transient long length;

	private transient int calUnits;

	private int ordinal = nextOrdinal++;

	private RelativeDayGranularity(String name, String pluralName,
			String abbreviation, String shortFormat, String mediumFormat,
			String longFormat, long length, int calUnits) {
		this.name = name;
		this.pluralName = pluralName;
		this.abbreviation = abbreviation;
		this.length = length;

		// Needs to be at end of initialization so that the other fields are
		// set.
		this.shortFormat = new RelativeTimeGranularityFormat(this, 1L,
				shortFormat);
		this.mediumFormat = new RelativeTimeGranularityFormat(this, 1L,
				mediumFormat);
		this.longFormat = new RelativeTimeGranularityFormat(this, 1L,
				longFormat);
		this.reprFormat = new RelativeTimeGranularityFormat(this, 1L,
				shortFormat, false, "RELATIVE:");
		this.calUnits = calUnits;
	}

	public String getPluralName() {
		return this.pluralName;
	}

	public String getName() {
		return this.name;
	}

	public String getAbbrevatedName() {
		return this.abbreviation;
	}

	public Format getLongFormat() {
		return this.longFormat;
	}

	public Format getMediumFormat() {
		return this.mediumFormat;
	}

	public Format getShortFormat() {
		return this.shortFormat;
	}

	public Format getReprFormat() {
		return this.reprFormat;
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

	public long earliest(long pos) {
		return pos;
	}

	public long latest(long pos) {
		return pos + this.length - 1;
	}

	public long maximumDistance(long position, long distance, Unit distanceUnit) {
		long d = distance * this.length;
		return distance == 0 ? 0 : d + this.length - 1;
	}

	public long minimumDistance(long position, long distance, Unit distanceUnit) {
		long d = distance * this.length;
		return distance == 0 ? 0 : d - this.length + 1;
	}

	public long distance(long start, long finish,
			Granularity finishGranularity, Unit distanceUnit) {
		return (finish - start) / this.length;
	}

	public int compareTo(Granularity o) {
		@SuppressWarnings("unused")
		RelativeDayGranularity rdg = (RelativeDayGranularity) o;
		return 0;
	}
	
	public Unit getCorrespondingUnit() {
		return RelativeDayUnit.DAY;
	}

}
