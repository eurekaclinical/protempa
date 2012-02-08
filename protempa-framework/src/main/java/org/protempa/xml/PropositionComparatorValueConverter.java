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

import org.protempa.proposition.comparator.AllPropositionIntervalComparator;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * @author mgrand
 *
 */
class PropositionComparatorValueConverter implements SingleValueConverter {

	private static final String ALL_PROPOSITION_INTERVAL_COMPARATOR = "AllPropositionIntervalComparator";

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return AllPropositionIntervalComparator.class.equals(type);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Object obj) {
		if (obj instanceof AllPropositionIntervalComparator) {
			return ALL_PROPOSITION_INTERVAL_COMPARATOR;
		} else {
			throw new ConversionException("Object to convert to string is not an instance of AllPropositionIntervalComparator");
		}
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#fromString(java.lang.String)
	 */
	@Override
	public Object fromString(String str) {
		if (ALL_PROPOSITION_INTERVAL_COMPARATOR.equals(str)) {
			return new AllPropositionIntervalComparator();
		}
		throw new ConversionException("Unable to convert an object that is not an instance of " + ALL_PROPOSITION_INTERVAL_COMPARATOR);
	}

}
