package org.protempa.xml;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.Query;
import org.xml.sax.SAXException;

/**
 * Tests for XML
 * 
 * @author Mark Grand
 */
public class XMLConfigurationTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testQuery() throws Throwable {
		Query query = createTestQuery();
		File file = new File("z.xml");
		XMLConfiguration.writeQueryAsXML(query, file);
		checkXMLValid(file);
		Query reconstitutedQuery = XMLConfiguration.readQueryAsXML(file);
		assertTrue(reconstitutedQuery.equals(query));
	}

	private void checkXMLValid(File file) throws SAXException, ParserConfigurationException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(XMLConfiguration.getQuerySchemaUrl());
		Validator validator = schema.newValidator();
		StreamSource source = new StreamSource(file);
		validator.validate(source);
	}

	private Query createTestQuery() throws ParseException {
		final String[] keyIds = { "keyId1", "keyId2" };

		final String[] PROP_IDS = { "Patient", "Encounter", "30DayReadmission", "No30DayReadmission", "PatientAll",
			"DISEASEINDICATOR:EndStageRenalDisease", "DISEASEINDICATOR:UncontrolledDiabetes", "DISEASEINDICATOR:MetastasisEvent",
			"PROCEDUREINDICATOR:BoneMarrowTransplantEvent", "DISEASEINDICATOR:Obesity", "ERATCancer", "ERATCKD", "VitalSign", "Geography", "MSDRG:MSDRG",
			"LAB:PlateletCountClassification", "LAB:1000764", "MED:(LME87) inotropic agents" };


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

		/*
		 * Nonsense filter for testing booleanValue
		 */
		PropertyValueFilter fubarEntity = new PropertyValueFilter(new String[] { "Encounter" }, "FUBAR", ValueComparator.EQUAL_TO, BooleanValue.TRUE);
		healthcareEntity.setAnd(fubarEntity);

		/*
		 * Nonsense filter for testing numberValue
		 */
		PropertyValueFilter numberEntity = new PropertyValueFilter(new String[] { "Encounter" }, "measure", ValueComparator.LESS_THAN_OR_EQUAL_TO,
				NumberValue.getInstance(44));
		fubarEntity.setAnd(numberEntity);

		/*
		 * Nonsense filter for testing inequalityValue
		 */
		PropertyValueFilter inequalityEntity = new PropertyValueFilter(new String[] { "Encounter" }, "measure", ValueComparator.GREATER_THAN_OR_EQUAL_TO,
				InequalityNumberValue.parse("<44"));
		numberEntity.setAnd(inequalityEntity);

		/*
		 * Nonsense filter for testing dateValue
		 */
		PropertyValueFilter dateEntity = new PropertyValueFilter(new String[] { "Encounter" }, "measure", ValueComparator.LESS_THAN,
				DateValue.getInstance(new Date()));
		inequalityEntity.setAnd(dateEntity);

		Query query = new Query(keyIds, timeRange, PROP_IDS, null);
		return query;
	}
}
