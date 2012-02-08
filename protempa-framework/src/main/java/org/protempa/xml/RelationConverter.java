/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.xml;

import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.Unit;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 * 
 */
class RelationConverter extends AbstractConverter {

	private static final String MAX_DISTANCE_BETWEEN_FINISHES = "maxDistanceBetweenFinishes";
	private static final String MIN_DISTANCE_BETWEEN_FINISHES = "minDistanceBetweenFinishes";
	private static final String MAX_DISTANCE_BETWEEN = "maxDistanceBetween";
	private static final String MIN_DISTANCE_BETWEEN = "minDistanceBetween";
	private static final String MAX_SPAN = "maxSpan";
	private static final String MIN_SPAN = "minSpan";
	private static final String MAX_DISTANCE_BETWEEN_STARTS = "maxDistanceBetweenStarts";
	private static final String UNIT = "unit";
	private static final String LENGTH = "length";
	private static final String MIN_DISTANCE_BETWEEN_STARTS = "minDistanceBetweenStarts";

	private UnitValueConverter unitConverter = new UnitValueConverter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
	 * lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Relation.class.equals(type);
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
		Relation relation = (Relation) source;

		if (relation.getMinDistanceBetweenStarts() != null || relation.getMinDistanceBetweenStartsUnits() != null) {
			writer.startNode(MIN_DISTANCE_BETWEEN_STARTS);
			addDurationAttributes(writer, relation.getMinDistanceBetweenStarts(), relation.getMinDistanceBetweenStartsUnits());
			writer.endNode();
		}
		if (relation.getMaxDistanceBetweenStarts() != null || relation.getMaxDistanceBetweenStartsUnits() != null) {
			writer.startNode(MAX_DISTANCE_BETWEEN_STARTS);
			addDurationAttributes(writer, relation.getMaxDistanceBetweenStarts(), relation.getMaxDistanceBetweenStartsUnits());
			writer.endNode();
		}
		if (relation.getMinSpan() != null || relation.getMinSpanUnits() != null) {
			writer.startNode(MIN_SPAN);
			addDurationAttributes(writer, relation.getMinSpan(), relation.getMinSpanUnits());
			writer.endNode();
		}
		if (relation.getMaxSpan() != null || relation.getMaxSpanUnits() != null) {
			writer.startNode(MAX_SPAN);
			addDurationAttributes(writer, relation.getMaxSpan(), relation.getMaxSpanUnits());
			writer.endNode();
		}
		if (relation.getMinDistanceBetween() != null || relation.getMinDistanceBetweenUnits() != null) {
			writer.startNode(MIN_DISTANCE_BETWEEN);
			addDurationAttributes(writer, relation.getMinDistanceBetween(), relation.getMinDistanceBetweenUnits());
			writer.endNode();
		}
		if (relation.getMaxDistanceBetween() != null || relation.getMaxDistanceBetweenUnits() != null) {
			writer.startNode(MAX_DISTANCE_BETWEEN);
			addDurationAttributes(writer, relation.getMaxDistanceBetween(), relation.getMaxDistanceBetweenUnits());
			writer.endNode();
		}
		if (relation.getMinDistanceBetweenFinishes() != null || relation.getMinDistanceBetweenFinishesUnits() != null) {
			writer.startNode(MIN_DISTANCE_BETWEEN_FINISHES);
			addDurationAttributes(writer, relation.getMinDistanceBetweenFinishes(), relation.getMinDistanceBetweenFinishesUnits());
			writer.endNode();
		}
		if (relation.getMaxDistanceBetweenFinishes() != null || relation.getMaxDistanceBetweenFinishesUnits() != null) {
			writer.startNode(MAX_DISTANCE_BETWEEN_FINISHES);
			addDurationAttributes(writer, relation.getMaxDistanceBetweenFinishes(), relation.getMaxDistanceBetweenFinishesUnits());
			writer.endNode();
		}
	}

	private void addDurationAttributes(HierarchicalStreamWriter writer, Integer length, Unit unit) {
		if (length != null) {
			writer.addAttribute(LENGTH, Integer.toString(length));
		}
		if (unit != null) {
			writer.addAttribute(UNIT, unitConverter.toString(unit));
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
		Integer minDistanceBetweenStarts = null;
		Unit minDistanceBetweenStartsUnits = null;
		Integer maxDistanceBetweenStarts = null;
		Unit maxDistanceBetweenStartsUnits = null;
		Integer minSpan = null;
		Unit minSpanUnits = null;
		Integer maxSpan = null;
		Unit maxSpanUnits = null;
		Integer minDistanceBetween = null;
		Unit minDistanceBetweenUnits = null;
		Integer maxDistanceBetween = null;
		Unit maxDistanceBetweenUnits = null;
		Integer minDistanceBetweenFinishes = null;
		Unit minDistanceBetweenFinishesUnits = null;
		Integer maxDistanceBetweenFinishes = null;
		Unit maxDistanceBetweenFinishesUnits = null;
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String elementName = reader.getNodeName();
			if (MIN_DISTANCE_BETWEEN_STARTS.equals(elementName)) {
				minDistanceBetweenStarts = integerAttributeValue(reader, LENGTH);
				minDistanceBetweenStartsUnits = unitAttributeValue(reader, UNIT);
			} else if (MAX_DISTANCE_BETWEEN_STARTS.equals(elementName)) {
				maxDistanceBetweenStarts = integerAttributeValue(reader, LENGTH);
				maxDistanceBetweenStartsUnits = unitAttributeValue(reader, UNIT);
			} else if (MIN_SPAN.equals(elementName)) {
				minSpan = integerAttributeValue(reader, LENGTH);
				minSpanUnits = unitAttributeValue(reader, UNIT);
			} else if (MAX_SPAN.equals(elementName)) {
				maxSpan = integerAttributeValue(reader, LENGTH);
				maxSpanUnits = unitAttributeValue(reader, UNIT);
			} else if (MIN_DISTANCE_BETWEEN.equals(elementName)) {
				minDistanceBetween = integerAttributeValue(reader, LENGTH);
				minDistanceBetweenUnits = unitAttributeValue(reader, UNIT);
			} else if (MAX_DISTANCE_BETWEEN.equals(elementName)) {
				maxDistanceBetween = integerAttributeValue(reader, LENGTH);
				maxDistanceBetweenUnits = unitAttributeValue(reader, UNIT);
			} else if (MIN_DISTANCE_BETWEEN_FINISHES.equals(elementName)) {
				minDistanceBetweenFinishes = integerAttributeValue(reader, LENGTH);
				minDistanceBetweenFinishesUnits = unitAttributeValue(reader, UNIT);
			} else if (MAX_DISTANCE_BETWEEN_FINISHES.equals(elementName)) {
				maxDistanceBetweenFinishes = integerAttributeValue(reader, LENGTH);
				maxDistanceBetweenFinishesUnits = unitAttributeValue(reader, UNIT);
			}
			reader.moveUp();
		}

		return new Relation(
				minDistanceBetweenStarts, minDistanceBetweenStartsUnits, maxDistanceBetweenStarts, maxDistanceBetweenStartsUnits, 
				minSpan, minSpanUnits, maxSpan, maxSpanUnits,
				minDistanceBetween, minDistanceBetweenUnits, maxDistanceBetween, maxDistanceBetweenUnits,
				minDistanceBetweenFinishes, minDistanceBetweenFinishesUnits, maxDistanceBetweenFinishes, maxDistanceBetweenFinishesUnits);
	}

}
