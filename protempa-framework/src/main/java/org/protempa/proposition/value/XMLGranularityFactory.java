package org.protempa.proposition.value;

/**
 * Get granularity objects using the name specified for them in the definition
 * of <code>granularityType</code> in the Protempa XML schema. Also get the XML
 * name for a given granularity object.
 * 
 * @author mgrand
 */
public class XMLGranularityFactory {

	public static Granularity xmlToGranularity(String name) {
		if ("second".equals(name)) {
			return AbsoluteTimeGranularity.SECOND;
		} else if ("minute".equals(name)) {
			return AbsoluteTimeGranularity.MINUTE;
		} else if ("hour".equals(name)) {
			return AbsoluteTimeGranularity.HOUR;
		} else if ("day".equals(name)) {
			return AbsoluteTimeGranularity.DAY;
		} else if ("month".equals(name)) {
			return AbsoluteTimeGranularity.MONTH;
		} else if ("year".equals(name)) {
			return AbsoluteTimeGranularity.YEAR;
		} else if ("relativeDay".equals(name)) {
			return RelativeDayGranularity.DAY;
		} else if ("relativeHour".equals(name)) {
			return RelativeHourGranularity.HOUR;
		}
		throw new IllegalArgumentException("Unknown granularity name: " + name);
	}
	
	public static String granularityToXml(Granularity g) {
		if (g == AbsoluteTimeGranularity.SECOND) {
			return "second";
		} else if (g == AbsoluteTimeGranularity.MINUTE) {
			return "minute";
		} else if (g == AbsoluteTimeGranularity.HOUR) {
			return "hour";
		} else if (g == AbsoluteTimeGranularity.DAY) {
			return "day";
		} else if (g == AbsoluteTimeGranularity.MONTH) {
			return "month";
		} else if (g == AbsoluteTimeGranularity.YEAR) {
			return "year";
		} else if (g == RelativeDayGranularity.DAY) {
			return "relativeDay";
		} else if (g == RelativeHourGranularity.HOUR) {
			return "relativeHour";
		}
		throw new IllegalArgumentException("Unknown granularity object");
	}
}
