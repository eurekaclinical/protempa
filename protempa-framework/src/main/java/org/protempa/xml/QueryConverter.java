/**
 * 
 */
package org.protempa.xml;

import org.protempa.backend.dsb.filter.Filter;
import org.protempa.query.And;
import org.protempa.query.Query;

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
public class QueryConverter implements Converter {

	/**
	 * Constructor
	 */
	public QueryConverter() {
		super();
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

		String[] keyIds = null;
		if ("keyIDs".equals(reader.getNodeName())) {
			KeyIDsConverter keyIDsConverter = new KeyIDsConverter();
			keyIds = (String[]) keyIDsConverter.unmarshal(reader, context);
			reader.moveUp();
		}
		if (!reader.hasMoreChildren()) {
			missingPropIds();
		}
		String[] propIds = null;
		reader.moveDown();
		if (!"propositionIDs".equals(reader.getNodeName())) {
			missingPropIds();
		}
		PropIDsConverter propIDsConverter = new PropIDsConverter();
		propIds = (String[]) propIDsConverter.unmarshal(reader, context);
		reader.moveUp();

		Filter filters = null;
		And<String>[] termIds = null;
		if (reader.hasMoreChildren()) {
			do { // do loop to allow break; not for iteration
				reader.moveDown();
				if (reader.getNodeName().equals("filters")) {
					FiltersConverter filtersConverter = new FiltersConverter();
					filters = (Filter)filtersConverter.unmarshal(reader, context);
					reader.moveUp();
					if (!reader.hasMoreChildren()) {
						break;
					}
					reader.moveDown();
				}
				if (reader.getNodeName().equals("termIDs")) {
					termIds = unmarshalTermIds(reader, context);
				}
			} while (false);
		}
		return new Query(keyIds, filters, propIds, termIds);
	}

	@SuppressWarnings("unchecked")
	private And<String>[] unmarshalTermIds(HierarchicalStreamReader reader, UnmarshallingContext context) {
		TermIDsConverter termIDsConverter = new TermIDsConverter();
		return (And<String>[])termIDsConverter.unmarshal(reader, context);
	}

	private void missingPropIds() {
		throw new ConversionException("Missing <propositionIDs> element");
	}

}
