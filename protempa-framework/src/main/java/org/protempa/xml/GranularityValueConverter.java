/**
 * 
 */
package org.protempa.xml;

import org.mvel.ConversionException;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.RelativeDayGranularity;
import org.protempa.proposition.value.RelativeHourGranularity;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Convert {@link Granularity} objects to/from a string.
 * 
 * @author mgrand
 */
public class GranularityValueConverter implements SingleValueConverter {
	private final static String SECOND = "second";
	private final static String MINUTE = "minute";
	private final static String HOUR = "hour";
	private final static String DAY = "day";
	private final static String MONTH = "month";
	private final static String YEAR = "year";
	private final static String RELATIVE_DAY = "relativeDay";
	private final static String RELATIVE_HOUR = "relativeHour";

	/*
	 * Acknowledge that this object can convert {@link Granularity} objects.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return Granularity.class.isAssignableFrom(clazz);
	}

	/*
	 * Convert a string used to represent a granularity value by the Proptema
	 * Schema's granularityType to teh equivalent granularity object.
	 */
	@Override
	public Object fromString(String s) {
		if (SECOND.equals(s)) {
			return AbsoluteTimeGranularity.SECOND;
		} else if (MINUTE.equals(s)) {
			return AbsoluteTimeGranularity.MINUTE;
		} else if (HOUR.equals(s)) {
			return AbsoluteTimeGranularity.HOUR;
		} else if (DAY.equals(s)) {
			return AbsoluteTimeGranularity.DAY;
		} else if (MONTH.equals(s)) {
			return AbsoluteTimeGranularity.MONTH;
		} else if (YEAR.equals(s)) {
			return AbsoluteTimeGranularity.YEAR;
		} else if (RELATIVE_DAY.equals(s)) {
			return RelativeDayGranularity.DAY;
		} else if (RELATIVE_HOUR.equals(s)) {
			return RelativeHourGranularity.HOUR;
		}
		throw new ConversionException("Unrecognized granularity value: " + s);
	}

	/*
	 * Convert a Granularity object to the equivalent string used to represent
	 * in as defined by Protempa's XML schema's granularityType.
	 */
	@Override
	public String toString(Object obj) {
		if (obj == AbsoluteTimeGranularity.SECOND) {
			return SECOND;
		} else if (obj == AbsoluteTimeGranularity.MINUTE) {
			return MINUTE;
		} else if (obj == AbsoluteTimeGranularity.HOUR) {
			return HOUR;
		} else if (obj == AbsoluteTimeGranularity.DAY) {
			return DAY;
		} else if (obj == AbsoluteTimeGranularity.MONTH) {
			return MONTH;
		} else if (obj == AbsoluteTimeGranularity.YEAR) {
			return YEAR;
		} else if (obj == RelativeDayGranularity.DAY) {
			return DAY;
		} else if (obj == RelativeHourGranularity.HOUR) {
			return HOUR;
		}

		// TODO Auto-generated method stub
		throw new ConversionException("Encountered granularity object with no known string representation: " + obj);
	}

}
