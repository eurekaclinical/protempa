/**
 * 
 */
package org.protempa.xml;

import org.protempa.proposition.value.InequalityNumberValue;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert a InequalityNumberValue object to/from XML
 * @author mgrand
 */
class InequalityValueObjectConverter extends AbstractConverter {
	
	public InequalityValueObjectConverter() {
		super();
	}

	/**
	 * Determine this class can convert the given object.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz == InequalityNumberValue.class;
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		InequalityNumberValue ival = (InequalityNumberValue)value;
		writer.setValue(ival.getComparator().getComparatorString() + ival.getBigDecimal().toPlainString());
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
		InequalityNumberValue ival = InequalityNumberValue.parse(reader.getValue());
		return ival;
	}
}
