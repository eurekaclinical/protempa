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
		super("yyyy-MM-dd'T'HH:mm:ss.S", new String[0], TimeZone.getDefault());
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
