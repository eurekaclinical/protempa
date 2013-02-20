/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.xml;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert String array object to/from XML. The tag for the nested elements is
 * specified by the constructor. This converter is not specific to any one class
 * and so it should be be registered as the converter for any specific class.
 * Instead, its instances should be explicitly passed into the context's
 * convertAnother method.
 * 
 * @author mgrand
 */
class StringArrayConverter extends AbstractConverter {
	private String elementName;

	/**
	 * Constructor
	 * 
	 * @param elementName
	 *            The name of the elements that are nested elements
	 *            corresponding to array elements.
	 */
	public StringArrayConverter(String elementName) {
		this.elementName = elementName;
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
		String[] keyIDs = (String[]) value;
		for (String keyID : keyIDs) {
			writer.startNode(elementName);
			writer.setValue(keyID);
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
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			expect(reader, elementName);
			keyIdList.add(reader.getValue());
			reader.moveUp();
		}
		return keyIdList.toArray(new String[keyIdList.size()]);
	}

}
