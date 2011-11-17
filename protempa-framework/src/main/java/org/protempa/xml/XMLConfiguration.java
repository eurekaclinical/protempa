/**
 * Copyright 2011 Emory University
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.protempa.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.logging.Logger;

import org.protempa.backend.dsb.filter.AbstractFilter;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.query.And;
import org.protempa.query.Query;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * This class takes a protempa configuration information such as a query and
 * generates equivalent XML that conforms to the schema in protempa_query.xsd.
 * 
 * @author mgrand
 */
public class XMLConfiguration {
	static final DateConverter STANDARD_DATE_CONVERTER = new DateConverter("yyyy-MM-dd'T'HH:mm:ss.S", new String[0]);

	private static Logger myLogger = Logger.getLogger(XMLConfiguration.class.getName());

	private static XStream xstream = null;

	private static ThreadLocal<Boolean> surpressSchemaReference = new ThreadLocal<Boolean>();

	/**
	 * private constructor as there is no reason to instantiate this class.
	 */
	private XMLConfiguration() {
	}

	private static synchronized XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream(new StaxDriver());
			xstream.registerConverter(STANDARD_DATE_CONVERTER, XStream.PRIORITY_VERY_HIGH);

			// and
			xstream.alias("and", And.class);
			xstream.registerConverter(new AndConverter());

			// booleanValue
			xstream.alias("booleanValue", BooleanValue.class);
			xstream.registerConverter(new BooleanValueObjectConverter());

			// comparator
			xstream.useAttributeFor(PropertyValueFilter.class, "valueComparator");
			xstream.aliasField("comparator", PropertyValueFilter.class, "valueComparator");
			xstream.registerConverter(new ValueComparatorValueConverter());

			// dateTimeFilter
			xstream.alias("dateTimeFilter", DateTimeFilter.class);
			xstream.registerConverter(new DateTimeFilterConverter());

			// dateValue
			xstream.alias("dateValue", DateValue.class);
			xstream.registerConverter(new DateValueObjectConverter());

			// filters
			// We want to use custom logic to traverse the links between filters
			// so tell XStream reflection to ignore the AbstractFilter class's
			// "and" field.
			xstream.omitField(AbstractFilter.class, "and");

			// finish
			xstream.useAttributeFor(PositionFilter.class, "finish");

			// finishGranularity
			xstream.useAttributeFor(PositionFilter.class, "finishGran");
			xstream.aliasField("finishGranularity", PositionFilter.class, "finishGran");

			// finishSide
			xstream.useAttributeFor(PositionFilter.class, "finishSide");

			// granularityType
			xstream.registerConverter(new GranularityValueConverter());

			// incomparableNumberValue
			xstream.alias("incomparableNumberValue", InequalityNumberValue.class);
			xstream.registerConverter(new InequalityValueObjectConverter());

			// nominialValue
			xstream.alias("nominalValue", NominalValue.class);
			xstream.registerConverter(new NominalValueObjectConverter());

			// numberValue
			xstream.alias("numberValue", NumberValue.class);
			xstream.registerConverter(new NumberValueObjectConverter());

			// positionFilter
			xstream.omitField(PositionFilter.class, "ival");
			xstream.alias("positionFilter", PositionFilter.class);

			// property
			xstream.useAttributeFor(PropertyValueFilter.class, "property");
			xstream.aliasField("propertyName", PropertyValueFilter.class, "property");

			// propertyValueFilter
			xstream.alias("propertyValueFilter", PropertyValueFilter.class);

			// propertyValueFilter
			xstream.alias("propertyValuesFilter", PropertyValueFilter.class);
			xstream.addImplicitArray(PropertyValueFilter.class, "values");

			// propositionIDs
			xstream.registerLocalConverter(AbstractFilter.class, "propositionIds", new PropIDsConverter());
			xstream.aliasField("propositionIDs", PropertyValueFilter.class, "propositionIds");

			// protempaQuery
			xstream.alias("protempaQuery", Query.class);
			xstream.registerConverter(new QueryConverter());

			// start
			xstream.useAttributeFor(PositionFilter.class, "start");

			// startGranularity
			xstream.useAttributeFor(PositionFilter.class, "startGran");
			xstream.aliasField("startGranularity", PositionFilter.class, "startGran");

			// startSide
			xstream.useAttributeFor(PositionFilter.class, "startSide");
		}
		return xstream;
	}

	/**
	 * Read XML from the specified file that describes a Protemp query and
	 * create the query.
	 * 
	 * @param file
	 *            The file to read. The top level element of the XML in the file
	 *            must be "protempaQuery" and the XML must conform to the
	 *            Protempa XML schema.
	 * @return The described query.
	 * @throws IOException
	 *             If there is a problem.
	 */
	public static Query readQueryAsXML(File file) throws IOException {
		myLogger.entering(XMLConfiguration.class.getName(), "readQueryAsXML");
		Query query = (Query) getXStream().fromXML(file);
		return query;
	}

	/**
	 * Write the given protempa query to the specified file.
	 * 
	 * @param query
	 *            The query to be written as XML.
	 * @param file
	 *            The file to write the XML to.
	 * @throws IOException
	 *             If there is a problem writing the file.
	 */
	public static void writeQueryAsXML(Query query, File file) throws IOException {
		writeQueryAsXML(query, file, false);
	}

	/**
	 * Write the given protempa query to the specified file.
	 * 
	 * @param query
	 *            The query to be written as XML.
	 * @param file
	 *            The file to write the XML to.
	 * @param surpressSchemaReference
	 *            If true, don't include a reference to the schema in the
	 *            generated XML file.
	 * @throws IOException
	 *             If there is a problem writing the file.
	 */
	public static void writeQueryAsXML(Query query, File file, boolean surpressSchemaReference) throws IOException {
		XMLConfiguration.surpressSchemaReference.set(Boolean.valueOf(surpressSchemaReference));
		myLogger.entering(XMLConfiguration.class.getName(), "writeQueryAsXML");
		Writer writer = new FileWriter(file);
		getXStream().toXML(query, writer);
		writer.close();
		myLogger.exiting(XMLConfiguration.class.getName(), "writeQueryAsXML");
	}

	/**
	 * @return the URL of the schema to use for validating the XML
	 *         representation of a query.
	 */
	static URL getQuerySchemaUrl() {
		return QueryConverter.getQuerySchemaUrl();
	}
	
	static boolean isSurpressSchemaReferenceRequested() {
		return surpressSchemaReference.get().equals(Boolean.TRUE);
	}
}
