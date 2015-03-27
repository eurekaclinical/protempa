/*
 * #%L
 * Protempa Commons INI Backend Configurations
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
package org.protempa.bconfigs.ini4j;

import org.apache.commons.io.FileUtils;
import org.arp.javautil.io.UniqueDirectoryCreator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.asb.AlgorithmSourceBackend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.protempa.backend.Configuration;

/**
 *
 * @author Andrew Post
 */
public class INIConfigurationsTest extends AbstractINIConfigurationsTest {

    private static File CONFIG_DIR;

    @BeforeClass
    public static void setUpClass() throws Exception {
        UniqueDirectoryCreator dirCreator = new UniqueDirectoryCreator();
        CONFIG_DIR = dirCreator.create("INI4JConfigurationsTest", "ini",
                FileUtils.getTempDirectory());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        FileUtils.deleteDirectory(CONFIG_DIR);
    }

    private INIConfigurations configurations;

    @Before
    public void setUp() {
        configurations = new INIConfigurations(CONFIG_DIR);
    }

    @After
    public void tearDown() {
        File f = new File(CONFIG_DIR, "test");
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    public void testPathname() {
        assertEquals(CONFIG_DIR, configurations.getDirectory());
    }

    /**
     * Test of load method, of class INICommonsConfigurations.
     */
    @Test
    public void testLoadSize() throws Exception {
        writeTestFile();
        Configuration configuration = configurations.load("test");
        List<BackendInstanceSpec<AlgorithmSourceBackend>> algorithmSourceBackendSections = configuration.getAlgorithmSourceBackendSections();
        assertEquals(1, algorithmSourceBackendSections.size());
    }

    /**
     * Test of load method, of class INICommonsConfigurations.
     */
    @Test
    public void testLoadUrlProperty() throws Exception {
        writeTestFile();
        Configuration configuration = configurations.load("test");
        List<BackendInstanceSpec<AlgorithmSourceBackend>> algorithmSourceBackendSections = configuration.getAlgorithmSourceBackendSections();
        BackendInstanceSpec<AlgorithmSourceBackend> theBis = null;
        for (BackendInstanceSpec<AlgorithmSourceBackend> bis : algorithmSourceBackendSections) {
            if (bis.getBackendSpec().getId().equals("ASBackendSpec1")) {
                theBis = bis;
            }
        }
        assertEquals("http://localhost", theBis.getProperty("url"));
    }

    /**
     * Test of save method, of class INICommonsConfigurations.
     */
    @Test
    public void testSave() throws Exception {
        BackendInstanceSpec<AlgorithmSourceBackend> backendInstanceSpec
                = this.configurations.newAlgorithmSourceBackendSection("ASBackendSpec1");
        backendInstanceSpec.setProperty("url", "http://localhost");
        Configuration configuration = new Configuration();
        configuration.setConfigurationId("test");
        configuration.setAlgorithmSourceBackendSections(Collections.singletonList(backendInstanceSpec));
        configurations.save(configuration);

        StringWriter sw = new StringWriter();
        writeTestFileAndClose(new PrintWriter(sw));
        File f = new File(CONFIG_DIR, "test");
        assertEquals("Contents of " + f.getPath() + " not as expected",
                sw.toString().trim(), FileUtils.readFileToString(f).trim());
    }

    private static void writeTestFile() throws FileNotFoundException {
        writeTestFileAndClose(new PrintWriter(new File(CONFIG_DIR, "test")));

    }

    private static void writeTestFileAndClose(PrintWriter pw) {
        pw.println("[ASBackendSpec1]");
        pw.println("url = http://localhost");
        pw.close();
    }
}
