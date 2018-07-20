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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.io.IOUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.protempa.Protempa;
import org.protempa.ProtempaException;
import org.protempa.SourceFactory;
import org.protempa.bconfigs.ini4j.INIConfigurations;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.protempa.dest.Destination;
import org.protempa.query.QueryMode;

/**
 * Unit tests for Protempa.
 *
 * Persistent stores go into the directory in the system property
 * <code>java.io.tmpdir</code>.
 *
 * @author Michel Mansour
 */
public class ProtempaWithPersistenceTest {

    /**
     * The ground truth results directory for the test data
     */
    private static final String TRUTH_DIR = "src/test/resources/truth/";
    /**
     * The ground truth output.
     */
    private static final String TRUTH_OUTPUT = TRUTH_DIR + "/output.txt";
    
    private static final String TRUTH_OUTPUT_REPROCESS = TRUTH_DIR + "/output-reprocess.txt";
    
    private static File TEMP_DIR;
    
    

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        // force the use of the H2 driver so we don't bother trying to load
        // others
        System.setProperty("protempa.dsb.relationaldatabase.sqlgenerator",
                "org.protempa.backend.dsb.relationaldb.h2.H2SQLGenerator");
        TEMP_DIR = Files.createTempDirectory(null).toFile();
    }

    /**
     * @throws Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        TEMP_DIR = null;
    }

    /**
     * Tests the end-to-end execution of Protempa with persistence. Only
     * verifies that the final output is correct.
     * @throws java.io.IOException if file writing fails.
     * @throws org.protempa.ProtempaException if the Protempa run fails.
     */
    @Test
    public void testProtempaWithPersistence() throws IOException, ProtempaException {

        SourceFactory sf = new SourceFactory(
                new INIConfigurations(new File("src/test/resources")),
                "protege-h2-test-config");
        try (Protempa protempa = Protempa.newInstance(sf)) {
            DefaultQueryBuilder q = new QueryBuilderFactory().getInstance();
            
            q.setDatabasePath(TEMP_DIR.getPath());
            Query query = protempa.buildQuery(q);

            File outputFile = 
                    File.createTempFile("protempa-test-with-persistence", null);
            try (BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile))) {
                Destination destination = new SingleColumnDestination(fw);
                protempa.execute(query, destination);
            }
            outputMatches(outputFile, TRUTH_OUTPUT);
        }
    }

    /**
     * Tests the end-to-end execution of Protempa with persistence, 
     * reprocessing the output of {@link #testProtempaWithPersistence() }. Only
     * verifies that the final output is correct.
     * @throws java.io.IOException if file writing fails.
     * @throws org.protempa.ProtempaException if the Protempa run fails.
     */
    @Test
    public void testProtempaWithPersistenceReprocess() throws IOException, ProtempaException {

        SourceFactory sf = new SourceFactory(
                new INIConfigurations(new File("src/test/resources")),
                "protege-h2-test-config");
        try (Protempa protempa = Protempa.newInstance(sf)) {
            DefaultQueryBuilder q = new QueryBuilderFactory().getInstance();
            q.setDatabasePath(TEMP_DIR.getPath());
            q.setQueryMode(QueryMode.REPROCESS);
            Query query = protempa.buildQuery(q);

            File outputFile = 
                    File.createTempFile("protempa-test-with-persistence-reprocess", null);
            try (BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile))) {
                Destination destination = new SingleColumnDestination(fw);
                protempa.execute(query, destination);
            }
            outputMatches(outputFile, TRUTH_OUTPUT_REPROCESS);
        }
    }
    
    private void outputMatches(File actual, String expected) throws IOException {
        try (BufferedReader actualReader = new BufferedReader(new FileReader(actual));
                BufferedReader expectedReader = new BufferedReader(new FileReader(expected))) {

            List<String> actualLines = IOUtils.readLines(actualReader);
            List<String> expectedLines = IOUtils.readLines(expectedReader);
            assertEquals(expectedLines, actualLines);
        }
    }

}
