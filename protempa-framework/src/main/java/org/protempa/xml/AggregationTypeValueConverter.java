/**
 * 
 */
package org.protempa.xml;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.mvel.ConversionException;
import org.protempa.proposition.value.Unit;
import org.protempa.query.handler.table.PropositionValueColumnSpec.Type;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Convert {@link Unit} objects to/from a string.
 * 
 * @author mgrand
 */
class AggregationTypeValueConverter implements SingleValueConverter {
	private static final DualHashBidiMap aggregationTypeToStringMap = new DualHashBidiMap();
	static {
		aggregationTypeToStringMap.put(Type.MAX, "max");
		aggregationTypeToStringMap.put(Type.MIN, "min");
		aggregationTypeToStringMap.put(Type.FIRST, "first");
		aggregationTypeToStringMap.put(Type.LAST, "last");
		aggregationTypeToStringMap.put(Type.SUM, "sum");
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Type.class.isAssignableFrom(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Object obj) {
		String unitString = (String)aggregationTypeToStringMap.get(obj);
		if (unitString == null) {
			String msg = "Unable to convert unexpected Type object to an aggregationType attribute value: " + obj.toString();
			throw new ConversionException(msg);
		}
		return unitString;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#fromString(java.lang.String)
	 */
	@Override
	public Object fromString(String str) {
		Unit unit = (Unit)aggregationTypeToStringMap.getKey(str);
		if (unit == null) {
			String msg = "Unknown aggregation type value string: " + str;
			throw new ConversionException(msg);
		}
		return unit;
	}

}
