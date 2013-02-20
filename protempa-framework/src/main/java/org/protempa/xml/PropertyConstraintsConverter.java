/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.handler.table.PropertyConstraint;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author mgrand
 *
 */
class PropertyConstraintsConverter extends AbstractConverter {

	private static final String PROPERTY_CONSTRAINT_VALUE_LIST = "propertyConstraintValueList";
	private static final String PROPERTY_CONSTRAINT = "propertyConstraint";

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return PropertyConstraint[].class.equals(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PropertyConstraint[] constraints = (PropertyConstraint[])source;
		if (constraints.length == 0) {
			return;
		}
		AbstractConverter converter = new PropertyConstraintConverter();
		for (PropertyConstraint constraint : constraints) {
			ValueComparator comparator = constraint.getValueComparator();
			if (comparator.equals(ValueComparator.IN) || comparator.equals(ValueComparator.NOT_IN)) {
				writer.startNode(PROPERTY_CONSTRAINT_VALUE_LIST);
			} else {
				writer.startNode(PROPERTY_CONSTRAINT);
			}
			context.convertAnother(constraint, converter);
			writer.endNode();
		}
	}
	
	private static final Set<String> propertyConstraintTags = new HashSet<String>();
	static {
		propertyConstraintTags.add(PROPERTY_CONSTRAINT);
		propertyConstraintTags.add(PROPERTY_CONSTRAINT_VALUE_LIST);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		expectChildren(reader);
		AbstractConverter converter = new PropertyConstraintConverter();
		ArrayList<PropertyConstraint> constraints = new ArrayList<PropertyConstraint>();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			expect(reader, propertyConstraintTags);
			constraints.add((PropertyConstraint)context.convertAnother(null, PropertyConstraint.class, converter));
			reader.moveUp();
		}
		return constraints.toArray(new PropertyConstraint[constraints.size()]);
	}

}
