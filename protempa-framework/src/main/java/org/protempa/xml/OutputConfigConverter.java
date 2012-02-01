/**
 * 
 */
package org.protempa.xml;

import org.protempa.query.handler.table.OutputConfig;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 * 
 */
public class OutputConfigConverter extends AbstractConverter {

	private static final String LENGTH_HEADING = "lengthHeading";
	private static final String FINISH_HEADING = "finishHeading";
	private static final String START_OR_TIMESTAMP_HEADING = "startOrTimestampHeading";
	private static final String ABBREV_DISPLAY_NAME_HEADING = "abbrevDisplayNameHeading";
	private static final String DISPLAY_NAME_HEADING = "displayNameHeading";
	private static final String VALUE_HEADING = "valueHeading";
	private static final String ID_HEADING = "idHeading";
	private static final String SHOW_LENGTH = "showLength";
	private static final String SHOW_FINISH = "showFinish";
	private static final String SHOW_START_OR_TIMESTAMP = "showStartOrTimestamp";
	private static final String SHOW_ABBREV_DISPLAY_NAME = "showAbbrevDisplayName";
	private static final String SHOW_DISPLAY_NAME = "showDisplayName";
	private static final String SHOW_VALUE = "showValue";
	private static final String SHOW_ID = "showId";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
	 * lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return OutputConfig.class.equals(type);
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
		OutputConfig outputConfig = (OutputConfig) source;
		writer.addAttribute(SHOW_ID, Boolean.toString(outputConfig.showId()));
		writer.addAttribute(SHOW_VALUE, Boolean.toString(outputConfig.showValue()));
		writer.addAttribute(SHOW_DISPLAY_NAME, Boolean.toString(outputConfig.showDisplayName()));
		writer.addAttribute(SHOW_ABBREV_DISPLAY_NAME, Boolean.toString(outputConfig.showAbbrevDisplayName()));
		writer.addAttribute(SHOW_START_OR_TIMESTAMP, Boolean.toString(outputConfig.showStartOrTimestamp()));
		writer.addAttribute(SHOW_FINISH, Boolean.toString(outputConfig.showFinish()));
		writer.addAttribute(SHOW_LENGTH, Boolean.toString(outputConfig.showLength()));
		writer.addAttribute(ID_HEADING, outputConfig.getIdHeading());
		writer.addAttribute(VALUE_HEADING, outputConfig.getValueHeading());
		writer.addAttribute(DISPLAY_NAME_HEADING, outputConfig.getDisplayNameHeading());
		writer.addAttribute(ABBREV_DISPLAY_NAME_HEADING, outputConfig.getAbbrevDisplayNameHeading());
		writer.addAttribute(START_OR_TIMESTAMP_HEADING, outputConfig.getStartOrTimestampHeading());
		writer.addAttribute(FINISH_HEADING, outputConfig.getFinishHeading());
		writer.addAttribute(LENGTH_HEADING, outputConfig.getLengthHeading());
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
		boolean showId = Boolean.valueOf(reader.getAttribute(SHOW_ID)).booleanValue();
		boolean showValue = Boolean.valueOf(reader.getAttribute(SHOW_VALUE)).booleanValue();
		boolean showDisplayName = Boolean.valueOf(reader.getAttribute(SHOW_DISPLAY_NAME)).booleanValue();
		boolean showAbbrevDisplayName = Boolean.valueOf(reader.getAttribute(SHOW_ABBREV_DISPLAY_NAME)).booleanValue();
		boolean showStartOrTimestamp = Boolean.valueOf(reader.getAttribute(SHOW_START_OR_TIMESTAMP)).booleanValue();
		boolean showFinish = Boolean.valueOf(reader.getAttribute(SHOW_FINISH)).booleanValue();
		boolean showLength = Boolean.valueOf(reader.getAttribute(SHOW_LENGTH)).booleanValue();
		String idHeading = nullAsEmptyString(reader.getAttribute(ID_HEADING));
		String valueHeading = nullAsEmptyString(reader.getAttribute(VALUE_HEADING));
		String displayNameHeading = nullAsEmptyString(reader.getAttribute(DISPLAY_NAME_HEADING));
		String abbrevDisplayNameHeading = nullAsEmptyString(reader.getAttribute(ABBREV_DISPLAY_NAME_HEADING));
		String startOrTimestampHeading = nullAsEmptyString(reader.getAttribute(START_OR_TIMESTAMP_HEADING));
		String finishHeading = nullAsEmptyString(reader.getAttribute(FINISH_HEADING));
		String lengthHeading = nullAsEmptyString(reader.getAttribute(LENGTH_HEADING));

		return new OutputConfig(showId, showValue, showDisplayName, showAbbrevDisplayName, showStartOrTimestamp, showFinish, showLength, idHeading,
				valueHeading, displayNameHeading, abbrevDisplayNameHeading, startOrTimestampHeading, finishHeading, lengthHeading);
	}

}
