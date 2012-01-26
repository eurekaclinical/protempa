/**
 * 
 */
package org.protempa.xml;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.mvel.ConversionException;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeDayUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Convert {@link Unit} objects to/from a string.
 * 
 * @author mgrand
 */
class UnitValueConverter implements SingleValueConverter {
	private static final DualHashBidiMap unitToStringMap = new DualHashBidiMap();
	static {
		unitToStringMap.put(AbsoluteTimeUnit.SECOND, "absoluteSecond");
		unitToStringMap.put(AbsoluteTimeUnit.MINUTE, "absoluteMinute");
		unitToStringMap.put(AbsoluteTimeUnit.HOUR,   "absoluteHour");
		unitToStringMap.put(AbsoluteTimeUnit.DAY,    "absoluteDay");
		unitToStringMap.put(AbsoluteTimeUnit.WEEK,   "absoluteWeek");
		unitToStringMap.put(AbsoluteTimeUnit.MONTH,  "absoluteMonth");
		unitToStringMap.put(AbsoluteTimeUnit.YEAR,   "absoluteYear");
		unitToStringMap.put(RelativeDayUnit.DAY,     "relativeDay");
		unitToStringMap.put(RelativeHourUnit.HOUR,   "relativeHour");
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Unit.class.isAssignableFrom(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Object obj) {
		String unitString = (String)unitToStringMap.get(obj);
		if (unitString == null) {
			String msg = "Unable to convert unexpected Unit object to an unit attribute value: " + obj.toString();
			throw new ConversionException(msg);
		}
		return unitString;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#fromString(java.lang.String)
	 */
	@Override
	public Object fromString(String str) {
		Unit unit = (Unit)unitToStringMap.getKey(str);
		if (unit == null) {
			String msg = "Unknown unit value string: " + str;
			throw new ConversionException(msg);
		}
		return unit;
	}

}
