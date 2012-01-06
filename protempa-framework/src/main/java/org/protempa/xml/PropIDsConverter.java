/**
 * 
 */
package org.protempa.xml;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert String array object to/from XML <propositionIDs></propositionIDs>
 * 
 * @author mgrand
 */
class PropIDsConverter extends AbstractConverter {

	private static final String PROPOSITION_ID = "propositionID";

	/**
	 * Constructor
	 */
	public PropIDsConverter() {
		super();
	}

	/**
	 * This converter is intended to be explicitly called from other converters
	 * as it corresponds to nothing more specific than an array of strings..
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
		String[] propIds = (String[]) value;
		for (String propId : propIds) {
			writer.startNode(PROPOSITION_ID);
			writer.setValue(propId);
			writer.endNode();
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
		ArrayList<String> propIdList = new ArrayList<String>();
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			if (!PROPOSITION_ID.equals(reader.getNodeName())) {
				throw new ConversionException("propositionIDs has a child that is not <propositionId>");
			}
			propIdList.add(reader.getValue());
			reader.moveUp();
		}
		return propIdList.toArray(new String[propIdList.size()]);
	}

}
