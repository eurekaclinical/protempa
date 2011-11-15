/**
 * 
 */
package org.protempa.xml;

import java.util.Date;
import java.util.TimeZone;

import com.thoughtworks.xstream.converters.basic.DateConverter;

/**
 * Convert between Java's time expressed as milliseconds and an XML data string.
 * @author mgrand
 */
class MillisecondsValueConverter extends DateConverter {

	/**
	 * 
	 */
	public MillisecondsValueConverter() {
		super();
	}

	/**
	 * @param timeZone
	 */
	public MillisecondsValueConverter(TimeZone timeZone) {
		super(timeZone);
	}

	/**
	 * @param lenient
	 */
	public MillisecondsValueConverter(boolean lenient) {
		super(lenient);
	}

	/**
	 * @param defaultFormat
	 * @param acceptableFormats
	 */
	public MillisecondsValueConverter(String defaultFormat, String[] acceptableFormats) {
		super(defaultFormat, acceptableFormats);
	}

	/**
	 * @param defaultFormat
	 * @param acceptableFormats
	 * @param timeZone
	 */
	public MillisecondsValueConverter(String defaultFormat, String[] acceptableFormats, TimeZone timeZone) {
		super(defaultFormat, acceptableFormats, timeZone);
	}

	/**
	 * @param defaultFormat
	 * @param acceptableFormats
	 * @param lenient
	 */
	public MillisecondsValueConverter(String defaultFormat, String[] acceptableFormats, boolean lenient) {
		super(defaultFormat, acceptableFormats, lenient);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public MillisecondsValueConverter(String arg0, String[] arg1, TimeZone arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * Acknowledge that this object can convert Long objects.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Long.class.equals(type);
	}

	@Override
	public Object fromString(String s) {
		Date date = (Date)super.fromString(s);
		return new Long(date.getTime());
	}

	@Override
	public String toString(Object obj) {
		Long time = (Long)obj;
		Date date = new Date(time);
		return super.toString(date);
	}
}
