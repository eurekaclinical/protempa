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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.datastore.DataStore;
import org.drools.WorkingMemory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.test.dataloading.DataInserter;
import org.protempa.test.dataloading.DataProviderException;
import org.protempa.test.dataloading.XlsxDataProvider;

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
            "Encounter", "AttendingPhysician", "CPTCode", "ICD9:Procedures",
            "ICD9:Diagnoses", "LAB:LabTest", "MED:medications", "VitalSign",
            "30DayReadmission", "No30DayReadmission" };

    /*
     * Instance of Protempa to run
     */
    private Protempa protempa;

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
        XlsxDataProvider provider = new XlsxDataProvider(new File(
                "src/test/resources/dsb/sample-data.xlsx"));
        DataInserter inserter = new DataInserter(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        inserter.createTables("src/test/resources/dsb/test-schema.sql");
        inserter.insertPatients(provider.getPatients());
        inserter.insertEncounters(provider.getEncounters());
        inserter.insertProviders(provider.getProviders());
        inserter.insertCptCodes(provider.getCptCodes());
        inserter.insertIcd9Diagnoses(provider.getIcd9Diagnoses());
        inserter.insertIcd9Procedures(provider.getIcd9Procedures());
        inserter.insertLabs(provider.getLabs());
        inserter.insertMedications(provider.getMedications());
        inserter.insertVitals(provider.getVitals());
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
        // testOutputResults();
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
            System.out.println(results.size());
            // assertEquals("data not expected size", 512, results.size());
            Map<String, Integer> propCounts = getResultCounts(PROP_COUNTS_FILE);
            // for (Entry<String, List<Proposition>> r : results.entrySet()) {
            // assertEquals("propositions for key " + r.getKey()
            // + " not expected", propCounts.get(r.getKey()), r
            // .getValue().size());
            // }
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
            Map<String, Integer> forwardDerivCounts = getResultCounts(FORWARD_DERIVATION_COUNTS_FILE);
            Map<String, Integer> backwardDerivCounts = getResultCounts(BACKWARD_DERIVATION_COUNTS_FILE);
            boolean foundId0 = false;
            for (Entry<String, WorkingMemory> r : results.entrySet()) {
                if (r.getKey().equals("0")) {
                    boolean foundEncId1 = false;
                    boolean foundEncId2 = false;
                    foundId0 = true;
                    System.out
                            .print("-----------------\nFORWARD\n---------------\n");
                    for (Entry<Proposition, List<Proposition>> e : afh
                            .getForwardDerivations(r.getKey()).entrySet()) {
                        if (e.getKey().getId().equals("Encounter")) {
                            if (e.getKey().getProperty("encounterId")
                                    .equals(NominalValue.getInstance("1"))) {
                                boolean found30DayReadmit = false;
                                foundEncId1 = true;
                                for (Proposition p : e.getValue()) {
                                    if (p.getId().equals("30DayReadmission")) {
                                        found30DayReadmit = true;
                                    }
                                }
                                assertTrue(
                                        "Did not find '30 Day Readmission' for encounter ID 1",
                                        found30DayReadmit);
                            }
                            if (e.getKey().getProperty("encounterId")
                                    .equals(NominalValue.getInstance("2"))) {
                                boolean found30DayReadmit = false;
                                foundEncId2 = true;
                                for (Proposition p : e.getValue()) {
                                    if (p.getId().equals("30DayReadmission")) {
                                        found30DayReadmit = true;
                                    }
                                }
                                assertTrue(
                                        "Did not find '30 Day Readmission' for encounter ID 2",
                                        found30DayReadmit);
                            }
                        }
                    }
                    assertTrue("Did not find encounter ID 1", foundEncId1);
                    assertTrue("Did not find encounter Id 2", foundEncId2);
                    // System.out
                    // .print("-----------------\nBACKWARD\n---------------\n");
                    // for (Entry<Proposition, List<Proposition>> e : afh
                    // .getBackwardDerivations(r.getKey()).entrySet()) {
                    // System.out.println(e.getKey().getId() + ": "
                    // + e.getValue());
                    // }
                }
                // assertEquals(
                // "wrong number of forward derivations for key "
                // + r.getKey(),
                // forwardDerivCounts.get(r.getKey()), afh
                // .getForwardDerivations(r.getKey()).size());
                // assertEquals("wrong number of backward derivations for key "
                // + r.getKey(), backwardDerivCounts.get(r.getKey()), afh
                // .getBackwardDerivations(r.getKey()).size());
            }
            assertTrue("Patient ID 0 not retrieved", foundId0);
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

}
