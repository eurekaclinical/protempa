package org.protempa.xml;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Base class for implementing XStream converters.
 * @author mgrand
 */
public abstract class AbstractConverter implements Converter {

	protected void expect(HierarchicalStreamReader reader, String elementName) {
		if (!elementName.equals(reader.getNodeName())) {
			String msg = "Expected a " + elementName + " element but found " + reader.getNodeName();
			throw new ConversionException(msg);
		}
	}
}
