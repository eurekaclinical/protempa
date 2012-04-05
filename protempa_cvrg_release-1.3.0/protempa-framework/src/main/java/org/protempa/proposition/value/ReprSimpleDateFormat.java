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
package org.protempa.proposition.value;

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Andrew Post
 */
class ReprSimpleDateFormat extends SimpleDateFormat {

	private final static String PREPEND = "ABSOLUTE:";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5276847494971693256L;

	ReprSimpleDateFormat(String pattern) {
		super(pattern);
	}

	@Override
	public StringBuffer format(Date obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		StringBuffer result = super.format(obj, toAppendTo, pos);
		result.insert(0, PREPEND);
		return result;
	}

	@Override
	public Date parse(String source, ParsePosition pos) {
		String[] split = source.split(":", 2);
		if (!split[0].equals(PREPEND)) {
			return null;
		}
		return super.parse(split[1], pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.SimpleDateFormat#formatToCharacterIterator(java.lang.Object)
	 */
	@Override
	public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
		throw new UnsupportedOperationException();
	}

}
