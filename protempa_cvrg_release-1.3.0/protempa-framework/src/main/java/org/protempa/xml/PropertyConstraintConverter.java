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

import org.mvel.ConversionException;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueList;
import org.protempa.query.handler.table.PropertyConstraint;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

class PropertyConstraintConverter extends AbstractConverter {

	private static final String COMPARATOR = "comparator";
	private static final String PROPERTY_NAME = "propertyName";

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return PropertyConstraint.class.equals(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PropertyConstraint constraint = (PropertyConstraint) source;
		writer.addAttribute(PROPERTY_NAME, constraint.getPropertyName());

		ValueComparatorValueConverter comparatorConverter = new ValueComparatorValueConverter();
		ValueComparator comparator = constraint.getValueComparator();
		String comparatorString = comparatorConverter.toString(comparator);
		writer.addAttribute(COMPARATOR, comparatorString);

		Value value = constraint.getValue();
		if (value instanceof ValueList<?>) {
			if (!isAListComparator(comparator)) {
				String msg = "Unsupported conversion of a PropertyConstraint that has a ValueList with the comparator " + comparator.toString();
				throw new ConversionException(msg);
			}
			for (Value thisValue : (ValueList<?>) value) {
				valueToXML(writer, context, thisValue);
			}
		} else {
			if (isAListComparator(comparator)) {
				String msg = "Unsupported conversion of a PropertyConstraint that does not use a ValueList with the comparator " + comparator.toString();
				throw new ConversionException(msg);
			}
			valueToXML(writer, context, value);
		}
	}

	private boolean isAListComparator(ValueComparator comparator) {
		return (ValueComparator.IN.equals(comparator) || ValueComparator.NOT_IN.equals(comparator));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String propertyName = requiredAttributeValue(reader, PROPERTY_NAME);

		String comparatorString = requiredAttributeValue(reader, COMPARATOR);
		expectChildren(reader);
		ValueComparatorValueConverter comparatorConverter = new ValueComparatorValueConverter();
		ValueComparator comparator = (ValueComparator) comparatorConverter.fromString(comparatorString);

		Value value;
		if (isAListComparator(comparator)) {
			ValueList<Value> vlist = new ValueList<Value>();
			do {
				vlist.add((Value) valueFromXML(reader, context));
			} while (reader.hasMoreChildren());
			value = vlist;
		} else {
			value = valueFromXML(reader, context);
			expectNoMore(reader);
		}

		// TODO Auto-generated method stub
		return new PropertyConstraint(propertyName, comparator, value);
	}

}