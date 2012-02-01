/**
 * 
 */
package org.protempa.xml;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.mvel.ConversionException;
import org.protempa.proposition.value.Unit;
import org.protempa.query.handler.table.Derivation;
import org.protempa.query.handler.table.Derivation.Behavior;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Convert {@link Unit} objects to/from a string.
 * 
 * @author mgrand
 */
class BehaviorValueConverter implements SingleValueConverter {
	private static final DualHashBidiMap BehaviorToStringMap = new DualHashBidiMap();
	static {
		BehaviorToStringMap.put(Derivation.Behavior.MULT_BACKWARD, "multBackward");
		BehaviorToStringMap.put(Derivation.Behavior.MULT_FORWARD, "multForward");
		BehaviorToStringMap.put(Derivation.Behavior.SINGLE_BACKWARD, "singleBackward");
		BehaviorToStringMap.put(Derivation.Behavior.SINGLE_FORWARD, "singleForward");
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Behavior.class.isAssignableFrom(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Object obj) {
		String unitString = (String)BehaviorToStringMap.get(obj);
		if (unitString == null) {
			String msg = "Unable to convert unexpected Behavior object to an attribute value: " + obj.toString();
			throw new ConversionException(msg);
		}
		return unitString;
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#fromString(java.lang.String)
	 */
	@Override
	public Object fromString(String str) {
		Unit unit = (Unit)BehaviorToStringMap.getKey(str);
		if (unit == null) {
			String msg = "Unknown behavior type value string: " + str;
			throw new ConversionException(msg);
		}
		return unit;
	}

}
