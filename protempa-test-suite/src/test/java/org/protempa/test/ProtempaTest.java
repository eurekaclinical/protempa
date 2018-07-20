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
import java.util.List;
import org.apache.commons.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.Protempa;
import org.protempa.ProtempaException;
import org.protempa.SourceFactory;
import org.protempa.bconfigs.ini4j.INIConfigurations;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.dest.Destination;

/**
 * Unit tests for Protempa.
 *
 * Persistent stores go into the directory in the system property
 * <code>java.io.tmpdir</code>.
 *
 * @author Michel Mansour
 */
public class ProtempaTest {

    /**
     * The ground truth results directory for the test data
     */
    private static final String TRUTH_DIR = "src/test/resources/truth/";
    /**
     * The ground truth output.
     */
    private static final String TRUTH_OUTPUT = TRUTH_DIR + "/output.txt";

    /*
     * Instance of Protempa to run
     */
    private Protempa protempa;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // force the use of the H2 driver so we don't bother trying to load
        // others
        System.setProperty("protempa.dsb.relationaldatabase.sqlgenerator",
                "org.protempa.backend.dsb.relationaldb.h2.H2SQLGenerator");
        SourceFactory sf = new SourceFactory(
                new INIConfigurations(new File("src/test/resources")),
                "protege-h2-test-config");
        protempa = Protempa.newInstance(sf);
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (this.protempa != null) {
            this.protempa.close();
        }
    }

    /**
     * Tests the end-to-end execution of Protempa without persistence. Only
     * verifies that the final output is correct.
     */
    @Test
    public void testProtempaRetrieve() throws IOException, ProtempaException {
        File outputFile = File.createTempFile("protempa-test", null);
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile))) {
            Destination destination = new SingleColumnDestination(fw);
            protempa.execute(queryRetrieve(), destination);
        }
        outputMatches(outputFile, TRUTH_OUTPUT);
    }

    private void outputMatches(File actual, String expected) throws IOException {
        try (BufferedReader actualReader = new BufferedReader(new FileReader(actual));
                BufferedReader expectedReader = new BufferedReader(new FileReader(expected))) {

            List<String> actualLines = IOUtils.readLines(actualReader);
            List<String> expectedLines = IOUtils.readLines(expectedReader);
            assertEquals(expectedLines, actualLines);
        }
    }
    
    private Query queryRetrieve() throws KnowledgeSourceReadException, QueryBuildException {
        DefaultQueryBuilder q = new QueryBuilderFactory().getInstance();
        Query query = protempa.buildQuery(q);

        return query;
    }
}
