/*
 * #%L
 * Protempa Test Suite
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Iterators;
import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.io.UniqueDirectoryCreator;
import org.arp.javautil.io.WithBufferedReaderByLine;
import org.drools.WorkingMemory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.protempa.AbstractionFinderTestHelper;
import org.protempa.CloseException;
import org.protempa.CompoundLowLevelAbstractionDefinition;
import org.protempa.ValueClassification;
import org.protempa.CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator;
import org.protempa.ContextDefinition;
import org.protempa.EventDefinition;
import org.protempa.ExtendedPropositionDefinition;
import org.protempa.FinderException;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionValueDefinition;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.Protempa;
import org.protempa.ProtempaException;
import org.protempa.ProtempaStartupException;
import org.protempa.SimpleGapFunction;
import org.protempa.SlidingWindowWidthMode;
import org.protempa.SourceFactory;
import org.protempa.TemporalExtendedParameterDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.bconfigs.ini4j.INIConfigurations;
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.comparator.TemporalPropositionIntervalComparator;
import org.protempa.proposition.interval.Interval.Side;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Unit tests for Protempa.
 *
 * Persistent stores go into the directory in the system property
 * <code>java.io.tmpdir</code>.
 *
 * @author Michel Mansour
 */
public class ProtempaTest {

    private static final String ICD9_013_82 = "ICD9:013.82";
    private static final String ICD9_804 = "ICD9:804";
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
     * The ground truth output.
     */
    private static final String TRUTH_OUTPUT = TRUTH_DIR + "/output.txt";
    /**
     * The ground truth for the number of propositions for each key pulled from
     * the sample data
     */
    private static final String PROP_COUNTS_FILE = TRUTH_DIR
            + "/db-proposition-counts.txt";
    /**
     * The ground truth for the number of Encounter propositions for each key
     * pulled from the sample data
     */
    private static final String ENCOUNTER_COUNTS_FILE = TRUTH_DIR
            + "/db-encounter-counts.txt";
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
     * All proposition IDs in the sample data
     */
    private static final String[] PROP_IDS = {"Patient", "PatientAll",
        "Encounter", ICD9_013_82, ICD9_804, "VitalSign",
        "HELLP_FIRST_RECOVERING_PLATELETS", "LDH_TREND", "AST_STATE",
        "30DayReadmission", "No30DayReadmission", "MyDiagnosis",
        "MyVitalSign", "MyTemporalPattern", "MyAndLikePattern",
        "DiastolicBloodPressure", "SystolicBloodPressure",
        "MySystolicClassification", "MyDiastolicClassification",
        "MyBloodPressureClassificationAny",
        "MyBloodPressureClassificationConsecutiveAny",
        "MyBloodPressureClassificationAll",
        "MyTwoConsecutiveHighBloodPressure", "MySystolicClassification3",
        "MyDiastolicClassification3", "MyBloodPressureClassification3Any",
        "MyContext1", "MySystolicClassificationMyContext1"
    };
    /**
     * Vital signs
     */
    private static final String[] VITALS = {"BodyMassIndex",
        "DiastolicBloodPressure", "HeartRate", "O2Saturation",
        "RespiratoryRate", "SystolicBloodPressure", "TemperatureAxillary",
        "TemperatureCore", "TemperatureNOS", "TemperatureRectal",
        "TemperatureTympanic"};
    /**
     * Key IDs (testing purposes only...you know what I mean...for testing the
     * tests)
     */
    private static final String[] KEY_IDS = {"0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};

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
    private DataInserter inserter;

    /**
     * Performs set up operations required for all testing (eg, setting up the
     * in-memory database).
     *
     * @throws Exception if something goes wrong
     */
    @BeforeClass
    public static void setUpAll() throws Exception {
    }

    private void initializeProtempa() throws ProtempaStartupException,
            BackendProviderSpecLoaderException, ConfigurationsLoadException,
            InvalidConfigurationException, ConfigurationsNotFoundException {
        // force the use of the H2 driver so we don't bother trying to load
        // others
        System.setProperty("protempa.dsb.relationaldatabase.sqlgenerator",
                "org.protempa.backend.dsb.relationaldb.H2SQLGenerator");
        SourceFactory sf = new SourceFactory(
                new INIConfigurations(new File("src/test/resources")),
                "protege-h2-test-config");
        protempa = Protempa.newInstance(sf);
    }

    /**
     * Performs tear down operations once all testing is complete (eg, closing
     * Protempa)
     *
     * @throws Exception if something goes wrong
     */
    @AfterClass
    public static void tearDownAll() throws Exception {
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws DataProviderException, SQLException,
            ProtempaStartupException, BackendProviderSpecLoaderException,
            ConfigurationsLoadException, InvalidConfigurationException, 
            ConfigurationsNotFoundException {
        logger.log(Level.INFO, "Populating database");
        this.dataProvider = new XlsxDataProvider(new File(SAMPLE_DATA_FILE));
        inserter = new DataInserter("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        inserter.createTables("src/test/resources/dsb/test-schema.sql");
        this.patientCount = dataProvider.getPatients().size();
        inserter.insertPatients(dataProvider.getPatients());
        inserter.insertEncounters(dataProvider.getEncounters());
        inserter.insertProviders(dataProvider.getProviders());
        inserter.insertIcd9Diagnoses(dataProvider.getIcd9Diagnoses());
        inserter.insertIcd9Procedures(dataProvider.getIcd9Procedures());
        inserter.insertLabs(dataProvider.getLabs());
        inserter.insertVitals(dataProvider.getVitals());
        logger.log(Level.INFO, "Database populated");

        initializeProtempa();
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws SQLException, CloseException {
        if (this.protempa != null) {
            this.protempa.close();
        }
        try {
            this.inserter.truncateTables();
        } finally {
            this.inserter.close();
        }
    }

    /**
     * Tests the end-to-end execution of Protempa with persistence. Tests that
     * the results are expected at each stage of execution: retrieval,
     * processing, and output.
     */
    @Test
    public void testProtempaWithPersistence() throws IOException,
            ParseException {
        File dir = new UniqueDirectoryCreator().create("test-protempa", null,
                FileUtils.getTempDirectory());
        try {
            String environmentName = dir.getAbsolutePath();
            testRetrieveDataAndPersist(environmentName);
            testProcessResultsAndPersist(environmentName);
            testOutputResults(environmentName);
        } finally {
            FileUtils.deleteDirectory(dir);
        }
    }

    /**
     * Tests the end-to-end execution of Protempa without persistence. Only
     * verifies that the final output is correct.
     */
    @Test
    public void testProtempaWithoutPersistence() throws FinderException,
            ParseException, KnowledgeSourceReadException, QueryBuildException,
            IOException {
        File outputFile = File.createTempFile("protempa-test", null);
        FileWriter fw = new FileWriter(outputFile);
        QueryResultsHandler handler = new SingleColumnQueryResultsHandler(fw);
        protempa.execute(query(), handler);
        assertTrue("output doesn't match",
                outputMatches(outputFile, TRUTH_OUTPUT));
    
    }
    
    private ContextDefinition context1() {
        ContextDefinition cd = new ContextDefinition("MyContext1");
        TemporalExtendedParameterDefinition tepd =
                new TemporalExtendedParameterDefinition("MyDiastolicClassification");
        tepd.setValue(NominalValue.getInstance("My Diastolic High"));
        cd.setInducedBy(new TemporalExtendedPropositionDefinition[]{tepd});
        return cd;
    }
    
    private PropositionDefinition systolicClassificationMyContext1() {
        LowLevelAbstractionDefinition systolic = 
                new LowLevelAbstractionDefinition(
                "MySystolicClassificationMyContext1");
        systolic.setPropositionId("MySystolicClassification");
        systolic.setContextId("MyContext1");
        systolic.setDisplayName("My Systolic Classification");
        systolic.setAlgorithmId("stateDetector");
        systolic.addPrimitiveParameterId("SystolicBloodPressure");
        systolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        systolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        systolic.setMaximumGapBetweenValues(24);
        systolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_HIGH");
        sysHigh.setValue(NominalValue.getInstance("My Systolic High"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(130));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_NORMAL");
        sysNormal.setValue(NominalValue.getInstance("My Systolic Normal"));
        sysNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(130));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);

        return systolic;
    }

    private PropositionDefinition systolicClassification() {
        LowLevelAbstractionDefinition systolic = new LowLevelAbstractionDefinition(
                "MySystolicClassification");
        systolic.setDisplayName("My Systolic Classification");
        systolic.setAlgorithmId("stateDetector");
        systolic.addPrimitiveParameterId("SystolicBloodPressure");
        systolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        systolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        systolic.setMaximumGapBetweenValues(24);
        systolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_HIGH");
        sysHigh.setValue(NominalValue.getInstance("My Systolic High"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(140));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_NORMAL");
        sysNormal.setValue(NominalValue.getInstance("My Systolic Normal"));
        sysNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(140));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);

        return systolic;
    }

    private PropositionDefinition systolicClassification3() {
        LowLevelAbstractionDefinition systolic = new LowLevelAbstractionDefinition(
                "MySystolicClassification3");

        systolic.setDisplayName("My Systolic Classification 3");
        systolic.setAlgorithmId("stateDetector");
        systolic.addPrimitiveParameterId("SystolicBloodPressure");
        systolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        systolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        systolic.setMaximumGapBetweenValues(24);
        systolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_HIGH_3");
        sysHigh.setValue(NominalValue.getInstance("My Systolic High 3"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(140));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysLow = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_LOW_3");
        sysLow.setValue(NominalValue.getInstance("My Systolic Low 3"));
        sysLow.setParameterValue("maxThreshold", NumberValue.getInstance(60));
        sysLow.setParameterComp("maxThreshold",
                ValueComparator.LESS_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_NORMAL_3");
        sysNormal.setValue(NominalValue.getInstance("My Systolic Normal 3"));
        sysNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(140));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);
        sysNormal
                .setParameterValue("minThreshold", NumberValue.getInstance(60));
        sysNormal
                .setParameterComp("minThreshold", ValueComparator.GREATER_THAN);

        return systolic;
    }

    private LowLevelAbstractionDefinition diastolicClassification() {
        LowLevelAbstractionDefinition diastolic = new LowLevelAbstractionDefinition(
                "MyDiastolicClassification");
        diastolic.setDisplayName("My Diastolic Classification");
        diastolic.setAlgorithmId("stateDetector");
        diastolic.addPrimitiveParameterId("DiastolicBloodPressure");
        diastolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        diastolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        diastolic.setMaximumGapBetweenValues(24);
        diastolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition diasHigh = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_HIGH");
        diasHigh.setValue(NominalValue.getInstance("My Diastolic High"));
        diasHigh.setParameterValue("minThreshold", NumberValue.getInstance(90));
        diasHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition diasNormal = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_NORMAL");
        diasNormal.setValue(NominalValue.getInstance("My Diastolic Normal"));
        diasNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(90));
        diasNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);

        return diastolic;
    }

    private PropositionDefinition diastolicClassification3() {
        LowLevelAbstractionDefinition diastolic = new LowLevelAbstractionDefinition(
                "MyDiastolicClassification3");

        diastolic.setDisplayName("My Diastolic Classification 3");
        diastolic.setAlgorithmId("stateDetector");
        diastolic.addPrimitiveParameterId("DiastolicBloodPressure");
        diastolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        diastolic.setGapFunction(new SimpleGapFunction(24,
                AbsoluteTimeUnit.HOUR));
        diastolic.setMaximumGapBetweenValues(24);
        diastolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_HIGH_3");
        sysHigh.setValue(NominalValue.getInstance("My Diastolic High 3"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(90));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysLow = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_LOW_3");
        sysLow.setValue(NominalValue.getInstance("My Diastolic Low 3"));
        sysLow.setParameterValue("maxThreshold", NumberValue.getInstance(30));
        sysLow.setParameterComp("maxThreshold",
                ValueComparator.LESS_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_NORMAL_3");
        sysNormal.setValue(NominalValue.getInstance("My Diastolic Normal 3"));
        sysNormal
                .setParameterValue("maxThreshold", NumberValue.getInstance(90));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);
        sysNormal
                .setParameterValue("minThreshold", NumberValue.getInstance(30));
        sysNormal
                .setParameterComp("minThreshold", ValueComparator.GREATER_THAN);

        return diastolic;
    }

    private PropositionDefinition bloodPressureClassificationConsecutiveAny() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationConsecutiveAny");
        bp.setDisplayName("My Blood Pressure Classification (ANY - 2)");
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(ValueDefinitionMatchOperator.ANY);
        bp.setMinimumNumberOfValues(2);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private PropositionDefinition bloodPressureClassificationAny() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationAny");
        bp.setDisplayName("My Blood Pressure Classification (ANY)");
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(ValueDefinitionMatchOperator.ANY);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private PropositionDefinition bloodPressureClassificationAll() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationAll");
        bp.setDisplayName("My Blood Pressure Classification (ALL)");
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(ValueDefinitionMatchOperator.ALL);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private PropositionDefinition bloodPressureClassification3Any() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassification3Any");
        bp.setDisplayName("My Blood Pressure Classification 3 (ANY)");
        bp.addValueClassification(new ValueClassification("MYBP3_HIGH", "MySystolicClassification3",
                "My Systolic High 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_HIGH", "MyDiastolicClassification3",
                "My Diastolic High 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_LOW", "MySystolicClassification3",
                "My Systolic Low 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_LOW", "MyDiastolicClassification3",
                "My Diastolic Low"));
        bp.addValueClassification(new ValueClassification("MYBP3_NORMAL", "MySystolicClassification3",
                "My Systolic Normal 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_NORMAL", "MyDiastolicClassification3",
                "My Diastolic Normal 3"));
        bp.setValueDefinitionMatchOperator(ValueDefinitionMatchOperator.ANY);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private Query query() throws ParseException, KnowledgeSourceReadException,
            QueryBuildException {
        DefaultQueryBuilder q = new DefaultQueryBuilder();

        q.setKeyIds(KEY_IDS);
        q.setPropositionIds(PROP_IDS);

        EventDefinition ed = new EventDefinition("MyDiagnosis");
        ed.setDisplayName("My Diagnosis");
        ed.setInverseIsA("ICD9:907.1");

        PrimitiveParameterDefinition pd = new PrimitiveParameterDefinition(
                "MyVitalSign");
        pd.setDisplayName("My Vital Sign");
        pd.setInverseIsA("HeartRate");

        HighLevelAbstractionDefinition hd = new HighLevelAbstractionDefinition(
                "MyTemporalPattern");
        hd.setDisplayName("My Temporal Pattern");
        TemporalExtendedPropositionDefinition td1 = new TemporalExtendedPropositionDefinition(
                ed.getId());
        TemporalExtendedPropositionDefinition td2 = new TemporalExtendedPropositionDefinition(
                pd.getId());
        hd.add(td1);
        hd.add(td2);
        Relation rel = new Relation();
        hd.setRelation(td1, td2, rel);

        HighLevelAbstractionDefinition hd2 = new HighLevelAbstractionDefinition(
                "MyAndLikePattern");
        hd.setDisplayName("My Or-like Pattern");
        ExtendedPropositionDefinition epd1 = new ExtendedPropositionDefinition(
                "ICD9:V-codes");
        ExtendedPropositionDefinition epd2 = new ExtendedPropositionDefinition(
                "ICD9:35.83");
        hd2.add(epd1);
        hd2.add(epd2);

        HighLevelAbstractionDefinition highBp = new HighLevelAbstractionDefinition(
                "MyTwoConsecutiveHighBloodPressure");
        highBp.setDisplayName("My Two Consecutive High Blood Pressure");
        TemporalExtendedParameterDefinition highBpTpd = new TemporalExtendedParameterDefinition(
                "MyBloodPressureClassificationConsecutiveAny");
        highBpTpd.setValue(NominalValue.getInstance("MYBP_HIGH"));
        highBp.add(highBpTpd);
        Relation highBpRel = new Relation();
        highBp.setRelation(highBpTpd, highBpTpd, highBpRel);

        q.setPropositionDefinitions(new PropositionDefinition[]{ed, pd, hd,
                    hd2, systolicClassification(), diastolicClassification(),
                    bloodPressureClassificationAny(),
                    bloodPressureClassificationConsecutiveAny(), highBp,
                    bloodPressureClassificationAll(), systolicClassification3(),
                    diastolicClassification3(), bloodPressureClassification3Any(),
                    context1(), systolicClassificationMyContext1()});

        DateFormat shortFormat = AbsoluteTimeGranularity.DAY.getShortFormat();
        DateTimeFilter timeRange = new DateTimeFilter(
                new String[]{"Encounter"}, shortFormat.parse("08/01/2006"),
                AbsoluteTimeGranularity.DAY, shortFormat.parse("08/31/2011"),
                AbsoluteTimeGranularity.DAY, Side.START, Side.START);

        q.setFilters(timeRange);
        Query query = protempa.buildQuery(q);

        return query;
    }

    private static Map<String, Integer> getResultCounts(String filename)
            throws NumberFormatException, IOException {
        final HashMap<String, Integer> result = new HashMap<String, Integer>();
        new WithBufferedReaderByLine(filename) {
            @Override
            public void readLine(String line) {
                String[] count = line.split(":");
                result.put(count[0], Integer.parseInt(count[1]));
            }
        }.execute();

        return result;
    }

    /**
     * Tests Protempa's retrieve and persist method.
     */
    private void testRetrieveDataAndPersist(String environmentName) {
        DataStore<String, List<Proposition>> results = null;
        try {
            results = new PropositionStoreCreator(environmentName)
                    .getPersistentStore();
            protempa.retrieveDataAndPersist(query(), environmentName);

            assertEquals("Wrong number of keys retrieved", this.patientCount,
                    results.size());
            assertRawPropositionCount(results);
            assertPatientsRetrieved(results);
            assertEncountersRetrieved(results);
            assertVitalsRetrieved(results);
            assertReferencesRetrieved(results);
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

    private void printDerivations(String keyId, AbstractionFinderTestHelper afh) {
        System.out.println("----- FORWARD DERIVATIONS -----");
        for (List<Proposition> derivs : afh.getForwardDerivations(keyId)
                .values()) {
            for (Proposition p : derivs) {
                System.out.println(p.getId());
            }
        }
        System.out.println("----- BACKWARD DERIVATIONS -----");
        for (List<Proposition> derivs : afh.getBackwardDerivations(keyId)
                .values()) {
            for (Proposition p : derivs) {
                System.out.println(p.getId());
            }
        }
    }

    /**
     * Tests Protempa's process and persist method.
     */
    private void testProcessResultsAndPersist(String environmentName)
            throws ParseException {
        DataStore<String, WorkingMemory> results = null;
        AbstractionFinderTestHelper afh = null;
        try {
            afh = new AbstractionFinderTestHelper(environmentName);
            results = afh.processStoredResults(protempa, query(),
                    environmentName);
            assertEquals("Wrong number of working memories generated",
                    this.patientCount, results.size());
            Map<String, Integer> forwardDerivCounts = 
                    getResultCounts(FORWARD_DERIVATION_COUNTS_FILE);
            Map<String, Integer> backwardDerivCounts = 
                    getResultCounts(BACKWARD_DERIVATION_COUNTS_FILE);
            for (String keyId : results.keySet()) {
                if (keyId.equals("12")) {
                    System.err.println("OUTPUT FOR " + keyId + " IS " + Iterators.asList(results.get(keyId).iterateObjects()));
                }
                int derivCount = 0;
                for (List<Proposition> derivs : afh
                        .getForwardDerivations(keyId).values()) {
                    derivCount += derivs.size();
                }
                assertEquals("wrong number of forward derivations for key "
                        + keyId, forwardDerivCounts.get(keyId).intValue(), 
                        derivCount);

                derivCount = 0;
                for (List<Proposition> derivs : afh.getBackwardDerivations(
                        keyId).values()) {
                    derivCount += derivs.size();
                }
                assertEquals("wrong number of backward derivations for key "
                        + keyId, backwardDerivCounts.get(keyId).intValue(), 
                        derivCount);

                assert30DayReadmissionDerived(afh);
                assertNo30DayReadmissionDerived(afh);
                assertChildIcd9Derived(results, afh);
                assertLdhTrendDerived(results);
                assertAstStateDerived(results);
                assertFirstHellpRecoveringSliceDerived(results);
                assertMyDiagnosisDerived(results);
                assertMyLabTestDerived(results);
                assertOrLikePatternDerived(results);
                assertCompoundBloodPressureHighAllDerived(results);
                assertConsecutiveCompoundBloodPressureAnyDerived(results);
                assertTwoConsecutiveElevatedBloodPressureDerived(results);
                assertCompoundBloodPressure3ClassificationsDerived(results);
                assertCompoundBloodPressureArbitraryIntervalsDerived(results);
            }
        } catch (KnowledgeSourceReadException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (FinderException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (ProtempaException e) {
            e.printStackTrace();
            fail(AF_ERROR_MSG);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (afh != null) {
                afh.cleanUp();
            }
            if (results != null) {
                results.shutdown();
            }
        }
    }

    /**
     * Tests Protempa's output method
     */
    private void testOutputResults(String environmentName) {
        FileWriter fw = null;
        File outputFile = null;
        try {
            outputFile = File.createTempFile("protempa-test", null);
            fw = new FileWriter(outputFile);
            QueryResultsHandler handler = new SingleColumnQueryResultsHandler(
                    fw);
            protempa.outputResults(query(), handler, environmentName);
            System.err
                    .println("output written to " + 
                    outputFile.getAbsolutePath());
            assertTrue("output doesn't match",
                    outputMatches(outputFile, TRUTH_OUTPUT));
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
                System.err.println("Failed to close file: " + outputFile);
            }
        }
    }

    private boolean outputMatches(File file1, String file2) throws IOException {
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

        // this accounts for the short-circuiting in the while loop above
        // when line1 == null, we won't execute br2.readLine()
        boolean retval = false;
        if (line1 == null && line2 != null) {
            line2 = br2.readLine();
            if (line2 == null) {
                retval = true;
            } else {
                retval = false;
            }
        }

        br1.close();
        br2.close();

        return retval;
    }

    private void testOutputResultsNewKeysProps() {
        fail("Not yet implemented");
    }

    private void assertEncountersRetrieved(
            DataStore<String, List<Proposition>> objectGraph) {
        logger.log(Level.INFO, "Running encounters test...");
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
                    patientEncounterMap.get(e.getKey()).intValue(), 
                    encounters.size());
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
        logger.log(Level.INFO, "Completed encounters test");
    }

    private void assertPatientsRetrieved(
            DataStore<String, List<Proposition>> objectGraph) {
        logger.log(Level.INFO, "Running patients test...");
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
            checkPatient(patientMap.get(keyId), onlyProp(patientProp),
                    onlyProp(patientAllProp));
        }
        logger.log(Level.INFO, "Completed patients test");
    }

    private Proposition onlyProp(Set<Proposition> singletonSet) {
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

    private void assertReferencesRetrieved(
            DataStore<String, List<Proposition>> objectGraph)
            throws IOException {
        logger.log(Level.INFO, "Running references test...");
        Map<String, Integer> propCounts = getResultCounts(ENCOUNTER_COUNTS_FILE);
        for (Map.Entry<String, Integer> me : propCounts.entrySet()) {
            String keyId = me.getKey();
            List<Proposition> props = objectGraph.get(keyId);
            for (Proposition prop : props) {
                if (prop.getId().equals("PatientAll")) {
                    assertEquals("PatientAll for keyId " + keyId + " failed", 1,
                            prop.getReferences("patientDetails").size());
                } else if (prop.getId().equals("Patient")) {
                    assertEquals("Patient for keyId " + keyId + " failed", me
                            .getValue().intValue(), 
                            prop.getReferences("encounters").size());
                }
            }
        }

        logger.log(Level.INFO, "Completed references test");
    }

    private void assertVitalsRetrieved(
            DataStore<String, List<Proposition>> objectGraph) {
        logger.log(Level.INFO, "Running vitals test...");
        for (String vitalSign : VITALS) {
            for (Patient patient : this.dataProvider.getPatients()) {
                checkPatientVitalSign(patient.getId(), vitalSign, objectGraph);
            }
        }
        logger.log(Level.INFO, "Completed vitals test");
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
        assertTrue("Value sets not equal for vital sign " + vitalSign
                + " for key ID " + keyId,
                dataVitalValues.equals(retrievedVitalValues));
    }

    private void assert30DayReadmissionDerived(AbstractionFinderTestHelper afh) {
        logger.log(Level.INFO, "Running 30DayReadmissions test...");
        Map<Proposition, List<Proposition>> encDerivations = getDerivedPropositionsForKey(
                "Encounter", afh.getForwardDerivations("0"));
        for (Entry<Proposition, List<Proposition>> prop : encDerivations
                .entrySet()) {
            Proposition encounter = prop.getKey();
            String encounterId = encounter.getProperty("encounterId")
                    .getFormatted();
            int count30DayReadmit = 0;

            for (Proposition derived : prop.getValue()) {
                if (derived.getId().equals("30DayReadmission")) {
                    count30DayReadmit++;
                }
            }

            if (encounterId.equals("1") || encounterId.equals("2")) {
                assertEquals(
                        "Did not find exactly 1 '30DayReadmission' for encounter ID 1",
                        1, count30DayReadmit);
            } else {
                assertEquals(
                        "Found some '30DayReadmission' propositions for encounter ID "
                        + encounterId, 0, count30DayReadmit);
            }
        }
        logger.log(Level.INFO, "Completed 30DayReadmissions test");
    }

    private void assertNo30DayReadmissionDerived(AbstractionFinderTestHelper afh) {
        logger.log(Level.INFO, "Running No30DayReadmissions test...");
        Map<Proposition, List<Proposition>> no30DayDerivations = getDerivedPropositionsForKey(
                "No30DayReadmission", afh.getBackwardDerivations("0"));
        assertEquals(
                "Found wrong number of 'No30DayReadmissions' for key ID 0", 3,
                no30DayDerivations.size());
        logger.log(Level.INFO, "Completed No30DayReadmissions test");
    }

    private void assertMyDiagnosisDerived(
            DataStore<String, WorkingMemory> derivedData) {
        boolean myDiagnosisDerived = false;
        for (Iterator<Proposition> it = derivedData.get("1").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("MyDiagnosis")) {
                myDiagnosisDerived = true;
                break;
            }
        }
        assertTrue("Proposition 'MyDiagnosis' not found", myDiagnosisDerived);
    }

    private void assertOrLikePatternDerived(
            DataStore<String, WorkingMemory> derivedData) {
        String[] ptIds = {"0", "1", "2", "8", "10"};
        boolean[] result = {false, false, true, false, false};
        for (int i = 0; i < ptIds.length; i++) {
            String ptId = ptIds[i];
            boolean orLikePatternDerived = false;
            for (Iterator<Proposition> it = derivedData.get(ptId)
                    .iterateObjects(); it.hasNext();) {
                Proposition p = it.next();
                if (p.getId().equals("MyAndLikePattern")) {
                    orLikePatternDerived = true;
                    break;
                }
            }
            assertEquals("Proposition 'MyAndLikePattern' not found", result[i],
                    orLikePatternDerived);
        }
    }

    private void assertMyLabTestDerived(
            DataStore<String, WorkingMemory> derivedData) {
        boolean myLabTestDerived = false;
        for (Iterator<Proposition> it = derivedData.get("3").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("MyVitalSign")) {
                myLabTestDerived = true;
                break;
            }
        }
        assertTrue("Proposition 'MyVitalSign' not found", myLabTestDerived);
    }

    private void assertMyBloodPressureDerived(
            DataStore<String, WorkingMemory> derivedData) {
    }

    private void assertChildIcd9Derived(
            DataStore<String, WorkingMemory> derivedData,
            AbstractionFinderTestHelper afh) {
        logger.log(Level.INFO, "Running ICD9 test...");
        boolean icd9d01382Derived = false;
        boolean icd9d804Derived = false;

        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("0").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals(ICD9_013_82)) {
                icd9d01382Derived = true;
            } else if (p.getId().equals(ICD9_804)) {
                icd9d804Derived = true;
            }

        }

        // matched exactly - should be no derivations
        assertTrue("Proposition '" + ICD9_013_82 + "' not found",
                icd9d01382Derived);
        assertEquals(
                "Proposition '" + ICD9_013_82
                + "' should not have any forward derivations",
                0,
                getDerivedPropositionsForKey(ICD9_013_82,
                afh.getForwardDerivations("0")).size());
        assertEquals(
                "Proposition '" + ICD9_013_82
                + "' should not have any backward derivations",
                0,
                getDerivedPropositionsForKey(ICD9_013_82,
                afh.getBackwardDerivations("0")).size());
        assertTrue("Proposition '" + ICD9_804 + "' not found", icd9d804Derived);

        // matched at higher level - should be derivations
        String[] icd9Levels = new String[]{"ICD9:804", "ICD9:804.3",
            "ICD9:804.34"};
        Set<String> expectedForwardDerivationsLevel0 = Arrays
                .asSet(new String[]{});
        Set<String> expectedBackwardDerivationsLevel0 = Arrays
                .asSet(new String[]{icd9Levels[1]});
        Set<String> expectedForwardDerivationsLevel1 = Arrays
                .asSet(new String[]{icd9Levels[0]});
        Set<String> expectedBackwardDerivationsLevel1 = Arrays
                .asSet(new String[]{icd9Levels[2]});
        Set<String> expectedForwardDerivationsLevel2 = Arrays
                .asSet(new String[]{icd9Levels[1]});
        Set<String> expectedBackwardDerivationsLevel2 = Arrays
                .asSet(new String[]{});
        Map<String, Set<String>> expectedForwardDerivations = new HashMap<String, Set<String>>();
        expectedForwardDerivations.put(icd9Levels[0],
                expectedForwardDerivationsLevel0);
        expectedForwardDerivations.put(icd9Levels[1],
                expectedForwardDerivationsLevel1);
        expectedForwardDerivations.put(icd9Levels[2],
                expectedForwardDerivationsLevel2);
        Map<String, Set<String>> expectedBackwardDerivations = new HashMap<String, Set<String>>();
        expectedBackwardDerivations.put(icd9Levels[0],
                expectedBackwardDerivationsLevel0);
        expectedBackwardDerivations.put(icd9Levels[1],
                expectedBackwardDerivationsLevel1);
        expectedBackwardDerivations.put(icd9Levels[2],
                expectedBackwardDerivationsLevel2);

        for (String icd9Code : icd9Levels) {
            Set<String> foundForwardDerivations = new HashSet<String>();
            Set<String> foundBackwardDerivations = new HashSet<String>();
            for (Entry<Proposition, List<Proposition>> p : getDerivedPropositionsForKey(
                    icd9Code, afh.getForwardDerivations("0")).entrySet()) {
                for (Proposition p2 : p.getValue()) {
                    foundForwardDerivations.add(p2.getId());
                }
            }
            for (Entry<Proposition, List<Proposition>> p : getDerivedPropositionsForKey(
                    icd9Code, afh.getBackwardDerivations("0")).entrySet()) {
                for (Proposition p2 : p.getValue()) {
                    foundBackwardDerivations.add(p2.getId());
                }
            }

            assertEquals("Wrong forward derivations found for proposition '"
                    + icd9Code + "'", expectedForwardDerivations.get(icd9Code),
                    foundForwardDerivations);
            assertEquals("Wrong backward derivations found for proposition '"
                    + icd9Code + "'",
                    expectedBackwardDerivations.get(icd9Code),
                    foundBackwardDerivations);
        }
        logger.log(Level.INFO, "Completed ICD9 test");
    }

    private Date buildDate(int year, int month, int dayOfMonth, int hour,
            int minute, int ampm) {
        Calendar date = Calendar.getInstance();

        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date.set(Calendar.HOUR, hour);
        date.set(Calendar.MINUTE, minute);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.set(Calendar.AM_PM, ampm);

        return date.getTime();
    }

    private void assertDateStringEquals(Date expected, String actual,
            SimpleDateFormat format, String testFailureMsg, String parseErrMsg) {
        try {
            assertEquals(testFailureMsg, expected, format.parse(actual));
        } catch (ParseException e) {
            e.printStackTrace();
            fail(parseErrMsg);
        }
    }

    private void assertLdhTrendDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO, "Running LDH_TREND test...");
        Set<AbstractParameter> ldhTrends = new HashSet<AbstractParameter>();

        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("0").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("LDH_TREND")) {
                ldhTrends.add((AbstractParameter) p);
            }
        }

        assertEquals("Did not derive correct number of 'LDH_TREND'", 2,
                ldhTrends.size());

        boolean foundInc = false;
        boolean foundDec = false;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy h:mm aa");
        for (AbstractParameter ldhTrend : ldhTrends) {
            Date expectedStart = null;
            Date expectedFinish = null;
            if (ldhTrend.getValueFormatted().equals("Increasing LDH")) {
                expectedStart = buildDate(2006, Calendar.AUGUST, 26, 9, 38,
                        Calendar.PM);
                expectedFinish = buildDate(2006, Calendar.AUGUST, 28, 10, 45,
                        Calendar.AM);
                assertDateStringEquals(expectedStart,
                        ldhTrend.getStartFormattedLong(), sdf,
                        "Wrong start time for 'INCREASING_LDH'",
                        "Unable to parse start time for 'INCREASING_LDH': "
                        + ldhTrend.getStartFormattedLong());
                assertDateStringEquals(expectedFinish,
                        ldhTrend.getFinishFormattedLong(), sdf,
                        "Wrong finish time for 'INCREASING_LDH'",
                        "Unable to parse finish time for 'INCREASING_LDH': "
                        + ldhTrend.getFinishFormattedLong());
                foundInc = true;
            } else if (ldhTrend.getValueFormatted().equals("Decreasing LDH")) {
                expectedStart = buildDate(2006, Calendar.AUGUST, 26, 8, 35,
                        Calendar.AM);
                expectedFinish = buildDate(2006, Calendar.AUGUST, 26, 9, 38,
                        Calendar.PM);
                assertDateStringEquals(expectedStart,
                        ldhTrend.getStartFormattedLong(), sdf,
                        "Wrong start time for 'DECREASING_LDH'",
                        "Unable to parse start time for 'DECREASING_LDH': "
                        + ldhTrend.getStartFormattedLong());
                assertDateStringEquals(expectedFinish,
                        ldhTrend.getFinishFormattedLong(), sdf,
                        "Wrong finish time for 'DECREASING_LDH'",
                        "Unable to parse finish time for 'DECREASING_LDH': "
                        + ldhTrend.getFinishFormattedLong());
                foundDec = true;
            } else {
                fail("Found 'LDH_TREND' with unknown value: "
                        + ldhTrend.getValueFormatted()
                        + ". Should be 'INCREASING_LDH' or 'DECREASING_LDH'");
            }
        }

        assertTrue("Did not find the increasing 'LDH_TREND'", foundInc);
        assertTrue("Did not find the decreasing 'LDH_TREND'", foundDec);

        logger.log(Level.INFO, "Completed LDH_TREND test");
    }

    private void assertAstStateDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO, "Running AST_STATE test...");
        Set<AbstractParameter> astStates = new HashSet<AbstractParameter>();

        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("0").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("AST_STATE")) {
                astStates.add((AbstractParameter) p);
            }
        }

        assertEquals("Wrong number of 'AST_STATE' derived", 4, astStates.size());

        boolean foundLowAstState = false;
        boolean foundNormalAstState = false;
        boolean foundHighAstState = false;
        boolean foundVeryHighAstState = false;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy h:mm aa");
        for (AbstractParameter astState : astStates) {
            Date expectedStart = null;
            Date expectedFinish = null;
            if (astState.getValueFormatted().equals("Normal AST")) {
                expectedStart = buildDate(2007, Calendar.FEBRUARY, 28, 0, 50,
                        Calendar.AM);
                expectedFinish = buildDate(2007, Calendar.FEBRUARY, 28, 7, 49,
                        Calendar.AM);
                assertDateStringEquals(expectedStart,
                        astState.getStartFormattedLong(), sdf,
                        "Wrong start time for 'NORMAL_AST'",
                        "Unable to parse start time for 'NORMAL_AST': "
                        + astState.getStartFormattedLong());
                assertDateStringEquals(expectedFinish,
                        astState.getFinishFormattedLong(), sdf,
                        "Wrong finish time for 'NORMAL_AST'",
                        "Unable to parse finish time for 'NORMAL_AST': "
                        + astState.getFinishFormattedLong());
                foundNormalAstState = true;
            } else if (astState.getValueFormatted().equals("Low AST")) {
                expectedStart = buildDate(2007, Calendar.FEBRUARY, 28, 8, 49,
                        Calendar.PM);
                expectedFinish = buildDate(2007, Calendar.FEBRUARY, 28, 8, 49,
                        Calendar.PM);
                assertDateStringEquals(
                        expectedStart,
                        astState.getStartFormattedLong(),
                        sdf,
                        "Wrong start time for 'LOW_AST'",
                        "Unable to parse start time for 'LOW_AST': "
                        + astState.getStartFormattedLong());
                assertDateStringEquals(expectedFinish,
                        astState.getFinishFormattedLong(), sdf,
                        "Wrong finish time for 'LOW_AST'",
                        "Unable to parse finish time for 'LOW_AST': "
                        + astState.getFinishFormattedLong());
                foundLowAstState = true;
            } else if (astState.getValueFormatted().equals("High AST")) {
                expectedStart = buildDate(2007, Calendar.FEBRUARY, 28, 9, 52,
                        Calendar.PM);
                expectedFinish = buildDate(2007, Calendar.MARCH, 1, 8, 32,
                        Calendar.PM);
                assertDateStringEquals(expectedStart,
                        astState.getStartFormattedLong(), sdf,
                        "Wrong start time for 'HIGH_AST'",
                        "Unable to parse start time for 'HIGH_AST': "
                        + astState.getStartFormattedLong());
                assertDateStringEquals(expectedFinish,
                        astState.getFinishFormattedLong(), sdf,
                        "Wrong finish time for 'HIGH_AST'",
                        "Unable to parse finish time for 'HIGH_AST': "
                        + astState.getFinishFormattedLong());
                foundHighAstState = true;
            } else if (astState.getValueFormatted().equals("Very High AST")) {
                expectedStart = buildDate(2007, Calendar.MARCH, 2, 2, 5,
                        Calendar.PM);
                expectedFinish = buildDate(2007, Calendar.MARCH, 2, 2, 5,
                        Calendar.PM);
                assertDateStringEquals(expectedStart,
                        astState.getStartFormattedLong(), sdf,
                        "Wrong start time for 'VERY_HIGH_AST'",
                        "Unable to parse start time for 'VERY_HIGH_AST': "
                        + astState.getStartFormattedLong());
                assertDateStringEquals(expectedFinish,
                        astState.getFinishFormattedLong(), sdf,
                        "Wrong finish time for 'VERY_HIGH_AST'",
                        "Unable to parse finish time for 'VERY_HIGH_AST': "
                        + astState.getFinishFormattedLong());
                foundVeryHighAstState = true;
            } else {
                fail("Found 'AST_STATE' with unknown value "
                        + astState.getValueFormatted()
                        + ". Should be 'NORMAL_AST', 'LOW_AST', 'HIGH_AST', or 'VERY_HIGH_AST'");
            }
        }

        assertTrue("Failed to find 'AST_STATE' with value 'NORMAL_AST'",
                foundNormalAstState);
        assertTrue("Failed to find 'AST_STATE' with value 'LOW_AST'",
                foundLowAstState);
        assertTrue("Failed to find 'AST_STATE' with value 'HIGH_AST'",
                foundHighAstState);
        assertTrue("Failed to find 'AST_STATE' with value 'VERY_HIGH_AST'",
                foundVeryHighAstState);

        logger.log(Level.INFO, "Completed AST_STATE test");
    }

    private void assertFirstHellpRecoveringSliceDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO,
                "Running HELLP_FIRST_RECOVERING_PLATELETS test...");
        Set<Proposition> hellpFirstRecoverings = new HashSet<Proposition>();
        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("11").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("HELLP_FIRST_RECOVERING_PLATELETS")) {
                hellpFirstRecoverings.add(p);
            }
        }
        assertEquals(
                "Found wrong number of 'HELLP_FIRST_RECOVERING_PLATELETS'", 1,
                hellpFirstRecoverings.size());
        AbstractParameter hellpFirstRecovering = (AbstractParameter) onlyProp(hellpFirstRecoverings);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy h:mm aa");
        Date expectedStart = buildDate(2010, Calendar.MAY, 12, 8, 47,
                Calendar.PM);
        Date expectedFinish = buildDate(2010, Calendar.MAY, 13, 11, 30,
                Calendar.AM);
        assertDateStringEquals(expectedStart,
                hellpFirstRecovering.getStartFormattedLong(), sdf,
                "Wrong start time for 'HELLP_FIRST_RECOVERING_PLATELETS'",
                "Unable to parse start time for 'HELLP_FIRST_RECOVERING_PLATELETS': "
                + hellpFirstRecovering.getStartFormattedLong());
        assertDateStringEquals(expectedFinish,
                hellpFirstRecovering.getFinishFormattedLong(), sdf,
                "Wrong finish time for 'HELLP_FIRST_RECOVERING_PLATELETS'",
                "Unable to parse finish time for 'HELLP_FIRST_RECOVERING_PLATELETS': "
                + hellpFirstRecovering.getFinishFormattedLong());

        logger.log(Level.INFO,
                "Completed HELLP_FIRST_RECOVERING_PLATELETS test");
    }

    private void assertConsecutiveCompoundBloodPressureAnyDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO,
                "Running MyBloodPressureClassificationConsecutiveAny test...");
        Set<Proposition> bps = new HashSet<Proposition>();
        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("12").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("MyBloodPressureClassificationConsecutiveAny")) {
                bps.add(p);
            }
        }
        assertEquals(
                "Found wrong number of 'MyBloodPressureClassificationConsecutiveAny'",
                1, bps.size());
        AbstractParameter bp = (AbstractParameter) onlyProp(bps);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy h:mm aa");
        Date expectedStart = buildDate(2009, Calendar.SEPTEMBER, 9, 8, 0,
                Calendar.AM);
        Date expectedFinish = buildDate(2009, Calendar.OCTOBER, 10, 8, 0,
                Calendar.AM);
        assertDateStringEquals(
                expectedStart,
                bp.getStartFormattedLong(),
                sdf,
                "Wrong start time for 'MyBloodPressureClassificationConsecutiveAny'",
                "Unable to parse start time for 'MyBloodPressureClassificationConsecutiveAny': "
                + bp.getStartFormattedLong());
        assertDateStringEquals(
                expectedFinish,
                bp.getFinishFormattedLong(),
                sdf,
                "Wrong finish time for 'MyBloodPressureClassificationConsecutiveAny'",
                "Unable to parse finish time for 'MyBloodPressureClassificationConsecutiveAny': "
                + bp.getFinishFormattedLong());
        logger.log(Level.INFO,
                "Completed MyBloodPressureClassificationConsecutiveAny test");
    }

    private void assertTwoConsecutiveElevatedBloodPressureDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO,
                "Running MyTwoConsecutiveHighBloodPressure test...");
        Set<Proposition> bps = new HashSet<Proposition>();
        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("12").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("MyTwoConsecutiveHighBloodPressure")) {
                bps.add(p);
            }
        }
        assertEquals(
                "Found wrong number of 'MyTwoConsecutiveHighBloodPressure'", 1,
                bps.size());
        AbstractParameter bp = (AbstractParameter) onlyProp(bps);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy h:mm aa");
        Date expectedStart = buildDate(2009, Calendar.SEPTEMBER, 9, 8, 0,
                Calendar.AM);
        Date expectedFinish = buildDate(2009, Calendar.OCTOBER, 10, 8, 0,
                Calendar.AM);
        assertDateStringEquals(expectedStart, bp.getStartFormattedLong(), sdf,
                "Wrong start time for 'MyTwoConsecutiveHighBloodPressure'",
                "Unable to parse start time for 'MyTwoConsecutiveHighBloodPressure': "
                + bp.getStartFormattedLong());
        assertDateStringEquals(expectedFinish, bp.getFinishFormattedLong(),
                sdf, "Wrong finish time for 'MyTwoConsecutiveBloodPressure'",
                "Unable to parse finish time for 'MyTwoConsecutiveBloodPressure': "
                + bp.getFinishFormattedLong());
        logger.log(Level.INFO,
                "Completed MyTwoConsecutiveHighBloodPressure test");
    }

    private void assertCompoundBloodPressureHighAllDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO,
                "Running MyBloodPressureClassificationAll test...");
        List<AbstractParameter> bps = new ArrayList<AbstractParameter>();
        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("13").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("MyBloodPressureClassificationAll")) {
                bps.add((AbstractParameter) p);
            }
        }
        assertEquals(
                "Found wrong number of 'MyBloodPressureClassificationAll'", 1,
                bps.size());
        Collections.sort(bps, new TemporalPropositionIntervalComparator());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy h:mm aa");

        AbstractParameter bp1 = bps.get(0);
        Date expectedStart1 = buildDate(2008, Calendar.AUGUST, 8, 10, 0,
                Calendar.AM);
        Date expectedFinish1 = buildDate(2008, Calendar.AUGUST, 8, 10, 0,
                Calendar.AM);
        assertDateStringEquals(
                expectedStart1,
                bp1.getStartFormattedLong(),
                sdf,
                "Wrong start time for 'MyBloodPressureClassificationAll' (first)",
                "Unable to parse start time for 'MyBloodPressureClassificationAll' (first): "
                + bp1.getStartFormattedLong());
        assertDateStringEquals(
                expectedFinish1,
                bp1.getFinishFormattedLong(),
                sdf,
                "Wrong finish time for 'MyBloodPressureClassificationAll' (first)",
                "Unable to parse finish time for 'MyBloodPressureClassificationAll' (first): "
                + bp1.getFinishFormattedLong());
        assertEquals(
                "Wrong value for 'MyBloodPressureClassificationAll' (first)",
                NominalValue.getInstance("MYBP_HIGH"), bp1.getValue());

        AbstractParameter bp2 = bps.get(0);
        Date expectedStart2 = buildDate(2008, Calendar.AUGUST, 8, 10, 0,
                Calendar.AM);
        Date expectedFinish2 = buildDate(2008, Calendar.AUGUST, 8, 10, 0,
                Calendar.AM);
        assertDateStringEquals(
                expectedStart2,
                bp2.getStartFormattedLong(),
                sdf,
                "Wrong start time for 'MyBloodPressureClassificationAll' (second)",
                "Unable to parse start time for 'MyBloodPressureClassificationAll (second)': "
                + bp2.getStartFormattedLong());
        assertDateStringEquals(
                expectedFinish2,
                bp2.getFinishFormattedLong(),
                sdf,
                "Wrong finish time for 'MyBloodPressureClassificationAll' (second)",
                "Unable to parse finish time for 'MyBloodPressureClassificationAll' (second): "
                + bp2.getFinishFormattedLong());
        assertEquals(
                "Wrong value for 'MyBloodPressureClassificationAll' (second)",
                NominalValue.getInstance("MYBP_HIGH"), bp2.getValue());

        logger.log(Level.INFO,
                "Completed MyBloodPressureClassificationAll test");
    }

    private void assertCompoundBloodPressure3ClassificationsDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO,
                "Running MyBloodPressureClassification3Any test...");

        List<AbstractParameter> bps = new ArrayList<AbstractParameter>();
        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("14").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("MyBloodPressureClassification3Any")) {
                bps.add((AbstractParameter) p);
            }
        }
        assertEquals(
                "Found wrong number of 'MyBloodPressureClassification3Any'", 2,
                bps.size());
        Collections.sort(bps, new TemporalPropositionIntervalComparator());
        AbstractParameter bp1 = bps.get(0);
        assertEquals(
                "Wrong value for 'MyBloodPressureClassification3Any' (first)",
                NominalValue.getInstance("MYBP3_LOW"), bp1.getValue());

        AbstractParameter bp2 = bps.get(1);
        assertEquals(
                "Wrong value for 'MyBloodPressureClassification3Any' (second)",
                NominalValue.getInstance("MYBP3_HIGH"), bp2.getValue());

        logger.log(Level.INFO,
                "Completed MyBloodPressureClassification3Any test");
    }

    private void assertCompoundBloodPressureArbitraryIntervalsDerived(
            DataStore<String, WorkingMemory> derivedData) {
        logger.log(Level.INFO,
                "Running MyBloodPressureClassificationAny with arbitrary intervals test...");
        List<AbstractParameter> bps = new ArrayList<AbstractParameter>();
        for (@SuppressWarnings("unchecked") Iterator<Proposition> it = derivedData.get("15").iterateObjects(); it
                .hasNext();) {
            Proposition p = it.next();
            if (p.getId().equals("MyBloodPressureClassificationAny")) {
                bps.add((AbstractParameter) p);
            }
        }
        assertEquals(
                "Found wrong number of 'MyBloodPressureClassificationAny'", 3,
                bps.size());
        Set<AbstractParameter> normals = new HashSet<AbstractParameter>();
        Set<AbstractParameter> highs = new HashSet<AbstractParameter>();
        for (AbstractParameter p : bps) {
            if (p.getValue().equals(NominalValue.getInstance("MYBP_HIGH"))) {
                highs.add(p);
            } else if (p.getValue().equals(
                    NominalValue.getInstance("MYBP_NORMAL"))) {
                normals.add(p);
            } else {
                fail("Unexpected value for 'MyBloodPressureClassificationAny':"
                        + p.getValueFormatted());
            }
        }
        assertEquals(
                "Found wrong number of 'MyBloodPressureClassificationAny' with value 'MYBP_HIGH'",
                1, highs.size());
        assertEquals(
                "Found wrong number of 'MyBloodPressureClassificationAny' with value 'MYBP_NORMAL'",
                2, normals.size());

        logger.log(Level.INFO,
                "Completed MyBloodPressureClassificationAny with arbitrary intervals test");
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
            if (prop.getKey().getId().equals(propId)) {
                results.put(prop.getKey(), prop.getValue());
            }
        }

        return results;
    }

    private void assertRawPropositionCount(DataStore<String, List<Proposition>> results) throws IOException, NumberFormatException {
        Map<String, Integer> propCounts = getResultCounts(PROP_COUNTS_FILE);
        for (Entry<String, List<Proposition>> r : results.entrySet()) {
            assertEquals(
                    "Wrong number of raw propositions retrieved for key "
                    + r.getKey(),
                    propCounts.get(r.getKey()).intValue(), 
                    r.getValue().size());
        }
    }
}
