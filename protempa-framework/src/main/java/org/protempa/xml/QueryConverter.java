/**
 * 
 */
package org.protempa.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.AlgorithmSource;
import org.protempa.KnowledgeSource;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.query.And;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert Protempa Query object to/from XML
 * 
 * @author mgrand
 */
class QueryConverter implements Converter {
	public static URL querySchemaUrl = null;
	private static Logger myLogger = Logger.getLogger(QueryConverter.class.getName());
	
	private KnowledgeSource knowledgeSource;
	private AlgorithmSource algorithmSource;
	
	/**
	 * Constructor
	 */
	public QueryConverter(KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) {
		super();
		this.knowledgeSource = knowledgeSource;
		this.algorithmSource = algorithmSource;
	}

	/**
	 * Determine this class can convert the given object.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz == Query.class;
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
		writer.addAttribute("xsi:noNamespaceSchemaLocation", getQuerySchemaUrl().toExternalForm());
		}

		Query query = (Query) value;
		// TODO marshal dataSourceBackend
		// TODO marshal knowledgeSourceBackend
		// TODO marshal algorithmSourceBackend
		// TODO marshal termSourceBackend
		String[] keyIDs = query.getKeyIds();
		if (keyIDs != null && keyIDs.length > 0) {
			KeyIDsConverter keyIDsConverter = new KeyIDsConverter();
			writer.startNode("keyIDs");
			keyIDsConverter.marshal(keyIDs, writer, context);
			writer.endNode();
		}
		String[] propIDs = query.getPropIds();
		PropIDsConverter propIDsConverter = new PropIDsConverter();
		writer.startNode("propositionIDs");
		propIDsConverter.marshal(propIDs, writer, context);
		writer.endNode();

		Filter filters = query.getFilters();
		if (filters != null) {
			FiltersConverter filtersConverter = new FiltersConverter();
			writer.startNode("filters");
			filtersConverter.marshal(filters, writer, context);
			writer.endNode();
		}

		And<String>[] termIds = query.getTermIds();
		if (termIds != null && termIds.length > 0) {
			TermIDsConverter termIDsConverter = new TermIDsConverter();
			writer.startNode("termIDs");
			termIDsConverter.marshal(termIds, writer, context);
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
		if (!reader.hasMoreChildren()) {
			missingPropIds();
		}
		reader.moveDown();
		// TODO unmarshal dataSourceBackend
		// TODO unmarshal knowledgeSourceBackend
		// TODO unmarshal algorithmSourceBackend
		// TODO unmarshal termSourceBackend

		DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder();
		
		if ("keyIDs".equals(reader.getNodeName())) {
			KeyIDsConverter keyIDsConverter = new KeyIDsConverter();
			String[] keyIds = (String[]) context.convertAnother(null, String[].class, keyIDsConverter);
			queryBuilder.setKeyIds(keyIds);
			reader.moveUp();
		}
		if (!reader.hasMoreChildren()) {
			missingPropIds();
		}
		reader.moveDown();
		if (!"propositionIDs".equals(reader.getNodeName())) {
			missingPropIds();
		}
		PropIDsConverter propIDsConverter = new PropIDsConverter();
		String[] propIds = (String[]) context.convertAnother(null, String[].class, propIDsConverter);
		queryBuilder.setPropIds(propIds);
		reader.moveUp();

		if (reader.hasMoreChildren()) {
			do { // do loop to allow break; not for iteration
				reader.moveDown();
				if (reader.getNodeName().equals("filters")) {
					FiltersConverter filtersConverter = new FiltersConverter();
					Filter filters = (Filter) context.convertAnother(null, Filter.class, filtersConverter);
					queryBuilder.setFilters(filters);
					reader.moveUp();
					if (!reader.hasMoreChildren()) {
						break;
					}
					reader.moveDown();
				}
				if (reader.getNodeName().equals("termIDs")) {
					And<String>[] termIds = unmarshalTermIds(reader, context);
					queryBuilder.setTermIds(termIds);
				}
			} while (false);
		}
		try {
			return queryBuilder.build(knowledgeSource, algorithmSource);
		} catch (QueryBuildException e) {
			myLogger.log(Level.SEVERE, "Error building query", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private And<String>[] unmarshalTermIds(HierarchicalStreamReader reader, UnmarshallingContext context) {
		TermIDsConverter termIDsConverter = new TermIDsConverter();
		return (And<String>[]) context.convertAnother(null, And[].class, termIDsConverter);
	}

	private void missingPropIds() {
		throw new ConversionException("Missing <propositionIDs> element");
	}

	/**
	 * @return the URL of the schema to use for validating the XML
	 *         representation of a query.
	 */
	static URL getQuerySchemaUrl() {
		if (querySchemaUrl==null) {
			URL propertiesUrl = QueryConverter.class.getResource("urls.properties");
			try {
				InputStream inStream = propertiesUrl.openStream();
				Properties urlProperties = new Properties();
				urlProperties.load(inStream);
				querySchemaUrl = new URL(urlProperties.getProperty("query.url"));
			} catch (IOException e) {
				throw new RuntimeException("Unexpected problem reading URL " + propertiesUrl, e);
			}
		}
		return querySchemaUrl;
	}
}
