package org.protempa.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

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
	 * Throw an exception if the current XML element has any unprocessed children.
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
	 * Throw an exception if the current XML element has no unprocessed children.
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
}
