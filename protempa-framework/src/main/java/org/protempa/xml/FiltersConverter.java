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

import org.protempa.backend.dsb.filter.AbstractFilter;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.value.ValueComparator;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert a Filter object to/from XML <filters></filters>
 * 
 * @author mgrand
 */
class FiltersConverter extends AbstractConverter {

	private static final String PROPERTY_VALUE_FILTER = "propertyValueFilter";
	private static final String PROPERTY_VALUES_FILTER = "propertyValuesFilter";
	private static final String POSITION_FILTER = "positionFilter";
	private static final String DATE_TIME_FILTER = "dateTimeFilter";

	/**
	 * Constructor
	 */
	public FiltersConverter() {
		super();
	}

	/**
	 * This converter is intended to be explicitly called from other converters
	 * as it corresponds to nothing 
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return Filter.class.isAssignableFrom(clazz);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		Filter filter = (Filter) value;
		do {
			writer.startNode(objectToXmlTag(filter));
			context.convertAnother(filter);
			writer.endNode();
			filter = filter.getAnd();
		} while (filter != null);
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
		Filter firstFilter = null;
		Filter lastFilter = null;
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			Class<? extends Filter> childClass = xmlTagToClass(reader.getNodeName());
			Filter thisFilter = (Filter)context.convertAnother(null, childClass);
			if (firstFilter == null) {
				firstFilter = thisFilter;
				lastFilter = thisFilter;
			} else {
				((AbstractFilter)lastFilter).setAnd(thisFilter);
				lastFilter = thisFilter;
			}
			reader.moveUp();
		}
		return firstFilter;
	}

	private Class<? extends Filter> xmlTagToClass(String tag) {
		if (DATE_TIME_FILTER.equals(tag)) {
			return DateTimeFilter.class;
		} else if (PROPERTY_VALUE_FILTER.equals(tag)) {
			return PropertyValueFilter.class;
		} else if (PROPERTY_VALUES_FILTER.equals(tag)) {
			return PropertyValueFilter.class;
		} else if (POSITION_FILTER.equals(tag)) {
			return PositionFilter.class;
		}
		throw new ConversionException("<filters> has a child with the unexpected tag " + tag);
	}
	
	private String objectToXmlTag(Filter filter) {
		Class<? extends Filter> clazz = filter.getClass();
		if (clazz == DateTimeFilter.class) {
			return DATE_TIME_FILTER;
		}
		if (clazz == PropertyValueFilter.class) {
			PropertyValueFilter pvFilter = (PropertyValueFilter)filter;
			ValueComparator comparitor = pvFilter.getValueComparator();
			if (comparitor.equals(ValueComparator.IN) || comparitor.equals(ValueComparator.NOT_IN)) {
				return PROPERTY_VALUES_FILTER;
			} else {
				return PROPERTY_VALUE_FILTER;
			}
		}
		if (clazz == PositionFilter.class) {
			return POSITION_FILTER;
		}
		throw new ConversionException("Encountered unknown type of filter class: " + clazz.getName());
	}
}
