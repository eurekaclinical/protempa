/**
 * 
 */
package org.protempa.xml;

import java.util.ArrayList;

import org.protempa.query.And;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert String array object to/from XML <keyIDs></keyIDs>
 * 
 * @author mgrand
 */
class TermIDsConverter extends AbstractConverter {

	/**
	 * Constructor
	 */
	public TermIDsConverter() {
		super();
	}

	/**
	 * This converter is intended to be explicitly called from other converters
	 * as it corresponds to nothing more specific than an array of AND objects.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return String[].class.equals(clazz);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		@SuppressWarnings("unchecked")
		And<String>[] termIds = (And<String>[]) value;
		for (And<String> and : termIds) {
			context.convertAnother(and);
		}
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
		ArrayList<And<String>> keyIdList = new ArrayList<And<String>>();
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			expect(reader, "and");
			keyIdList.add(unmarshallAnd(context));
			reader.moveUp();
		}
		return keyIdList.toArray(new String[keyIdList.size()]);
	}

	@SuppressWarnings("unchecked")
	private And<String> unmarshallAnd(UnmarshallingContext context) {
		return (And<String>)context.convertAnother(null, And.class);
	}

}
