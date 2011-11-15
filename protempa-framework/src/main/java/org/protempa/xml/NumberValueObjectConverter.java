/**
 * 
 */
package org.protempa.xml;

import java.math.BigDecimal;

import org.protempa.proposition.value.NumberValue;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert a NumberValue object to/from XML
 * @author mgrand
 */
class NumberValueObjectConverter implements Converter {
	private final BigDecimalConverter bdconv = new BigDecimalConverter();
	
	public NumberValueObjectConverter() {
		super();
	}

	/**
	 * Determine this class can convert the given object.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz == NumberValue.class;
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		NumberValue nval = (NumberValue)value;
		writer.setValue(bdconv.toString(nval.getBigDecimal()));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks
	 * .xstream.io.HierarchicalStreamReader,
	 * com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		BigDecimal nval = (BigDecimal)bdconv.fromString(reader.getValue());
		return NumberValue.getInstance(nval);
	}
}
