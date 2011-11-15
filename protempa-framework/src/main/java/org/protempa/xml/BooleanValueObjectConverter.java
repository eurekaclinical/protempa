/**
 * 
 */
package org.protempa.xml;

import org.protempa.proposition.value.BooleanValue;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert a BooleanValue object to/from XML
 * @author mgrand
 */
class BooleanValueObjectConverter implements Converter {
	private final BooleanConverter bconv = new BooleanConverter();
	
	public BooleanValueObjectConverter() {
		super();
	}

	/**
	 * Determine this class can convert the given object.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz == BooleanValue.class;
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		BooleanValue bval = (BooleanValue)value;
		writer.setValue(bconv.toString(bval.getBoolean()));
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
		Boolean bool = (Boolean)bconv.fromString(reader.getValue());
		return new BooleanValue(bool);
	}
}
