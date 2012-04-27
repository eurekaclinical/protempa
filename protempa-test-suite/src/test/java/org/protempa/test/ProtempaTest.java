/*
 * #%L
 * Protempa Test Suite
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
package org.protempa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.datastore.DataStore;
import org.drools.WorkingMemory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.protempa.AbstractionFinderTestHelper;
import org.protempa.FinderException;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.Protempa;
import org.protempa.ProtempaException;
import org.protempa.ProtempaStartupException;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Unit tests for Protempa.
 * 
 * @author Michel Mansour
 */
public class ProtempaTest {

    private static final String QUERY_ERROR_MSG = "Failed to build query";
    private static final String AF_ERROR_MSG = "Exception thrown by AbstractionFinder";

    private static Logger logger = Logger.getLogger(ProtempaTest.class
            .getName());

    /**
     * Sample data file
     */
    private static final String SAMPLE_DATA_FILE = "src/test/resources/dsb/sample-data.xlsx";

    /**
     * The ground truth results directory for the test data
     */
    private static final String TRUTH_DIR = "src/test/resources/truth/";

    /**
     * The ground truth for the number of propositions for each key pulled from
     * the sample data
     */
    private static final String PROP_COUNTS_FILE = TRUTH_DIR
            + "/db-proposition-counts.txt";

    /**
     * The ground truth for the number of forward derivations for each key
     * processed from the sample data
     */
    private static final String FORWARD_DERIVATION_COUNTS_FILE = TRUTH_DIR
            + "/forward-derivations.txt";

    /**
     * The ground truth for the number of backward derivations for each key
     * processed from the sample data
     */
    private static final String BACKWARD_DERIVATION_COUNTS_FILE = TRUTH_DIR
            + "/backward-derivations.txt";

    /**
     * Where to keep the persistent stores
     */
    private static final String STORE_HOME = "src/test/resources/store";

    /**
     * Name of the persistent storage environment
     */
    private static final String STORE_ENV_NAME = "test-store";

    /**
     * Name of the persistent store for the data retrieved from the data source.
     */
    private static final String RETRIEVAL_STORE_NAME = "test-retrieve";

    /**
     * Name of the persistent store for data after rules processing
     */
    private static final String WORKING_MEMORY_STORE_NAME = "working-memory-store";

    /**
     * Name of the output file
     */
    private static final String OUTPUT_FILENAME = "src/test/resources/output/test-output";

    /**
     * All proposition IDs in the sample data
     */
    private static final String[] PROP_IDS = { "Patient", "PatientAll",
            "Encounter",
            // "AttendingPhysician",
            // "CPTCode",
            // "ICD9:Procedures",
            // "ICD9:Diagnoses",
            // "LAB:LabTest",
            // "MED:medications",
            "VitalSign",
    // "LAB_HELLP_PLATELETS", "HELLP_RECOVERING_PLATELETS",
    // "HELLP_FIRST_RECOVERING_PLATELETS",
    // "HELLP_SECOND_RECOVERING_PLATELETS",
    // "LAB:LDH",
    // "30DayReadmission", "No30DayReadmission"
    };

    /**
     * Vital signs
     */
    private static final String[] VITALS = { "BodyMassIndex",
            "DiastolicBloodPressure", "HeartRate", "O2Saturation",
            "RespiratoryRate", "SystolicBloodPressure", "TemperatureAxillary",
            "TemperatureCore", "TemperatureNOS", "TemperatureRectal",
            "TemperatureTympanic" };

    /**
     * Key IDs (testing purposes only...you know what I mean)
     */
    private static final String[] KEY_IDS = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10", "11" };

    /*
     * Date format used by the data source
     */
    private static final SimpleDateFormat SDF = new SimpleDateFormat(
            "yyyy.MM.dd HH:mm:ss");

    /*
     * Instance of Protempa to run
     */
    private Protempa protempa;

    /*
     * Spreadsheet reader and data provider
     */
    private DataProvider dataProvider;

    /*
     * Number of patients in the dataset
     */
    private int patientCount;

    /**
     * Performs set up operations required for all testing (eg, setting up the
     * in-memory database).
     * 
     * @throws Exception
     *             if something goes wrong
     */
    @BeforeClass
    public static void setUpAll() throws Exception {

    }

    private void populateDatabase() throws DataProviderException, SQLException {
        logger.log(Level.INFO, "Populating database");
        this.dataProvider = new XlsxDataProvider(new File(SAMPLE_DATA_FILE));
        DataInserter inserter = new DataInserter(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        inserter.createTables("src/test/resources/dsb/test-schema.sql");
        this.patientCount = dataProvider.getPatients().size();
        inserter.insertPatients(dataProvider.getPatients());
        inserter.insertEncounters(dataProvider.getEncounters());
        inserter.insertProviders(dataProvider.getProviders());
        inserter.insertCptCodes(dataProvider.getCptCodes());
        inserter.insertIcd9Diagnoses(dataProvider.getIcd9Diagnoses());
        inserter.insertIcd9Procedures(dataProvider.getIcd9Procedures());
        inserter.insertLabs(dataProvider.getLabs());
        inserter.insertMedications(dataProvider.getMedications());
        inserter.insertVitals(dataProvider.getVitals());
        inserter.close();
        logger.log(Level.INFO, "Database populated");
    }

    private void initializeProtempa() throws ProtempaStartupException {
        System.setProperty("protempa.inicommonsconfigurations.pathname",
                "src/test/resources");
        // force the use of the H2 driver so we don't bother trying to load
        // others
        System.setProperty("protempa.dsb.relationaldatabase.sqlgenerator",
                "org.protempa.bp.commons.dsb.relationaldb.H2SQLGenerator");

        File storeHome = new File(STORE_HOME);
        logger.log(Level.INFO, "Clearing out persistent storage files");
        deleteDir(storeHome);
        storeHome.mkdir();
        logger.log(Level.INFO, "Persistent storage area clear");

        // system properties for caching and persistence
        System.setProperty("java.io.tmpdir", STORE_HOME);
        System.setProperty("store.env.name", STORE_ENV_NAME);

        protempa = Protempa.newInstance("protege-h2-test-config");
    }

    private void deleteDir(File path) {
        if (path.exists()) {
            if (path.isDirectory()) {
                for (File f : path.listFiles()) {
                    deleteDir(f);
                }
            } else {
                path.delete();
            }
        }
    }

    /**
     * Performs tear down operations once all testing is complete (eg, closing
     * Protempa)
     * 
     * @throws Exception
     *             if something goes wrong
     */
    @AfterClass
    public static void tearDownAll() throws Exception {

    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        populateDatabase();
        initializeProtempa();
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        protempa.close();
    }

    /**
     * Tests the end-to-end execution of Protempa.
     */
    @Test
    public void testProtempa() {
        testRetrieveDataAndPersist();
        testProcessResultsAndPersist();
        testOutputResults();
    }

    private Query query() throws ParseException, KnowledgeSourceReadException,
            QueryBuildException {
        DefaultQueryBuilder q = new DefaultQueryBuilder();

        q.setPropIds(PROP_IDS);
        DateTimeFilter timeRange = new DateTimeFilter(
                new String[] { "Encounter" }, AbsoluteTimeGranularity.DAY
                        .getShortFormat().parse("08/01/2006"),
                AbsoluteTimeGranularity.DAY, AbsoluteTimeGranularity.DAY
                        .getShortFormat().parse("08/31/2011"),
                AbsoluteTimeGranularity.DAY, Side.START, Side.START);

        String[] meds = protempa.getKnowledgeSource()
                .inDataSourcePropositionIds("MED:medications")
                .toArray(new String[0]);
        PropertyValueFilter medOrderStatus = new PropertyValueFilter(meds,
                "orderStatus", ValueComparator.NOT_IN, new NominalValue(
                        "Canceled"), new NominalValue("Deleted"),
                new NominalValue("Incomplete"), new NominalValue(
                        "VoidedWithResults"));
        timeRange.setAnd(medOrderStatus);

        PropertyValueFilter medOrderNotContinuing = new PropertyValueFilter(
                meds, "continuingOrder", ValueComparator.IN, new NominalValue(
                        "Modify"), new NominalValue("Order"));

        medOrderStatus.setAnd(medOrderNotContinuing);

        q.setFilters(timeRange);
        Query query = protempa.buildQuery(q);

        return query;
    }

    private static Map<String, Integer> getResultCounts(String filename)
            throws NumberFormatException, IOException {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] count = line.split(":");
            result.put(count[0], Integer.parseInt(count[1]));
        }

        return result;
    }

    /**
     * Tests Protempa's retrieve and persist method.
     */
    private void testRetrieveDataAndPersist() {
        DataStore<String, List<Proposition>> results = null;
        try {
            protempa.retrieveDataAndPersist(query(), RETRIEVAL_STORE_NAME);
            results = PropositionStoreCreator.getInstance().getPersistentStore(
                    RETRIEVAL_STORE_NAME);
            assertEquals("data not expected size", this.patientCount,
                    results.size());
            Map<String, Integer> propCounts = getResultCounts(PROP_COUNTS_FILE);
            // for (Entry<String, List<Proposition>> r : results.entrySet()) {
            // assertEquals("propositions for key " + r.getKey()
            // + " not expected", propCounts.get(r.getKey()), r
            // .getValue().size());
            // }
            // assertPatientsRetrieved(results);
            assertEncountersRetrieved(results);
            // assertVitalsRetrieved(results);
        } catch (FinderException ex) {
            ex.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (ParseException ex) {
            ex.printStackTrace();
            fail(QUERY_ERROR_MSG);
        } catch (KnowledgeSourceReadException ex) {
            ex.printStackTrace();
            fail(QUERY_ERROR_MSG);
        } catch (QueryBuildException ex) {
            ex.printStackTrace();
            fail(QUERY_ERROR_MSG);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (results != null) {
                results.shutdown();
            }
        }
    }

    private void testProcessResultsAndPersistQueryNewKeysProps() {
        fail("Not yet implemented");
    }

    /**
     * Tests Protempa's process and persist method.
     */
    private void testProcessResultsAndPersist() {
        AbstractionFinderTestHelper afh = new AbstractionFinderTestHelper(
                WORKING_MEMORY_STORE_NAME);
        DataStore<String, WorkingMemory> results = null;
        try {
            results = afh.processStoredResults(protempa, null,
                    Arrays.asSet(PROP_IDS), null, RETRIEVAL_STORE_NAME);
            // assertEquals("wrong number of working memories", 512,
            // results.size());
            System.out.println(results.size());
            Map<String, Integer> forwardDerivCounts = getResultCounts(FORWARD_DERIVATION_COUNTS_FILE);
            Map<String, Integer> backwardDerivCounts = getResultCounts(BACKWARD_DERIVATION_COUNTS_FILE);
            boolean foundId0 = false;
            for (Entry<String, WorkingMemory> r : results.entrySet()) {
                assert30DayReadmissionDerived(afh);
                System.out
                        .print("-----------------\nFORWARD\n---------------\n");
                for (Entry<Proposition, List<Proposition>> e : afh
                        .getForwardDerivations(r.getKey()).entrySet()) {

                }

                System.out
                        .print("-----------------\nBACKWARD\n---------------\n");
                for (Entry<Proposition, List<Proposition>> e : afh
                        .getBackwardDerivations(r.getKey()).entrySet()) {

                }
                // }
                // assertEquals(
                // "wrong number of forward derivations for key "
                // + r.getKey(),
                // forwardDerivCounts.get(r.getKey()), afh
                // .getForwardDerivations(r.getKey()).size());
                // assertEquals("wrong number of backward derivations for key "
                // + r.getKey(), backwardDerivCounts.get(r.getKey()), afh
                // .getBackwardDerivations(r.getKey()).size());
            }
            // assertTrue("Patient ID 0 not retrieved", foundId0);
        } catch (KnowledgeSourceReadException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (FinderException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (ProtempaException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            afh.cleanUp();
            if (results != null) {
                results.shutdown();
            }
        }
    }

    /**
     * Tests Protempa's output method
     */
    private void testOutputResults() {
        FileWriter fw = null;
        try {
            fw = new FileWriter(OUTPUT_FILENAME);
            QueryResultsHandler handler = new SingleColumnQueryResultsHandler(
                    fw);
            protempa.outputResults(query(), handler, WORKING_MEMORY_STORE_NAME);
            // assertTrue("output doesn't match",
            // outputMatches(OUTPUT_FILENAME, TRUTH_FILE));
        } catch (FinderException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (IOException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (KnowledgeSourceReadException e) {
            e.printStackTrace();
            fail(QUERY_ERROR_MSG);
        } catch (QueryBuildException e) {
            e.printStackTrace();
            fail(QUERY_ERROR_MSG);
        } catch (ParseException e) {
            e.printStackTrace();
            fail(QUERY_ERROR_MSG);
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                System.err.println("Failed to close file: " + OUTPUT_FILENAME);
            }
        }
    }

    private boolean outputMatches(String file1, String file2)
            throws IOException {
        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        BufferedReader br2 = new BufferedReader(new FileReader(file2));
        String line1 = null, line2 = null;

        while ((line1 = br1.readLine()) != null
                && ((line2 = br2.readLine()) != null)) {
            if (!line1.equals(line2)) {
                br1.close();
                br2.close();
                return false;
            }
        }
        br1.close();
        br2.close();
        return ((line1 == null && line2 == null));
    }

    private void testOutputResultsNewKeysProps() {
        fail("Not yet implemented");
    }

    private void assertEncountersRetrieved(
            DataStore<String, List<Proposition>> objectGraph) {
        Map<String, Integer> patientEncounterMap = new HashMap<String, Integer>();
        Map<String, Encounter> encountersMap = new HashMap<String, Encounter>();
        for (Encounter e : this.dataProvider.getEncounters()) {
            encountersMap.put(e.getId().toString(), e);
            if (patientEncounterMap.containsKey(e.getPatientId().toString())) {
                patientEncounterMap.put(e.getPatientId().toString(),
                        1 + patientEncounterMap
                                .get(e.getPatientId().toString()));
            } else {
                patientEncounterMap.put(e.getPatientId().toString(), 1);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy");
        for (Entry<String, List<Proposition>> e : objectGraph.entrySet()) {
            Set<Proposition> encounters = getPropositionsForKey(e.getKey(),
                    "Encounter", objectGraph);
            assertEquals("Wrong number of encounters for key ID " + e.getKey(),
                    patientEncounterMap.get(e.getKey()), encounters.size());
            for (Proposition enc : encounters) {
                Event event = (Event) enc;
                Encounter encounter = encountersMap.get(event.getProperty(
                        "encounterId").getFormatted());
                assertEquals(
                        "Wrong start time for encounter " + encounter.getId()
                                + " for key ID " + encounter.getPatientId(),
                        sdf.format(encounter.getStart()),
                        event.getStartFormattedLong());
                assertEquals(
                        "Wrong finish time for encounter " + encounter.getId()
                                + " for key ID " + encounter.getPatientId(),
                        sdf.format(encounter.getEnd()),
                        event.getFinishFormattedLong());
            }
        }
    }

    private void assertPatientsRetrieved(
            DataStore<String, List<Proposition>> objectGraph) {
        Map<String, Patient> patientMap = new HashMap<String, Patient>();

        for (Patient p : this.dataProvider.getPatients()) {
            patientMap.put(p.getId().toString(), p);
        }
        for (String keyId : KEY_IDS) {
            assertTrue("Key ID " + keyId + " not retrieved",
                    objectGraph.containsKey(keyId));
            Set<Proposition> patientProp = getPropositionsForKey(keyId,
                    "Patient", objectGraph);
            Set<Proposition> patientAllProp = getPropositionsForKey(keyId,
                    "PatientAll", objectGraph);
            assertEquals("Should be exactly 1 Patient proposition, got "
                    + patientProp.size() + " for key ID " + keyId, 1,
                    patientProp.size());
            assertEquals("Should be exactly 1 PatientAll proposition, got "
                    + patientAllProp.size() + " for key ID " + keyId, 1,
                    patientAllProp.size());
            checkPatient(patientMap.get(keyId), firstProp(patientProp),
                    firstProp(patientAllProp));
        }
    }

    private Proposition firstProp(Set<Proposition> singletonSet) {
        return singletonSet.iterator().next();
    }

    private void checkPatient(Patient patient, Proposition patientProp,
            Proposition patientAllProp) {
        String id = patient.getId().toString();
        assertEquals("Patient " + id + " first name",
                NominalValue.getInstance(patient.getFirstName()),
                patientProp.getProperty("firstName"));
        assertEquals("Patient " + id + "  last name",
                NominalValue.getInstance(patient.getLastName()),
                patientProp.getProperty("lastName"));
        assertEquals("Patient " + id + " date of birth",
                DateValue.getInstance(patient.getDateOfBirth()),
                patientProp.getProperty("dateOfBirth"));
        assertEquals("Patient " + id + " race",
                NominalValue.getInstance(patient.getRace()),
                patientProp.getProperty("race"));
        assertEquals("Patient " + id + " gender",
                NominalValue.getInstance(patient.getGender()),
                patientProp.getProperty("gender"));
    }

    private Map<Long, Long> mapEncountersToPatients() {
        Map<Long, Long> result = new HashMap<Long, Long>();

        for (Encounter e : this.dataProvider.getEncounters()) {
            result.put(e.getId(), e.getPatientId());
        }

        return result;
    }

    private void assertVitalsRetrieved(
            DataStore<String, List<Proposition>> objectGraph) {
        for (String vitalSign : VITALS) {
            for (Patient patient : this.dataProvider.getPatients()) {
                checkPatientVitalSign(patient.getId(), vitalSign, objectGraph);
            }
        }
    }

    private void checkPatientVitalSign(Long keyId, String vitalSign,
            DataStore<String, List<Proposition>> objectGraph) {
        Set<Proposition> retrievedVitals = getPropositionsForKey(
                keyId.toString(), vitalSign, objectGraph);
        List<Vital> dataVitals = new ArrayList<Vital>(
                this.dataProvider.getVitals());
        Map<Long, Long> enc2Pat = mapEncountersToPatients();
        for (Iterator<Vital> it = dataVitals.iterator(); it.hasNext();) {
            Vital v = it.next();
            if (!v.getEntityId().equals(vitalSign)
                    || !enc2Pat.get(v.getEncounterId()).equals(keyId)) {
                it.remove();
            }
        }
        assertEquals("Wrong number of " + vitalSign + " instances for key ID "
                + keyId, dataVitals.size(), retrievedVitals.size());
        Set<String> dataVitalValues = new HashSet<String>();
        for (Vital v : dataVitals) {
            dataVitalValues.add(v.getResultAsStr());
        }
        Set<String> retrievedVitalValues = new HashSet<String>();
        for (Proposition p : retrievedVitals) {
            retrievedVitalValues.add(((PrimitiveParameter) p)
                    .getValueFormatted());
        }
        for (String value : retrievedVitalValues) {
            System.out.println(value);
        }
        assertTrue("Value sets not equal for vital sign " + vitalSign
                + " for key ID " + keyId,
                dataVitalValues.equals(retrievedVitalValues));
    }

    private void assert30DayReadmissionDerived(AbstractionFinderTestHelper afh) {
        Map<Proposition, List<Proposition>> encDerivations = getDerivedPropositionsForKey(
                "Encounter", afh.getForwardDerivations("0"));
        for (Entry<Proposition, List<Proposition>> prop : encDerivations.entrySet()) {
            Proposition encounter = prop.getKey();
            System.out.println("Encounter? " + encounter.getId());
            String encounterId = encounter.getProperty("encounterId")
                    .getFormatted();
            int count30DayReadmit = 0;

            for (Proposition derived : prop.getValue()) {
                if (derived.getId().equals("30DayReadmission")) {
                    count30DayReadmit++;
                }
            }

            if (encounterId.equals("1")) {
                assertEquals(
                        "Did not find exactly 1 '30DayReadmission' for encounter ID 1",
                        1, count30DayReadmit);
            } else {
                assertEquals(
                        "Found some '30DayReadmission' propositions for encounter ID "
                                + encounterId, 0, count30DayReadmit);
            }
        }

    }

    private void assertNo30DayReadmissionDerived(
            DataStore<String, List<Proposition>> derivedData) {

    }

    private void assertParentIcd9Derived(
            DataStore<String, List<Proposition>> derivedData) {

    }

    private void assertLdhTrendDerived(
            DataStore<String, List<Proposition>> derivedData) {

    }

    private void assertAstStateDerived(
            DataStore<String, List<Proposition>> derivedData) {

    }

    private void assertFirstHellpRecoveringSliceDerived(
            DataStore<String, List<Proposition>> derivedData) {

    }

    private Set<Proposition> getPropositionsForKey(String keyId, String propId,
            DataStore<String, List<Proposition>> props) {
        Set<Proposition> results = new HashSet<Proposition>();

        List<Proposition> values = props.get(keyId);
        for (Proposition p : values) {
            if (p.getId().equals(propId)) {
                results.add(p);
            }
        }

        return results;
    }

    private Map<Proposition, List<Proposition>> getDerivedPropositionsForKey(
            String propId, Map<Proposition, List<Proposition>> derivations) {
        Map<Proposition, List<Proposition>> results = new HashMap<Proposition, List<Proposition>>();
        for (Entry<Proposition, List<Proposition>> prop : derivations
                .entrySet()) {
            if (prop.getKey().equals(propId)) {
                results.put(prop.getKey(), prop.getValue());
            }
        }
        
        return results;
    }
}
