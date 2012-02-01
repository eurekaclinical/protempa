/**
 * 
 */
package org.protempa.xml;

import org.protempa.proposition.value.Unit;
import org.protempa.query.handler.table.DistanceBetweenColumnSpec;
import org.protempa.query.handler.table.Link;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert {@link DistanceBetweenColumnSpec} object to/from XML
 * <distanceBetweenColumnSpec></distanceBetweenColumnSpec>
 *
 * @author mgrand
 */
class DistanceBetweenColumnSpecConverter extends AbstractConverter {
	private static final String UNIT = "unit";
	private static final String COLUMN_NAME_PREFIX_OVERRIDE = "columnNamePrefixOverride";
	private static final String LINKS = "links";

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return DistanceBetweenColumnSpec.class.equals(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		DistanceBetweenColumnSpec columnSpec = (DistanceBetweenColumnSpec)source;

		String columnNameOverride = columnSpec.getColumnNamePrefixOverride();
		if (columnNameOverride != null) {
			writer.addAttribute(COLUMN_NAME_PREFIX_OVERRIDE, columnNameOverride);
		}
		
		Unit unit = columnSpec.getUnits();
		if (unit != null) {
			UnitValueConverter unitConverter = new UnitValueConverter();
			writer.addAttribute(UNIT, unitConverter.toString(unit));
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
		
		String unitString = requiredAttributeValue(reader, UNIT);
		Unit unit;
		if (unitString != null) {
			UnitValueConverter unitConverter = new UnitValueConverter();
			unit = (Unit)unitConverter.fromString(unitString);
		} else {
			unit = null;
		}
		
		reader.moveDown();
		expect(reader, LINKS);
		Link[] links = (Link[])context.convertAnother(null, Link[].class);
		reader.moveUp();
		expectNoMore(reader);

		return new DistanceBetweenColumnSpec(columnNameOverride, links, unit);
	}

}
