/**
 * 
 */
package org.protempa.xml;

import org.protempa.query.handler.table.CountColumnSpec;
import org.protempa.query.handler.table.Link;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 * 
 */
class CountColumnSpecConverter extends AbstractConverter {
	private static final String COUNT_UNIQUE = "countUnique";
	private static final String COLUMN_NAME_OVERRIDE = "columnNameOverride";
	private static final String LINKS = "links";

	/*
	 * (non-Javadoc) Convert {@link CountColumnSpec} object to/from XML
	 * <countColumnSpec></countColumnSpec>
	 * 
	 * @author mgrand
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return CountColumnSpec.class.equals(type);
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
		CountColumnSpec columnSpec = (CountColumnSpec)source;

		String columnNameOverride = columnSpec.getColumnNameOverride();
		if (columnNameOverride != null) {
			writer.addAttribute(COLUMN_NAME_OVERRIDE, columnNameOverride);
		}
		writer.addAttribute(COUNT_UNIQUE, Boolean.toString(columnSpec.isCountUnique()));

		writer.startNode(LINKS);
		context.convertAnother(columnSpec.getLinks());
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
		String columnNameOverride = reader.getAttribute(COLUMN_NAME_OVERRIDE);
		
		String countUniqueString = requiredAttributeValue(reader, COUNT_UNIQUE);
		boolean countUnique = Boolean.valueOf(countUniqueString);
		
		reader.moveDown();
		expect(reader, LINKS);
		Link[] links = (Link[])context.convertAnother(null, Link[].class);
		reader.moveUp();
		expectNoMore(reader);

		return new CountColumnSpec(columnNameOverride, links, countUnique);
	}

}
