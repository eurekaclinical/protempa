/**
 * 
 */
package org.protempa.xml;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert String array object to/from XML <keyIDs></keyIDs>
 * 
 * @author mgrand
 */
class KeyIDsConverter implements Converter {

	/**
	 * Constructor
	 */
	public KeyIDsConverter() {
		super();
	}

	/**
	 * This converter is intended to be explicitly called from other converters
	 * as it corresponds to nothing more specifiec than an array of strings..
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return false;
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		String[] keyIds = (String[]) value;
		for (String keyId : keyIds) {
			writer.startNode("keyId");
			writer.setValue(keyId);
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
		ArrayList<String> keyIdList = new ArrayList<String>();
		while(!reader.hasMoreChildren()) {
			reader.moveDown();
			if (!"keyId".equals(reader.getNodeName())) {
				throw new ConversionException("keyIDs has a child that is not <keyId>");
			}
			keyIdList.add(reader.getValue());
			reader.moveUp();
		}
		return keyIdList.toArray(new String[keyIdList.size()]);
	}

}
