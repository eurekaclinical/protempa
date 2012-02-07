/**
 * 
 */
package org.protempa.xml;

import java.io.BufferedWriter;
import java.net.URL;

import org.mvel.ConversionException;
import org.protempa.query.handler.TableQueryResultsHandler;
import org.protempa.query.handler.table.TableColumnSpec;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert Protempa Query object to/from XML
 * 
 * @author mgrand
 */
class TableQueryResultshandlerConverter extends AbstractConverter {
	private static final String TABLE_QUERY_RESULTS_HANDLER = "tableQueryResultsHandler";

	public static URL querySchemaUrl = null;
	//private static Logger myLogger = Logger.getLogger(TableQueryResultshandlerConverter.class.getName());
	
	private static final String TABLE_COLUMN_SPECS = "tableColumnSpecs";
	
	private final TableColumnSpecsConverter columnSpecsConverter = new TableColumnSpecsConverter();
	
	/**
	 * Constructor
	 */
	public TableQueryResultshandlerConverter() {
		super();
	}

	/**
	 * Determine this class can convert the given object.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return TableQueryResultsHandler.class.equals(clazz);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		// Reference Schema
		writer.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		if (!XMLConfiguration.isSurpressSchemaReferenceRequested()) {
		writer.addAttribute("xsi:noNamespaceSchemaLocation", getTableQueryResultsHandlerUrl().toExternalForm());
		}

		TableQueryResultsHandler resultsHandler = (TableQueryResultsHandler)value;
		
		writer.addAttribute("columnDelimiter", Character.toString(resultsHandler.getColumnDelimiter()));

		String[] propIDs = resultsHandler.getRowPropositionIds();
		PropIDsConverter propIDsConverter = new PropIDsConverter();
		writer.startNode("rowPropositionIDs");
		context.convertAnother(propIDs, propIDsConverter);
		propIDsConverter.marshal(propIDs, writer, context);
		writer.endNode();

		TableColumnSpec[] tableColumnSpecs = resultsHandler.getColumnSpecs();
		writer.startNode(TABLE_COLUMN_SPECS);
		context.convertAnother(tableColumnSpecs, columnSpecsConverter);
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
		if (!TABLE_QUERY_RESULTS_HANDLER.equals(reader.getNodeName())) {
			String msg = "Current element tag is " + reader.getNodeName() + " but expected " + TABLE_QUERY_RESULTS_HANDLER;
			throw new ConversionException(msg);
		}
		String columnDelimiter = requiredAttributeValue(reader, "columnDelimiter");
		expectChildren(reader);

		reader.moveDown();
		expect(reader, "rowPropositionIDs");
		PropIDsConverter propIDsConverter = new PropIDsConverter();
		String[] propIds = (String[]) context.convertAnother(null, String[].class, propIDsConverter);
		reader.moveUp();

		reader.moveDown();
		expect(reader, TABLE_COLUMN_SPECS);
		TableColumnSpec[] tableColumnSpecs = (TableColumnSpec[]) context.convertAnother(null, TableColumnSpec[].class, columnSpecsConverter);
		reader.moveUp();
		
		expectNoMore(reader);

		BufferedWriter dataWriter = (BufferedWriter)context.get("writer");
		return new TableQueryResultsHandler(dataWriter, columnDelimiter.charAt(0), propIds, tableColumnSpecs, true);
	}
	
	URL getTableQueryResultsHandlerUrl() {
		return getUrl("tableQueryResultsHandler.url");
	}
}
