package org.protempa.test;

/*-
 * #%L
 * Protempa Test Suite
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.protempa.Protempa;
import org.protempa.ProtempaException;
import org.protempa.SourceFactory;
import org.protempa.bconfigs.ini4j.INIConfigurations;
import org.protempa.dest.Destination;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.QueryBuilder;
import org.protempa.query.QueryMode;

/**
 *
 * @author Andrew Post
 */
abstract class AbstractProtempaWithPersistenceTest {

    private File tempDir;

    /**
     * The ground truth results directory for the test data
     */
    private static final String TRUTH_DIR = "src/test/resources/truth/";

    
    @Before
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory(null).toFile();
    }
    
    @After
    public void tearDown() {
        tempDir = null;
    }
    
    
    void runProtempa(DefaultQueryBuilder qb, QueryMode queryMode) throws IOException, ProtempaException {
        runProtempa(qb, queryMode, null);
    }

    void runProtempa(DefaultQueryBuilder qb, QueryMode queryMode, String truthFile) throws IOException, ProtempaException {
        SourceFactory sf = new SourceFactory(
                new INIConfigurations(new File("src/test/resources")),
                "protege-h2-test-config");
        try (Protempa protempa = Protempa.newInstance(sf)) {
            doRun(protempa, buildQuery(qb, queryMode), truthFile != null ? new File(TRUTH_DIR, truthFile) : null);
        }
    }

    QueryBuilder buildQuery(DefaultQueryBuilder qb, QueryMode queryMode) {
        qb.setDatabasePath(new File(tempDir, "test").getPath());
        qb.setQueryMode(queryMode);
        return qb;
    }

    void doRun(final Protempa protempa, QueryBuilder qb, File truthFile) throws IOException, ProtempaException {
        File outputFile
                = File.createTempFile("protempa-test-with-persistence", null);
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile))) {
            Destination destination = new SingleColumnDestination(fw);
            protempa.execute(protempa.buildQuery(qb), destination);
        }
        if (truthFile != null) {
            assertEqualFiles(outputFile, truthFile);
        }
    }

    private static void assertEqualFiles(File actual, File expected) throws IOException {
        try (BufferedReader actualReader = new BufferedReader(new FileReader(actual))) {
            System.out.println("expected file " + expected);
            String line;
            while ((line = actualReader.readLine()) != null) {
                System.out.println(line);
            }
        }
        try (BufferedReader actualReader = new BufferedReader(new FileReader(actual));
                BufferedReader expectedReader = new BufferedReader(new FileReader(expected))) {
            assertEquals(IOUtils.readLines(expectedReader), IOUtils.readLines(actualReader));
        }
    }
}
