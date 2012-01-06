package org.protempa.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.protempa.AlgorithmSource;
import org.protempa.KnowledgeSource;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Tests for XML
 * 
 * @author Mark Grand
 */
public class XMLConfigurationTest extends TestCase {

	private DateTimeFilter timeRange = new DateTimeFilter(new String[] { "Encounter" }, new GregorianCalendar(2010, 11, 1).getTime(),
			AbsoluteTimeGranularity.DAY, new GregorianCalendar(2011, 2, 31).getTime(), AbsoluteTimeGranularity.DAY, Side.FINISH,
			Side.START);

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testQuery() throws Throwable {
		Query query = createTestQuery();
		File file = new File("z.xml");
		XMLConfiguration.writeQueryAsXML(query, file, true);
		checkXMLValid(file);
		KnowledgeSource ks = new KnowledgeSource(new KnowledgeSourceBackend[0]);
		AlgorithmSource as = new AlgorithmSource(new AlgorithmSourceBackend[0]);
		Query reconstitutedQuery = XMLConfiguration.readQueryAsXML(file, ks, as);
		assertTrue("Deserialized query is equal to the original query", reconstitutedQuery.equals(query));
		
		File z2 = new File("z2.xml");
		XMLConfiguration.writeQueryAsXML(reconstitutedQuery, z2);
		FileReader freader = new FileReader(z2);
		String xml = new BufferedReader(freader).readLine();
		assertTrue(xml.contains("xsi:noNamespaceSchemaLocation=\"http://aiwdev02.eusch.org/protempa/schema/1.1/protempa_query.xsd\""));
		
	}

	private void checkXMLValid(File file) throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = parser.parse(file);

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// Get schema from the local file since the correct schema may not yet
		// be deployed to its URL when we run this test.
		Schema schema = schemaFactory.newSchema(getClass().getResource("protempa_query.xsd"));
		Validator validator = schema.newValidator();
		validator.validate(new DOMSource(document));
	}

	private Query createTestQuery() throws Exception {
		final String[] keyIds = { "keyId1", "keyId2" };

		final String[] PROP_IDS = { "Patient", "Encounter", "30DayReadmission", "No30DayReadmission", "PatientAll", "DISEASEINDICATOR:EndStageRenalDisease",
				"DISEASEINDICATOR:UncontrolledDiabetes", "DISEASEINDICATOR:MetastasisEvent", "PROCEDUREINDICATOR:BoneMarrowTransplantEvent",
				"DISEASEINDICATOR:Obesity", "ERATCancer", "ERATCKD", "VitalSign", "Geography", "MSDRG:MSDRG", "LAB:PlateletCountClassification", "LAB:1000764",
				"MED:(LME87) inotropic agents" };

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

		DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder();
		queryBuilder.setKeyIds(keyIds);
		queryBuilder.setFilters(timeRange);
		queryBuilder.setPropIds(PROP_IDS);
		DefaultQueryBuilder.setValidatePropositionIds(false);
		Query query = queryBuilder.build(new KnowledgeSource(new KnowledgeSourceBackend[0]), new AlgorithmSource(new AlgorithmSourceBackend[0]));
		return query;
	}
}
