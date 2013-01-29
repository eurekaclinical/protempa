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
import org.protempa.proposition.value.ValueComparator;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Convert {@link ValueComparator} objects to/from a string.
 * 
 * @author mgrand
 */
class ValueComparatorValueConverter implements SingleValueConverter {
	private final static String GT = "greater";
	private final static String LT = "less";
	private final static String EQ = "equal";
	private final static String NE = "notEqual";
	private final static String GTE = "greaterOrEqual";
	private final static String LTE = "lessOrEqual";
	private final static String IN = "in";
	private final static String NIN = "notIn";

	/*
	 * Acknowledge that this object can convert {@link Granularity} objects.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return ValueComparator.class.isAssignableFrom(clazz);
	}

	/*
	 * Convert a string used to represent a granularity value by the Proptema
	 * Schema's granularityType to teh equivalent granularity object.
	 */
	@Override
	public Object fromString(String s) {
		if (GT.equals(s)) {
			return ValueComparator.GREATER_THAN;
		} else if (LT.equals(s)) {
			return ValueComparator.LESS_THAN;
		} else if (EQ.equals(s)) {
			return ValueComparator.EQUAL_TO;
		} else if (NE.equals(s)) {
			return ValueComparator.NOT_EQUAL_TO;
		} else if (GTE.equals(s)) {
			return ValueComparator.GREATER_THAN_OR_EQUAL_TO;
		} else if (LTE.equals(s)) {
			return ValueComparator.LESS_THAN_OR_EQUAL_TO;
		} else if (IN.equals(s)) {
			return ValueComparator.IN;
		} else if (NIN.equals(s)) {
			return ValueComparator.NOT_IN;
		}
		throw new ConversionException("Unrecognized comparator value: " + s);
	}

	/*
	 * Convert a Granularity object to the equivalent string used to represent
	 * in as defined by Protempa's XML schema's granularityType.
	 */
	@Override
	public String toString(Object obj) {
		if (obj == ValueComparator.GREATER_THAN) {
			return GT;
		} else if (obj == ValueComparator.LESS_THAN) {
			return LT;
		} else if (obj == ValueComparator.EQUAL_TO) {
			return EQ;
		} else if (obj == ValueComparator.NOT_EQUAL_TO) {
			return NE;
		} else if (obj == ValueComparator.GREATER_THAN_OR_EQUAL_TO) {
			return GTE;
		} else if (obj == ValueComparator.LESS_THAN_OR_EQUAL_TO) {
			return LTE;
		} else if (obj == ValueComparator.IN) {
			return IN;
		} else if (obj == ValueComparator.NOT_IN) {
			return NIN;
		}

		throw new ConversionException("Encountered ValueComparator object with no known string representation: " + obj);
	}

}
