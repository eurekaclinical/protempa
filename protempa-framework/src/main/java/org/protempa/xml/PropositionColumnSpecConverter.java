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

import org.protempa.query.handler.table.Link;
import org.protempa.query.handler.table.OutputConfig;
import org.protempa.query.handler.table.PropositionColumnSpec;
import org.protempa.query.handler.table.ValueOutputConfig;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 *
 */
public class PropositionColumnSpecConverter extends AbstractConverter {
	private static final String OUTPUT_CONFIG = "outputConfig";
	private static final String VALUE_OUTPUT_CONFIG = "valueOutputConfig";
	private static final String PROPERTY_NAMES = "propertyNames";
	private static final String COLUMN_NAME_PREFIX_OVERRIDE = "columnNamePrefixOverride";
	private static final String LINKS = "links";
	private static final String NUM_INSTANCES = "numInstances";


	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return PropositionColumnSpec.class.equals(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PropositionColumnSpec columnSpec = (PropositionColumnSpec)source;

		String columnNameOverride = columnSpec.getColumnNamePrefixOverride();
		if (columnNameOverride != null) {
			writer.addAttribute(COLUMN_NAME_PREFIX_OVERRIDE, columnNameOverride);
		}
		writer.addAttribute(NUM_INSTANCES, Integer.toString(columnSpec.getNumInstances()));
		
		writer.startNode(PROPERTY_NAMES);
		context.convertAnother(columnSpec.getPropertyNames(), new PropertyNamesConverter());
		writer.endNode();
		
		OutputConfig outputConfig = columnSpec.getOutputConfig();
		if (outputConfig != null) {
			writer.startNode(OUTPUT_CONFIG);
			context.convertAnother(outputConfig);
			writer.endNode();
		}

		ValueOutputConfig valueOutputConfig = columnSpec.getValueOutputConfig();
		if (valueOutputConfig != null) {
			writer.startNode(VALUE_OUTPUT_CONFIG);
			context.convertAnother(valueOutputConfig);
			writer.endNode();
		}

		writer.startNode(LINKS);
		context.convertAnother(columnSpec.getLinks());
		writer.endNode();
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String columnNameOverride = reader.getAttribute(COLUMN_NAME_PREFIX_OVERRIDE);

		int numInstances = intAttributeValue(reader, NUM_INSTANCES);

		reader.moveDown();
		expect(reader, PROPERTY_NAMES);
		PropertyNamesConverter converter = new PropertyNamesConverter();
		String[] propertyNames = (String[])context.convertAnother(null, String[].class, converter);
		reader.moveUp();

		reader.moveDown();
		OutputConfig outputConfig;
		if (OUTPUT_CONFIG.equals(reader.getNodeName())) {
			outputConfig = (OutputConfig)context.convertAnother(null, OutputConfig.class);
			reader.moveUp();
			reader.moveDown();
		} else {
			outputConfig = null;
		}

		ValueOutputConfig valueOutputConfig;
		if (VALUE_OUTPUT_CONFIG.equals(reader.getNodeName())) {
			valueOutputConfig = (ValueOutputConfig)context.convertAnother(null, ValueOutputConfig.class);
			reader.moveUp();
			reader.moveDown();
		} else {
			valueOutputConfig = null;
		}

		expect(reader, LINKS);
		Link[] links = (Link[])context.convertAnother(null, Link[].class);
		reader.moveUp();
		expectNoMore(reader);

		return new PropositionColumnSpec(columnNameOverride, propertyNames, outputConfig,
	            valueOutputConfig, links, numInstances);
	}

}
