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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.Value;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Base class for implementing XStream converters.
 * 
 * @author mgrand
 */
public abstract class AbstractConverter implements Converter {

	/**
	 * Throw an exception if the current XML element does not have the expected
	 * name.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @param elementName
	 *            The expected element name
	 * @throws ConversionException
	 *             if the current element does not have the expected name.
	 */
	protected void expect(HierarchicalStreamReader reader, String elementName) {
		if (!elementName.equals(reader.getNodeName())) {
			String msg = "Expected a " + elementName + " element but found " + reader.getNodeName();
			throw new ConversionException(msg);
		}
	}

	/**
	 * Throw an exception if the current XML element does not have one of the
	 * expected names.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @param elementNames
	 *            The expected element names.
	 * @return the tag of the current element.
	 * @throws ConversionException
	 *             if the current element does not have the expected name.
	 */
	protected String expect(HierarchicalStreamReader reader, Set<String> elementNames) {
		String tag = reader.getNodeName();
		if (!elementNames.contains(tag)) {
			String msg = "Expected an element whose tag is one of " + elementNames + " but found " + reader.getNodeName();
			throw new ConversionException(msg);
		}
		return tag;
	}

	/**
	 * Throw an exception if the current XML element has any unprocessed
	 * children.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @throws ConversionException
	 *             if the current element has any unprocessed children.
	 */
	protected void expectNoMore(HierarchicalStreamReader reader) {
		if (reader.hasMoreChildren()) {
			String msg = "Element contains unexpected child elements";
			throw new ConversionException(msg);
		}
	}

	/**
	 * Throw an exception if the current XML element has no unprocessed
	 * children.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @throws ConversionException
	 *             if the current element does not have the expected name.
	 */
	protected void expectChildren(HierarchicalStreamReader reader) {
		if (!reader.hasMoreChildren()) {
			String msg = "Element contains no child elements but is required to contain at least one.";
			throw new ConversionException(msg);
		}
	}

	/**
	 * Get the value of the named attribute from the given reader. If the named
	 * attribute is not specified, then throw a ConversionException.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @param attributeName
	 *            The name of the attribute to get a value for.
	 * @return The value of the attribute.
	 */
	protected String requiredAttributeValue(HierarchicalStreamReader reader, String attributeName) {
		String value = reader.getAttribute(attributeName);
		if (value == null) {
			missingAttribute(attributeName);
		}
		return value;
	}

	private void missingAttribute(String attributeName) {
		String msg = "The current element does not specify a value for an attribute named \"" + attributeName + "\"";
		throw new ConversionException(msg);
	}

	/**
	 * Get the int value of the named attribute from the given reader. If the
	 * named attribute is not specified, then throw a ConversionException.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @param attributeName
	 *            The name of the attribute to get a value for.
	 * @return The value of the attribute.
	 * @throws ConversionException
	 *             if the specified value of the attribute can not be parsed as
	 *             an int.
	 */
	protected int intAttributeValue(HierarchicalStreamReader reader, String attributeName) {
		String valueString = requiredAttributeValue(reader, attributeName);
		int value;
		try {
			value = Integer.parseInt(valueString);
		} catch (Exception e) {
			String msg = "Unable to parse value of attribute n: \"" + valueString + "\"";
			throw new ConversionException(msg, e);
		}
		return value;
	}

	/**
	 * Get the int value of the named attribute from the given reader. If the
	 * named attribute is not specified, then return the given default value.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @param attributeName
	 *            The name of the attribute to get a value for.
	 * @param defaultValue
	 *            The default value to use if the named attribute is not
	 *            specified.
	 * @return The value of the attribute.
	 * @throws ConversionException
	 *             if the specified value of the attribute can not be parsed as
	 *             an int.
	 */
	protected int intAttributeValue(HierarchicalStreamReader reader, String attributeName, int defaultValue) {
		String valueString = reader.getAttribute(attributeName);
		int value;
		if (valueString == null) {
			value = 1;
		} else {
			try {
				value = Integer.parseInt(valueString);
			} catch (Exception e) {
				String msg = "Unable to parse value of attribute n: \"" + valueString + "\"";
				throw new ConversionException(msg, e);
			}
		}
		return value;
	}

	/**
	 * Get the value of the named attribute from the given reader as an Integer
	 * object. If the named attribute is not specified, then return null.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @param attributeName
	 *            The name of the attribute to get a value for.
	 * @return The value of the attribute.
	 * @throws ConversionException
	 *             if the specified value of the attribute can not be parsed as
	 *             an int.
	 */
	protected Integer integerAttributeValue(HierarchicalStreamReader reader, String attributeName) {
		String valueString = reader.getAttribute(attributeName);
		Integer value;
		if (valueString == null) {
			value = null;
		} else {
			try {
				value = Integer.valueOf(valueString);
			} catch (Exception e) {
				String msg = "Unable to parse value of attribute n: \"" + valueString + "\"";
				throw new ConversionException(msg, e);
			}
		}
		return value;
	}
	
	private static final UnitValueConverter unitConverter = new UnitValueConverter(); 

	/**
	 * Get the value of the named attribute from the given reader as a {@link Unit}
	 * object. If the named attribute is not specified, then return null.
	 * 
	 * @param reader
	 *            The object used to inquire about the current XML element.
	 * @param attributeName
	 *            The name of the attribute to get a value for.
	 * @return The value of the attribute.
	 * @throws ConversionException
	 *             if the specified value of the attribute can not be recognized
	 *             as a Unit.
	 */
	protected Unit unitAttributeValue(HierarchicalStreamReader reader, String attributeName) {
		String valueString = reader.getAttribute(attributeName);
		Unit value;
		if (valueString == null) {
			value = null;
		} else {
			try {
				value = (Unit)unitConverter.fromString(valueString);
			} catch (Exception e) {
				String msg = "Unable to parse value of attribute n: \"" + valueString + "\"";
				throw new ConversionException(msg, e);
			}
		}
		return value;
	}

	/**
	 * Write an entire Value XML element, including the element tag. This is
	 * different than XStream's marshal that writes the attributes, child
	 * elements and contents, but does not write the tag.
	 * 
	 * @param writer
	 *            The {@link} HierarchicalStreamWriter} object that XStream
	 *            passed into a marshal method.
	 * @param context
	 *            The {@link MarshallingContext} object that XStream passed into
	 *            a marshal method.
	 * @param value
	 *            The {@link Value} object to be written as XML.
	 */
	protected void valueToXML(HierarchicalStreamWriter writer, MarshallingContext context, Value value) {
		Mapper mapper = XMLConfiguration.getTableQueryResultsHandlerXStream().getMapper();
		String valueElementTag = mapper.serializedClass(value.getClass());
		writer.startNode(valueElementTag);
		context.convertAnother(value);
		writer.endNode();
	}

	/**
	 * Read an entire Value XML element, including the element tag. This is
	 * different than XStream's unmarshal that reads the attributes, child
	 * elements and contents, but does not read the tag.
	 * 
	 * @param reader
	 * @param context
	 * @return
	 */
	protected Value valueFromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Value value;
		reader.moveDown();
		Mapper mapper = XMLConfiguration.getTableQueryResultsHandlerXStream().getMapper();
		@SuppressWarnings("unchecked")
		Class<Value> type = (Class<Value>) mapper.realClass(reader.getNodeName());
		value = (Value) context.convertAnother(null, type);
		reader.moveUp();
		return value;
	}

	// Cached properties for URLS
	private static Properties urlProperties = null;
	private static URL propertiesUrl;

	/**
	 * Return the URL of the schema to use for validating XML.
	 * 
	 * @param propertyName
	 *            The property name whose value is the URL of interest.
	 */
	protected static URL getUrl(String propertyName) {
		if (urlProperties == null) {
			propertiesUrl = QueryConverter.class.getResource("urls.properties");
			try {
				InputStream inStream = propertiesUrl.openStream();
				Properties props = new Properties();
				props.load(inStream);
				urlProperties = props;
			} catch (Exception e) {
				String msg = "Error occurred while trying to read properties from " + propertiesUrl.toExternalForm();
				throw new RuntimeException(msg, e);
			}
		}
		String urlString = urlProperties.getProperty(propertyName);
		if (urlString == null) {
			String msg = "Configuration error: " + propertiesUrl.toExternalForm() + " does not specify a value for the property " + propertyName;
			throw new RuntimeException(msg);
		}
		try {
			URL querySchemaUrl = new URL(urlString);
			return querySchemaUrl;
		} catch (IOException e) {
			String msg = "Error parsing URL that was specified as the value of the " + propertyName + " property in " + propertiesUrl.toExternalForm();
			msg += "\nThe problem URL is " + urlString;
			throw new RuntimeException(msg, e);
		}
	}

	protected static String nullAsEmptyString(String str) {
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}
}
