package org.protempa.xml;

import java.util.Comparator;

import org.mvel.ConversionException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.query.handler.table.PropertyConstraint;
import org.protempa.query.handler.table.Reference;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ReferenceConverter extends AbstractConverter implements Converter {

	private static final String PROPERTY_CONSTRAINTS = "propertyConstraints";
	private static final String PROPOSITION_IDS = "propositionIDs";
	private static final String REFERENCE_NAMES = "referenceNames";
	private static final String ALL_PROPOSITION_INTERVAL_COMPARATOR = "AllPropositionIntervalComparator";
	private static final String PROPOSITION_COMPARATOR = "propositionComparator";
	private static final String TO_INDEX = "toIndex";
	private static final String FROM_INDEX = "fromIndex";

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Reference.class.equals(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Reference reference = (Reference) source;
		writer.addAttribute(FROM_INDEX, Integer.toString(reference.getFromIndex()));
		writer.addAttribute(TO_INDEX, Integer.toString(reference.getToIndex()));
		if (reference.getComparator() != null) {
			if (!(reference.getComparator() instanceof AllPropositionIntervalComparator)) {
				String msg = "Unable to convert reference comparator to XML. This conversion is implemented only for instances of "
						+ AllPropositionIntervalComparator.class.getName() + " but the comparator is an instance of "
						+ reference.getComparator().getClass().getName();
				throw new ConversionException(msg);
			}
			writer.addAttribute(PROPOSITION_COMPARATOR, ALL_PROPOSITION_INTERVAL_COMPARATOR);
		}
		String[] referenceNames = reference.getReferenceNames();
		if (referenceNames != null && referenceNames.length > 0) {
			writer.startNode(REFERENCE_NAMES);
			ReferenceNamesConverter converter = new ReferenceNamesConverter();
			context.convertAnother(referenceNames, converter);
			writer.endNode();
		}
		String[] propositionIDs = reference.getPropositionIds();
		if (propositionIDs != null && propositionIDs.length > 0) {
			writer.startNode(PROPOSITION_IDS);
			PropIDsConverter converter = new PropIDsConverter();
			context.convertAnother(propositionIDs, converter);
			writer.endNode();
		}
		PropertyConstraint[] constraints = reference.getConstraints();
		if (constraints != null && constraints.length > 0) {
			writer.startNode(PROPERTY_CONSTRAINTS);
			PropertyConstraintsConverter converter = new PropertyConstraintsConverter();
			context.convertAnother(constraints, converter);
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		int fromIndex = intAttributeValue(reader, FROM_INDEX, -1);
		int toIndex = intAttributeValue(reader, TO_INDEX, -1);
		
		String comparatorString = reader.getAttribute(PROPOSITION_COMPARATOR);
		Comparator<Proposition> comparator;
		if (comparatorString != null) {
			if (ALL_PROPOSITION_INTERVAL_COMPARATOR.equals(comparatorString)) {
				comparator = new AllPropositionIntervalComparator();
			} else {
				String msg = "XML specifies unsupported value for " + PROPOSITION_COMPARATOR;
				throw new ConversionException(msg);
			}
		} else {
			comparator = null;
		}
		String[] referenceNames = null;
		String[] propositionIds = null;
		PropertyConstraint[] constraints = null;
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (REFERENCE_NAMES.equals(reader.getNodeName())) {
				if (referenceNames != null) {
					String msg = REFERENCE_NAMES + " element is specified more than once.";
					throw new ConversionException(msg);
				}
				ReferenceNamesConverter converter = new ReferenceNamesConverter();
				referenceNames = (String[])context.convertAnother(null, String[].class, converter);
			} else if (PROPOSITION_IDS.equals(reader.getNodeName())) {
				if (propositionIds != null) {
					String msg = PROPOSITION_IDS + " element is specified more than once.";
					throw new ConversionException(msg);
				}
				PropIDsConverter converter = new PropIDsConverter();
				propositionIds = (String[])context.convertAnother(null, String[].class, converter);
				
			} else if (PROPERTY_CONSTRAINTS.equals(reader.getNodeName())) {
				if (constraints != null) {
					String msg = PROPERTY_CONSTRAINTS + " element is specified more than once.";
					throw new ConversionException(msg);
				}
				PropertyConstraintsConverter converter = new PropertyConstraintsConverter();
				constraints = (PropertyConstraint[])context.convertAnother(null, PropertyConstraint[].class, converter);
				
			}
			reader.moveUp();
		}
		return new Reference(referenceNames, propositionIds, constraints, comparator, fromIndex, toIndex);
	}

}
