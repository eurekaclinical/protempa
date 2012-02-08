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

import org.protempa.query.handler.table.AtLeastNColumnSpec;
import org.protempa.query.handler.table.Link;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
* Convert {@link AtLeastNColumnSpec} object to/from XML
 * <atLeastNColumnSpec></atLeastNColumnSpec>
 *
 * @author mgrand
 */
class AtLeastNColumnSpecConverter extends AbstractConverter {

	private static final String FALSE_OUTPUT = "falseOutput";
	private static final String TRUE_OUTPUT = "trueOutput";
	private static final String COLUMN_NAME_OVERRIDE = "columnNameOverride";
	private static final String LINKS = "links";
	private static final String N = "n";

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return AtLeastNColumnSpec.class.equals(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		AtLeastNColumnSpec columnSpec = (AtLeastNColumnSpec)source;

		String columnNameOverride = columnSpec.getColumnNameOverride();
		if (columnNameOverride != null) {
			writer.addAttribute(COLUMN_NAME_OVERRIDE, columnNameOverride);
		}
		writer.addAttribute(N, Integer.toString(columnSpec.getN()));
		writer.addAttribute(TRUE_OUTPUT, columnSpec.getTrueOutput());
		writer.addAttribute(FALSE_OUTPUT, columnSpec.getFalseOutput());

		writer.startNode(LINKS);
		context.convertAnother(columnSpec.getLinks());
		writer.endNode();
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String columnNameOverride = reader.getAttribute(COLUMN_NAME_OVERRIDE);

		int n = intAttributeValue(reader, N);
		
		String trueOutput = reader.getAttribute(TRUE_OUTPUT);
		if (trueOutput == null) {
			trueOutput = "true";
		}
		
		String falseOutput = reader.getAttribute(FALSE_OUTPUT);
		if (falseOutput == null) {
			falseOutput = "false";
		}
		
		reader.moveDown();
		expect(reader, LINKS);
		Link[] links = (Link[])context.convertAnother(null, Link[].class);
		reader.moveUp();
		expectNoMore(reader);

		return new AtLeastNColumnSpec(columnNameOverride, n, links, trueOutput, falseOutput);
	}

}
