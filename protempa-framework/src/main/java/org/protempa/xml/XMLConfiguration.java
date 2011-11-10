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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.logging.Logger;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.Query;

/**
 * This class takes a protempa configuration information such as a query and
 * generates equivalent XML that conforms to the schema in protempa_query.xsd.
 * 
 * @author mgrand
 */
public class XMLConfiguration {
	private static Logger myLogger = Logger.getLogger(XMLConfiguration.class.getName());

	/**
	 * URL of Castor mapping for Protempa queries
	 */
	private static URL queryMappingUrl;

	/**
	 * private constructor as there is no reason to instantiate this class.
	 */
	private XMLConfiguration() {
	}

	private static URL getQueryMappingURL() {
		if (queryMappingUrl == null) {
			queryMappingUrl = XMLConfiguration.class.getResource("castor_query.xml");
		}
		return queryMappingUrl;
	}
	
	/**
	 * @return a Castor XMLContext that encapsulates XML mapping for Protempa queries.
	 * @throws IOException
	 */
	private static XMLContext castorQueryXMLContext() throws IOException {
		XMLContext context = new XMLContext();
		Mapping mapping = new Mapping();
		try {
			mapping.loadMapping(getQueryMappingURL());
			context.addMapping(mapping);
		} catch (MappingException e) {
			String msg = "Unable to perform XML mapping operation due to a misconfiguration in the Castor mapping file at " + getQueryMappingURL();
			myLogger.severe(msg);
			throw new RuntimeException(msg, e); 
		}
		return context;
	}

	public static Query readQueryAsXML(File file) throws IOException, MarshalException, ValidationException {
		myLogger.entering(XMLConfiguration.class.getName(), "readQueryAsXML");
		XMLContext context = castorQueryXMLContext();
		Reader reader = new FileReader(file);
		
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setClass(Query.class);
		unmarshaller.setUnmarshalListener(new UnmarshalHandler());
		
		Query query = (Query)unmarshaller.unmarshal(reader);
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
	 * @throws ValidationException 
	 * @throws MarshalException 
	 */
	public static void writeQueryAsXML(Query query, File file) throws IOException, MarshalException, ValidationException {
		myLogger.entering(XMLConfiguration.class.getName(), "writeQueryAsXML");
		XMLContext context = castorQueryXMLContext();
		Marshaller marshaller = context.createMarshaller();
		Writer writer = new FileWriter(file);
		marshaller.setWriter(writer);
		marshaller.marshal(query);
		writer.close();
		myLogger.exiting(XMLConfiguration.class.getName(), "writeQueryAsXML");
	}

	private static final String[] PROP_IDS = {"Patient",
        "Encounter", "30DayReadmission", "No30DayReadmission", "PatientAll",
        "DISEASEINDICATOR:EndStageRenalDisease",
        "DISEASEINDICATOR:UncontrolledDiabetes",
        "DISEASEINDICATOR:MetastasisEvent",
        "DISEASEINDICATOR:MethicillinResistantStaphAureusEvent",
        "DISEASEINDICATOR:PressureUlcerEvent",
        "PROCEDUREINDICATOR:AmputationEvent",
        "PROCEDUREINDICATOR:BoneMarrowTransplantEvent",
        "DISEASEINDICATOR:Obesity",
        "DISEASEINDICATOR:MyocardialInfarction",
        "ERATCancer",
        "ERATCKD", "ERATCOPD", "ERATDiabetes", "ERATHF",
        "ERATHxTransplant", "ERATMI", "ERATPulmHyp", "ERATStroke",
        "DISEASEINDICATOR:SickleCellAnemiaEvent",
        "DISEASEINDICATOR:SickleCellCrisisEvent",
        "ICD9:Procedures", "ICD9:Diagnoses", "ERATMedicationOrders",
        "VitalSign", "Geography", "MSDRG:MSDRG", //"APRDRG:APRDRG",
        "HospitalChargeAmount",
        "READMISSIONS:Chemotherapy180DaysBeforeSurgery",
        "READMISSIONS:Chemotherapy365DaysBeforeSurgery",
        "READMISSIONS:Encounter90DaysEarlier",
        "READMISSIONS:Encounter180DaysEarlier",
        "LAB:BNPClassificationHeartFailureDetailed",
        "LAB:PlateletCountClassification",
        "LAB:1000764", // Serum creatinine
        "MED:(LME70) angiotensin converting enzyme inhibitors",
        "MED:(LME77) beta-adrenergic blocking agents",
        "MED:(LME81) diuretics",
        "MED:(LME87) inotropic agents"
        };

	public static void main(String[] args) throws Exception {
        String[] keyIds = {"keyId1", "keyId2"};
        
        DateTimeFilter timeRange
        = new DateTimeFilter(new String[]{"Encounter"}, 
      		               AbsoluteTimeGranularity.DAY.getShortFormat().parse("12/1/2010"),
                             AbsoluteTimeGranularity.DAY, 
                             AbsoluteTimeGranularity.DAY.getShortFormat().parse("3/31/2011"),
                             AbsoluteTimeGranularity.DAY, Side.START, Side.START);
      /*
       * Includes only inpatient visits.
       */
      PropertyValueFilter encType = new PropertyValueFilter(
              new String[]{"Encounter"}, "type",
              ValueComparator.EQUAL_TO, new NominalValue("INPATIENT"));
      timeRange.setAnd(encType);
      /*
       * Includes only inpatient visits at EUH, EUHM and WW.
       */
      PropertyValueFilter healthcareEntity = new PropertyValueFilter(
              new String[]{"Encounter"}, "healthcareEntity",
              ValueComparator.IN,
              NominalValue.getInstance("EUH"),
              NominalValue.getInstance("CLH"),
              NominalValue.getInstance("WW"));
      encType.setAnd(healthcareEntity);
      Query query = new Query(keyIds, timeRange, PROP_IDS, null);
      writeQueryAsXML(query, new File("z.xml"));
	}
}
