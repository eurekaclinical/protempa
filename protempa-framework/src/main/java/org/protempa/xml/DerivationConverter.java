/**
 * 
 */
package org.protempa.xml;

import java.util.Comparator;

import org.mvel.ConversionException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.Value;
import org.protempa.query.handler.table.Derivation;
import org.protempa.query.handler.table.Derivation.Behavior;
import org.protempa.query.handler.table.PropertyConstraint;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 *
 */
public class DerivationConverter extends AbstractConverter {

	private static final String RELATION = "relation";
	private static final String ALLOWED_VALUES = "allowedValues";
	private static final String PROPERTY_CONSTRAINTS = "propertyConstraints";
	private static final String PROPOSITION_IDS = "propositionIDs";
	private static final String PROPOSITION_COMPARATOR = "propositionComparator";
	private static final String TO_INDEX = "toIndex";
	private static final String FROM_INDEX = "fromIndex";
	private static final String BEHAVIOR = "behavior";

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Derivation.class.equals(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Derivation derivation = (Derivation)source;
		
		BehaviorValueConverter behaviorConverter = new BehaviorValueConverter();
		String behaviorString = behaviorConverter.toString(derivation.getBehavior());
		writer.addAttribute(BEHAVIOR, behaviorString);
		
		writer.addAttribute(FROM_INDEX, Integer.toString(derivation.getFromIndex()));
		writer.addAttribute(TO_INDEX, Integer.toString(derivation.getToIndex()));
		
		Comparator<Proposition> comparator = derivation.getComparator();
		if (comparator != null) {
			PropositionComparatorValueConverter converter = new PropositionComparatorValueConverter();
			writer.addAttribute(PROPOSITION_COMPARATOR, converter.toString(comparator));
		}
		
		writer.startNode(PROPOSITION_IDS);
		context.convertAnother(derivation.getPropositionIds(), new PropIDsConverter());
		writer.endNode();
		
		PropertyConstraint[] constraints = derivation.getConstraints();
		if (constraints != null && constraints.length > 0) {
			writer.startNode(PROPERTY_CONSTRAINTS);
			PropertyConstraintsConverter converter = new PropertyConstraintsConverter();
			context.convertAnother(constraints, converter);
			writer.endNode();
		}
		
		Value[] allowedValues = derivation.getAllowedValues();
		if (allowedValues != null && allowedValues.length > 0) {
			writer.startNode(ALLOWED_VALUES);
			AllowedValuesConverter converter = new AllowedValuesConverter();
			context.convertAnother(allowedValues, converter);
			writer.endNode();
		}
		Relation relation = derivation.getRelation();
		if (relation != null) {
			writer.startNode(RELATION);
			RelationConverter relationConverter = new RelationConverter();
			context.convertAnother(relation, relationConverter);
			writer.endNode();
		}
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String behaviorString = requiredAttributeValue(reader, BEHAVIOR);
		BehaviorValueConverter behaviorConverter = new BehaviorValueConverter();
		Behavior behavior = (Behavior) behaviorConverter.fromString(behaviorString);
		
		int fromIndex = intAttributeValue(reader, FROM_INDEX, -1);
		int toIndex = intAttributeValue(reader, TO_INDEX, -1);
		
		Comparator<Proposition> comparator;
		String comparatorString = reader.getAttribute(PROPOSITION_COMPARATOR);
		if (comparatorString != null) {
			comparator = convertComparatorString(comparatorString);
		} else {
			comparator = null;
		}

		expectChildren(reader);
		reader.moveDown();
		expect(reader, PROPOSITION_IDS);
		String[] propositionIds = (String[]) context.convertAnother(null, String[].class, new PropIDsConverter());
		reader.moveUp();

		PropertyConstraint[] constraints = null;
		Value[] allowedValues = null;
		Relation relation = null;

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (PROPERTY_CONSTRAINTS.equals(reader.getNodeName())) {
				if (constraints != null) {
					throw new ConversionException("diversion contains multiple propertyConstraints elements");
				}
				PropertyConstraintsConverter converter = new PropertyConstraintsConverter();
				constraints = (PropertyConstraint[]) context.convertAnother(null, PropertyConstraint[].class, converter);
				reader.moveUp();
			} else if (ALLOWED_VALUES.equals(reader.getNodeName())) {
				if (allowedValues != null) {
					throw new ConversionException("diversion contains multiple allowedValues elements");
				}
				AllowedValuesConverter converter = new AllowedValuesConverter();
				allowedValues = (Value[]) context.convertAnother(null, Value[].class, converter);
				reader.moveUp();
			} else if (RELATION.equals(reader.getNodeName())) {
				if (relation != null) {
					throw new ConversionException("diversion contains multiple relation elements");
				}
				RelationConverter relationConverter = new RelationConverter();
				relation = (Relation) context.convertAnother(null, Relation.class, relationConverter);
				reader.moveUp();
			} else {
				throw new ConversionException("Encountered unexpected element: " + reader.getNodeName());
			}
		}
		return new Derivation(propositionIds, constraints, comparator, fromIndex, toIndex, allowedValues, behavior, relation);
	}

	@SuppressWarnings("unchecked")
	private Comparator<Proposition> convertComparatorString(String comparatorString) {
		PropositionComparatorValueConverter converter = new PropositionComparatorValueConverter();
		return (Comparator<Proposition>)converter.fromString(comparatorString);
	}

}
