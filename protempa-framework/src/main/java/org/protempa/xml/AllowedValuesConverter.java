/**
 * 
 */
package org.protempa.xml;

import java.util.ArrayList;

import org.mvel.ConversionException;
import org.protempa.proposition.value.Value;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 * 
 */
public class AllowedValuesConverter extends AbstractConverter {

	private static final String ALLOWED_VALUES = "allowedValues";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
	 * lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Value[].class.equals(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 * com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Value[] values = (Value[]) source;
		if (values == null || values.length == 0) {
			return;
		}
		Class<?> valueClass = values[0].getClass();
		writer.startNode(ALLOWED_VALUES);
		for (Value value : values) {
			if (value.getClass() != valueClass) {
				String msg = "Cannot convert allowed values to XML for mixed types of values that include " //
					+ value.getClass().getName() + " and " + valueClass.getName();
				throw new ConversionException(msg);
			}
			valueToXML(writer, context, value);
		}
		writer.endNode();
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
		ArrayList<Value> valueList = new ArrayList<Value>();
		while (reader.hasMoreChildren()) {
			valueList.add((Value)valueFromXML(reader, context)); 
		}
		return valueList.toArray(new Value[valueList.size()]);
	}

}
