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
import java.util.logging.Logger;

import org.protempa.backend.dsb.filter.AbstractFilter;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.And;
import org.protempa.query.Query;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * This class takes a protempa configuration information such as a query and
 * generates equivalent XML that conforms to the schema in protempa_query.xsd.
 * 
 * @author mgrand
 */
public class XMLConfiguration {
	private static Logger myLogger = Logger.getLogger(XMLConfiguration.class.getName());

	private static XStream xstream = null;

	/**
	 * private constructor as there is no reason to instantiate this class.
	 */
	private XMLConfiguration() {
	}

	private static synchronized XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream(new StaxDriver());
			
			xstream.alias("and", And.class);
			xstream.registerConverter(new AndConverter());
			
			// dateTimeFilter
			xstream.alias("dateTimeFilter", DateTimeFilter.class);
			xstream.registerConverter(new DateTimeFilterConverter());

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

			// nominialValue
			xstream.alias("nominalValue", NominalValue.class);
			
			xstream.alias("positionFilter", PositionFilter.class);

			// positionFilter
			xstream.omitField(PositionFilter.class, "ival");
			
			// property
			xstream.useAttributeFor(PropertyValueFilter.class, "property");
			xstream.aliasField("propertyName", PropertyValueFilter.class, "property");
			
			// propertyValueFilter
			xstream.alias("propertyValueFilter", PropertyValueFilter.class);
			
			// propertyValueFilter
			xstream.alias("propertyValuesFilter", PropertyValueFilter.class);
			
			// propositionIDs
			xstream.registerLocalConverter(AbstractFilter.class, "propositionIds", new PropIDsConverter());

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
		myLogger.entering(XMLConfiguration.class.getName(), "writeQueryAsXML");
		Writer writer = new FileWriter(file);
		getXStream().toXML(query, writer);
		writer.close();
		myLogger.exiting(XMLConfiguration.class.getName(), "writeQueryAsXML");
	}

	private static final String[] PROP_IDS = { "Patient", "Encounter", "30DayReadmission", "No30DayReadmission", "PatientAll",
			"DISEASEINDICATOR:EndStageRenalDisease", "DISEASEINDICATOR:UncontrolledDiabetes", "DISEASEINDICATOR:MetastasisEvent",
			"PROCEDUREINDICATOR:BoneMarrowTransplantEvent", "DISEASEINDICATOR:Obesity", "ERATCancer", "ERATCKD",
			"VitalSign",
			"Geography",
			"MSDRG:MSDRG",
			"LAB:PlateletCountClassification",
			"LAB:1000764", 
			"MED:(LME87) inotropic agents" };

	public static void main(String[] args) throws Exception {
		String[] keyIds = { "keyId1", "keyId2" };

		DateTimeFilter timeRange = new DateTimeFilter(new String[] { "Encounter" }, AbsoluteTimeGranularity.DAY.getShortFormat().parse("12/1/2010"),
				AbsoluteTimeGranularity.DAY, AbsoluteTimeGranularity.DAY.getShortFormat().parse("3/31/2011"), AbsoluteTimeGranularity.DAY, Side.FINISH,
				Side.START);
		/*
		 * Includes only inpatient visits.
		 */
		PropertyValueFilter encType = new PropertyValueFilter(new String[] { "Encounter" }, "type", ValueComparator.EQUAL_TO, new NominalValue("INPATIENT"));
		timeRange.setAnd(encType);
		
		/*
		 * Includes only inpatient visits at EUH, EUHM and WW.
		 */
		PropertyValueFilter healthcareEntity = new PropertyValueFilter(new String[] { "Encounter" }, "healthcareEntity", ValueComparator.IN,
				NominalValue.getInstance("EUH"), NominalValue.getInstance("CLH"), NominalValue.getInstance("WW"));
		encType.setAnd(healthcareEntity);
		
		Query query = new Query(keyIds, timeRange, PROP_IDS, null);
		writeQueryAsXML(query, new File("z.xml"));
	}
}
