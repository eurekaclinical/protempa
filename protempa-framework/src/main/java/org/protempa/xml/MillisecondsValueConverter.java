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

import java.util.Date;
import java.util.TimeZone;

import com.thoughtworks.xstream.converters.basic.DateConverter;

/**
 * Convert between Java's time expressed as milliseconds and an XML data string.
 * @author mgrand
 */
class MillisecondsValueConverter extends DateConverter {

	/**
	 * 
	 */
	public MillisecondsValueConverter() {
		super("yyyy-MM-dd'T'HH:mm:ss.S", new String[0], TimeZone.getDefault());
	}

	/**
	 * Acknowledge that this object can convert Long objects.
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return Long.class.equals(type);
	}

	@Override
	public Object fromString(String s) {
		Date date = (Date)super.fromString(s);
		return new Long(date.getTime());
	}

	@Override
	public String toString(Object obj) {
		Long time = (Long)obj;
		Date date = new Date(time);
		return super.toString(date);
	}
}
