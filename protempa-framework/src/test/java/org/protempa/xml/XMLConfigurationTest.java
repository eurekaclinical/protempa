/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
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
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertArrayEquals;
import org.protempa.AlgorithmSource;
import org.protempa.AlgorithmSourceImpl;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceImpl;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.dest.QueryResultsHandlerInitException;
import org.protempa.dest.table.AtLeastNColumnSpec;
import org.protempa.dest.table.CountColumnSpec;
import org.protempa.dest.table.Derivation;
import org.protempa.dest.table.DistanceBetweenColumnSpec;
import org.protempa.dest.table.Link;
import org.protempa.dest.table.OutputConfig;
import org.protempa.dest.table.PropertyConstraint;
import org.protempa.dest.table.PropositionColumnSpec;
import org.protempa.dest.table.PropositionValueColumnSpec;
import org.protempa.dest.table.Reference;
import org.protempa.dest.table.TableColumnSpec;
import org.protempa.dest.table.TableDestination;
import org.protempa.dest.table.ValueOutputConfig;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.proposition.interval.Interval.Side;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.RelativeDayUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueList;
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

    private final DateTimeFilter timeRange
            = new DateTimeFilter(new String[]{"Encounter"}, new GregorianCalendar(2010, 11, 1).getTime(),
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
        File file = File.createTempFile("xmlConfigTestz", ".xml");
        XMLConfiguration.writeQueryAsXML(query, file, true);
        checkXMLValid(file, "protempa_query.xsd");
        KnowledgeSource ks = new KnowledgeSourceImpl(new KnowledgeSourceBackend[0]);
        AlgorithmSource as = new AlgorithmSourceImpl(new AlgorithmSourceBackend[0]);
        Query reconstitutedQuery = XMLConfiguration.readQueryAsXML(file, ks, as);
        assertTrue("Deserialized query is equal to the original query", reconstitutedQuery.equals(query));

        File z2 = File.createTempFile("xmlConfigTestz2", ".xml");
        XMLConfiguration.writeQueryAsXML(reconstitutedQuery, z2);
        FileReader freader = new FileReader(z2);
        String xml = new BufferedReader(freader).readLine();
        assertTrue(xml.contains("xsi:noNamespaceSchemaLocation=\"http://aiwdev02.eushc.org/protempa/schema/1.1/protempa_query.xsd\""));
    }

    private void checkXMLValid(File file, String xsd) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(file);

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // Get schema from the local file since the correct schema may not yet
        // be deployed to its URL when we run this test.
        Schema schema = schemaFactory.newSchema(getClass().getResource(xsd));
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(document));
    }

    private Query createTestQuery() throws Exception {
        final String[] keyIds = {"keyId1", "keyId2"};

        final String[] PROP_IDS = {"Patient", "Encounter", "30DayReadmission", "No30DayReadmission", "PatientAll", "DISEASEINDICATOR:EndStageRenalDisease",
            "DISEASEINDICATOR:UncontrolledDiabetes", "DISEASEINDICATOR:MetastasisEvent", "PROCEDUREINDICATOR:BoneMarrowTransplantEvent",
            "DISEASEINDICATOR:Obesity", "ERATCancer", "ERATCKD", "VitalSign", "Geography", "MSDRG:MSDRG", "LAB:PlateletCountClassification", "LAB:1000764",
            "MED:(LME87) inotropic agents"};

        /*
         * Includes only inpatient visits.
         */
        PropertyValueFilter encType = new PropertyValueFilter(new String[]{"Encounter"}, "type", ValueComparator.EQUAL_TO, new NominalValue("INPATIENT"));
        timeRange.setAnd(encType);

        /*
         * Includes only inpatient visits at EUH, EUHM and WW.
         */
        PropertyValueFilter healthcareEntity = new PropertyValueFilter(new String[]{"Encounter"}, "healthcareEntity", ValueComparator.IN,
                NominalValue.getInstance("EUH"), NominalValue.getInstance("CLH"), NominalValue.getInstance("WW"));
        encType.setAnd(healthcareEntity);

        /*
         * Nonsense filter for testing booleanValue
         */
        PropertyValueFilter fubarEntity = new PropertyValueFilter(new String[]{"Encounter"}, "FUBAR", ValueComparator.EQUAL_TO, BooleanValue.TRUE);
        healthcareEntity.setAnd(fubarEntity);

        /*
         * Nonsense filter for testing numberValue
         */
        PropertyValueFilter numberEntity = new PropertyValueFilter(new String[]{"Encounter"}, "measure", ValueComparator.LESS_THAN_OR_EQUAL_TO,
                NumberValue.getInstance(44));
        fubarEntity.setAnd(numberEntity);

        /*
         * Nonsense filter for testing inequalityValue
         */
        PropertyValueFilter inequalityEntity = new PropertyValueFilter(new String[]{"Encounter"}, "measure", ValueComparator.GREATER_THAN_OR_EQUAL_TO,
                InequalityNumberValue.parse("<44"));
        numberEntity.setAnd(inequalityEntity);

        /*
         * Nonsense filter for testing dateValue
         */
        PropertyValueFilter dateEntity = new PropertyValueFilter(new String[]{"Encounter"}, "measure", ValueComparator.LESS_THAN,
                DateValue.getInstance(new Date()));
        inequalityEntity.setAnd(dateEntity);

        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder();
        queryBuilder.setKeyIds(keyIds);
        queryBuilder.setFilters(timeRange);
        queryBuilder.setPropositionIds(PROP_IDS);
        DefaultQueryBuilder.setValidatePropositionIds(false);
        Query query = queryBuilder.build(new KnowledgeSourceImpl(new KnowledgeSourceBackend[0]), new AlgorithmSourceImpl(new AlgorithmSourceBackend[0]));
        return query;
    }

    public void testTableResultsQueryHandler() throws Throwable {
        BufferedWriter dataWriter = new BufferedWriter(new StringWriter());
        TableDestination destination = createTestTableDestination(dataWriter);
        File file = File.createTempFile("testTableResultsQueryHandler", ".xml");
        KnowledgeSource ks = new KnowledgeSourceImpl(new KnowledgeSourceBackend[0]);
        XMLConfiguration.writeTableDestinationAsXML(destination, file, true, ks);
        System.err.println(FileUtils.readFileToString(file));
        checkXMLValid(file, "protempa_tableQueryResultsHandler.xsd");
        TableDestination reconstitutedResultsHandler = XMLConfiguration.readTableQueryResultsHandlerFactoryAsXML(file, dataWriter, ks);
        assertEquals("Delimiter is the same", reconstitutedResultsHandler.getColumnDelimiter(), destination.getColumnDelimiter());
        assertArrayEquals("Column specs are the same", reconstitutedResultsHandler.getColumnSpecs(), destination.getColumnSpecs());
        assertArrayEquals("Row proposition ids are the same", reconstitutedResultsHandler.getRowPropositionIds(), destination.getRowPropositionIds());
        assertEquals("headerWritten is the same", reconstitutedResultsHandler.isHeaderWritten(), destination.isHeaderWritten());
    }

    private TableDestination createTestTableDestination(BufferedWriter dataWriter) throws QueryResultsHandlerInitException {
        String[] propIds1 = {"p1", "p2", "p3", "p4"};
        String[] propIds2 = {"q1", "q2", "q3"};
        PropertyConstraint constraint1 = new PropertyConstraint("foo", ValueComparator.EQUAL_TO, new BooleanValue(Boolean.FALSE));
        PropertyConstraint constraint2 = new PropertyConstraint("bar", ValueComparator.GREATER_THAN, new DateValue(new Date()));
        PropertyConstraint constraint3 = new PropertyConstraint("blech", ValueComparator.GREATER_THAN_OR_EQUAL_TO, new NominalValue("qwerty"));
        ValueList<NominalValue> valueList1 = new ValueList<>();
        valueList1.add(new NominalValue("fx"));
        valueList1.add(new NominalValue("a1"));
        PropertyConstraint constraint4 = new PropertyConstraint("gobo", ValueComparator.IN, valueList1);
        PropertyConstraint constraint5 = new PropertyConstraint("akwa", ValueComparator.LESS_THAN, new NumberValue(123));
        PropertyConstraint constraint6 = new PropertyConstraint("coom", ValueComparator.LESS_THAN_OR_EQUAL_TO, new InequalityNumberValue(ValueComparator.GREATER_THAN_OR_EQUAL_TO, 34));
        PropertyConstraint constraint7 = new PropertyConstraint("doga", ValueComparator.NOT_EQUAL_TO, new InequalityNumberValue(ValueComparator.LESS_THAN, 34));
        ValueList<NumberValue> valueList2 = new ValueList<>();
        valueList2.add(new NumberValue(77));
        valueList2.add(new NumberValue(86));
        PropertyConstraint constraint8 = new PropertyConstraint("ekro", ValueComparator.NOT_IN, valueList2);

        PropertyConstraint[] constraints1 = {constraint1, constraint2, constraint3, constraint4};
        PropertyConstraint[] constraints2 = {constraint5, constraint6, constraint7, constraint8};
        Value[] valueArray1 = {new NumberValue(4), new NumberValue(5), new NumberValue(7.3)};
        Value[] valueArray2 = {new NominalValue("asdf"), new NominalValue("opd")};
        Relation relation1 = new Relation(6, RelativeDayUnit.DAY, 4, RelativeHourUnit.HOUR, 2, AbsoluteTimeUnit.SECOND, 44, AbsoluteTimeUnit.MINUTE, 33, AbsoluteTimeUnit.HOUR, 144, AbsoluteTimeUnit.DAY, 14, AbsoluteTimeUnit.WEEK, 3, AbsoluteTimeUnit.YEAR);
        Relation relation2 = new Relation(6, RelativeDayUnit.DAY, 4, RelativeHourUnit.HOUR, 2, AbsoluteTimeUnit.MONTH, 44, AbsoluteTimeUnit.MINUTE, 33, AbsoluteTimeUnit.HOUR, 144, AbsoluteTimeUnit.DAY, 14, AbsoluteTimeUnit.WEEK, 3, AbsoluteTimeUnit.YEAR);
        KnowledgeSource ks = new KnowledgeSourceImpl(new KnowledgeSourceBackend[0]);
        Derivation derivation1 = new Derivation(propIds1, constraints1, new AllPropositionIntervalComparator(), 1, 6, null, Derivation.Behavior.MULT_BACKWARD, relation1);
        Derivation derivation2 = new Derivation(propIds2, constraints2, new AllPropositionIntervalComparator(), 3, 9, valueArray1, Derivation.Behavior.SINGLE_FORWARD, relation2);
        Derivation derivation3 = new Derivation(propIds1, constraints1, new AllPropositionIntervalComparator(), 8, 15, valueArray2, Derivation.Behavior.MULT_FORWARD, relation1);
        Derivation derivation4 = new Derivation(propIds1, constraints1, new AllPropositionIntervalComparator(), 8, 15, valueArray2, Derivation.Behavior.SINGLE_FORWARD, relation2);

        String[] referenceNames = {"foo", "bar", "blech"};
        Reference reference1 = new Reference(referenceNames, propIds1, constraints1, new AllPropositionIntervalComparator(), 1, 5);
        Reference reference2 = new Reference(new String[0], propIds2, constraints2, new AllPropositionIntervalComparator(), -1, -1);

        Link[] links = {derivation1, reference1, reference2, derivation2, derivation3, derivation4};
        AtLeastNColumnSpec atLeastN = new AtLeastNColumnSpec("An overridden name", 47, links, "ja", "nein");
        CountColumnSpec countColumnSpec = new CountColumnSpec("Different Name", links, true);
        DistanceBetweenColumnSpec secondsBetweenColumnSpec = new DistanceBetweenColumnSpec("pref", links, AbsoluteTimeUnit.SECOND);
        DistanceBetweenColumnSpec minutesBetweenColumnSpec = new DistanceBetweenColumnSpec("pref", links, AbsoluteTimeUnit.MINUTE);
        DistanceBetweenColumnSpec hoursBetweenColumnSpec = new DistanceBetweenColumnSpec("pref", links, AbsoluteTimeUnit.HOUR);
        DistanceBetweenColumnSpec daysBetweenColumnSpec = new DistanceBetweenColumnSpec("pref", links, AbsoluteTimeUnit.DAY);
        DistanceBetweenColumnSpec weeksBetweenColumnSpec = new DistanceBetweenColumnSpec("pref", links, AbsoluteTimeUnit.WEEK);
        DistanceBetweenColumnSpec monthsBetweenColumnSpec = new DistanceBetweenColumnSpec("pref", links, AbsoluteTimeUnit.MONTH);
        DistanceBetweenColumnSpec yearsBetweenColumnSpec = new DistanceBetweenColumnSpec("pref", links, AbsoluteTimeUnit.YEAR);
        OutputConfig outputConfig = new OutputConfig(true, true, true, true, true, true, true,
                "idHeading", "valueHeading", "displayNameHeading", "abbrevDisplayNameHeading", "startOrTimestampHeading", "finishHeading", "lengthHeading");
        ValueOutputConfig valueOutputConfig = new ValueOutputConfig(true, true, "Display Name", "Disp. Nm.");
        PropositionColumnSpec propositionColumnSpec = new PropositionColumnSpec("xPref", propIds1, outputConfig, valueOutputConfig, links, 5);
        PropositionValueColumnSpec maxPropositionValueColumnSpec = new PropositionValueColumnSpec("yPref", links, PropositionValueColumnSpec.Type.MAX);
        PropositionValueColumnSpec minPropositionValueColumnSpec = new PropositionValueColumnSpec("yPref", links, PropositionValueColumnSpec.Type.MIN);
        PropositionValueColumnSpec firstPropositionValueColumnSpec = new PropositionValueColumnSpec("yPref", links, PropositionValueColumnSpec.Type.FIRST);
        PropositionValueColumnSpec lastPropositionValueColumnSpec = new PropositionValueColumnSpec("yPref", links, PropositionValueColumnSpec.Type.LAST);
        PropositionValueColumnSpec sumPropositionValueColumnSpec = new PropositionValueColumnSpec("yPref", links, PropositionValueColumnSpec.Type.SUM);
        TableColumnSpec[] columnSpecs = new TableColumnSpec[]{
            atLeastN, countColumnSpec,
            secondsBetweenColumnSpec, minutesBetweenColumnSpec, hoursBetweenColumnSpec,
            daysBetweenColumnSpec, weeksBetweenColumnSpec, monthsBetweenColumnSpec, yearsBetweenColumnSpec,
            propositionColumnSpec, firstPropositionValueColumnSpec, lastPropositionValueColumnSpec,
            maxPropositionValueColumnSpec, minPropositionValueColumnSpec, sumPropositionValueColumnSpec
        };
        String[] rowPropositionIds = {"alpha", "beta", "gamma"};
        return new TableDestination(dataWriter, '\t', rowPropositionIds, columnSpecs, true);
    }
}
