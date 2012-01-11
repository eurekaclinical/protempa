/**
 * 
 */
package org.protempa.xml;

import java.util.Date;

import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;
import org.protempa.proposition.value.Granularity;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert a Filter object to/from XML <filters></filters>
 * 
 * @author mgrand
 */
class DateTimeFilterConverter implements Converter {

	private static final String DATE_TIME_FILTER = "dateTimeFilter";
	private static final String PROPOSITION_IDS = "propositionIDs";
	private static final String FINISH_SIDE = "finishSide";
	private static final String START_SIDE = "startSide";
	private static final String FINISH_GRANULARITY = "finishGranularity";
	private static final String FINISH = "finish";
	private static final String START_GRANULARITY = "startGranularity";
	private static final String START = "start";
	
	private MillisecondsValueConverter msConverter = new MillisecondsValueConverter();

	/**
	 * Constructor
	 */
	public DateTimeFilterConverter() {
		super();
	}

	/**
	 * This converter is intended to be explicitly called from other converters
	 * as it corresponds to nothing 
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return DateTimeFilter.class.equals(clazz);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		DateTimeFilter filter = (DateTimeFilter) value;
		
		writer.addAttribute(START, msConverter.toString(filter.getStart()));
		
		GranularityValueConverter granularityConverter = new GranularityValueConverter();
		writer.addAttribute(START_GRANULARITY, granularityConverter.toString(filter.getStartGranularity()));
		
		writer.addAttribute(FINISH, msConverter.toString(filter.getFinish()));
		
		writer.addAttribute(FINISH_GRANULARITY, granularityConverter.toString(filter.getFinishGranularity()));
		
		writer.addAttribute(START_SIDE, filter.getStartSide().getXmlName());
		
		writer.addAttribute(FINISH_SIDE, filter.getFinishSide().getXmlName());
		
		writer.startNode(PROPOSITION_IDS);
		TableColumnSpecsConverter propIdsConverter = new TableColumnSpecsConverter();
		context.convertAnother(filter.getPropositionIds(), propIdsConverter);
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
		Date start = (Date)XMLConfiguration.STANDARD_DATE_CONVERTER.fromString(reader.getAttribute(START));
		
		GranularityValueConverter granularityConverter = new GranularityValueConverter();
		String startGranularityString = reader.getAttribute(START_GRANULARITY);
		Granularity startGranularity = (Granularity)granularityConverter.fromString(startGranularityString);
		
		Date finish = (Date)XMLConfiguration.STANDARD_DATE_CONVERTER.fromString(reader.getAttribute(FINISH));

		String finishGranularityString = reader.getAttribute(FINISH_GRANULARITY);
		Granularity finishGranularity = (Granularity)granularityConverter.fromString(finishGranularityString);

		String startSideString = reader.getAttribute(START_SIDE);
		Side startSide = Side.fromXmlName(startSideString);
		
		String finishSideString = reader.getAttribute(FINISH_SIDE);
		Side finishSide = Side.fromXmlName(finishSideString);
		
		if (!reader.hasMoreChildren()) {
			String msg = DATE_TIME_FILTER+" is required to have a " + PROPOSITION_IDS + " child element.";
			throw new ConversionException(msg);
		}
		reader.moveDown();
		String childNodeName = reader.getNodeName();
		if (! PROPOSITION_IDS.equals(childNodeName)) {
			String msg = "Found child element of " + DATE_TIME_FILTER + " with tag \"" + childNodeName
				+ "\". The only type of child element allowed for " + DATE_TIME_FILTER + " is " + PROPOSITION_IDS;
			throw new ConversionException(msg);
		}
		String[] propostionsIds = (String[])context.convertAnother(null, String[].class, new TableColumnSpecsConverter());
		reader.moveUp();
		
		return new DateTimeFilter(propostionsIds, start, startGranularity, finish, finishGranularity, startSide, finishSide);
	}
}
