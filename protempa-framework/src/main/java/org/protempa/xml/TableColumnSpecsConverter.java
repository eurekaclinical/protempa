/**
 * 
 */
package org.protempa.xml;

import java.util.ArrayList;

import org.protempa.query.handler.table.AtLeastNColumnSpec;
import org.protempa.query.handler.table.CountColumnSpec;
import org.protempa.query.handler.table.DistanceBetweenColumnSpec;
import org.protempa.query.handler.table.PropositionColumnSpec;
import org.protempa.query.handler.table.PropositionValueColumnSpec;
import org.protempa.query.handler.table.TableColumnSpec;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert {@link TableColumnSpec} array object to/from XML <propositionIDs></propositionIDs>
 * 
 * @author mgrand
 */
class TableColumnSpecsConverter extends AbstractConverter {

	private static final String TABLE_COLUMN_SPECS = "tableColumnSpecs";

	/**
	 * Constructor
	 */
	public TableColumnSpecsConverter() {
		super();
	}

	/**
	 * This converter is intended to be explicitly called from other converters
	 * as it corresponds to nothing more specific than an array of strings..
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return TableColumnSpec[].class.equals(clazz);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		TableColumnSpec[] columnSpecs = (TableColumnSpec[]) value;
		for (TableColumnSpec columnSpec : columnSpecs) {
			if (columnSpec instanceof AtLeastNColumnSpec) {
				writer.startNode("atLeastNColumnSpec");
			} else if (columnSpec instanceof CountColumnSpec) {
				writer.startNode("countColumnSpec");
			} else if (columnSpec instanceof DistanceBetweenColumnSpec) {
				writer.startNode("distanceBetweenColumnSpec");
			} else if (columnSpec instanceof PropositionColumnSpec) {
				writer.startNode("propositionColumnSpec");
			} else if (columnSpec instanceof PropositionValueColumnSpec){
				writer.startNode("propositionValueColumnSpec");
			} else {
				throw new ConversionException("TableColumnSpecs array contains instance of unsupported class: " + columnSpec.getClass().getName());
			}
			context.convertAnother(columnSpec);
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
		expectChildren(reader);
		ArrayList<String> propIdList = new ArrayList<String>();
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			if (!TABLE_COLUMN_SPECS.equals(reader.getNodeName())) {
				throw new ConversionException("propositionIDs has a child that is not <propositionId>");
			}
			propIdList.add(reader.getValue());
			reader.moveUp();
		}
		return propIdList.toArray(new String[propIdList.size()]);
	}

}
