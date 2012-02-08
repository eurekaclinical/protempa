/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

import org.protempa.query.handler.table.ValueOutputConfig;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 * 
 */
class ValueOutputConfigConverter extends AbstractConverter {

	private static final String PROPERTY_VALUE_ABBREV_DISPLAY_NAME = "propertyValueAbbrevDisplayName";
	private static final String PROPERTY_VALUE_DISPLAY_NAME = "propertyValueDisplayName";
	private static final String SHOW_PROPERTY_VALUE_ABBREV_DISPLAY_NAME = "showPropertyValueAbbrevDisplayName";
	private static final String SHOW_PROPERTY_VALUE_DISPLAY_NAME = "showPropertyValueDisplayName";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
	 * lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return ValueOutputConfig.class.equals(type);
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
		ValueOutputConfig valueOutputConfig = (ValueOutputConfig) source;

		writer.addAttribute(SHOW_PROPERTY_VALUE_DISPLAY_NAME, Boolean.toString(valueOutputConfig.isShowPropertyValueDisplayName()));
		writer.addAttribute(SHOW_PROPERTY_VALUE_ABBREV_DISPLAY_NAME, Boolean.toString(valueOutputConfig.isShowPropertyValueAbbrevDisplayName()));
		writer.addAttribute(PROPERTY_VALUE_DISPLAY_NAME, valueOutputConfig.getPropertyValueDisplayName());
		writer.addAttribute(PROPERTY_VALUE_ABBREV_DISPLAY_NAME, valueOutputConfig.getPropertyValueAbbrevDisplayName());
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
		boolean showPropertyValueDisplayName = Boolean.valueOf(reader.getAttribute(SHOW_PROPERTY_VALUE_DISPLAY_NAME)).booleanValue();
		boolean showPropertyValueAbbrevDisplayName = Boolean.valueOf(reader.getAttribute(SHOW_PROPERTY_VALUE_ABBREV_DISPLAY_NAME)).booleanValue();
		String propertyValueDisplayName = nullAsEmptyString(reader.getAttribute(PROPERTY_VALUE_DISPLAY_NAME));
		String propertyValueAbbrevDisplayName = nullAsEmptyString(reader.getAttribute(PROPERTY_VALUE_ABBREV_DISPLAY_NAME));

		return new ValueOutputConfig(showPropertyValueDisplayName, showPropertyValueAbbrevDisplayName, //
				propertyValueDisplayName, propertyValueAbbrevDisplayName);
	}

}
