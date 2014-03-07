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
package org.protempa.bconfigs.commons;

import org.apache.commons.io.FileUtils;
import org.arp.javautil.io.UniqueDirectoryCreator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendProviderManager;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.BackendSpecLoader;
import org.protempa.backend.BackendSpecNotFoundException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.InvalidPropertyNameException;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.test.MockBackendProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Andrew Post
 */
public class INICommonsConfigurationsTest {

    private static File CONFIG_DIR;
    private static BackendSpecLoader<AlgorithmSourceBackend> LOADER;

    @BeforeClass
    public static void setUpClass() throws Exception {
        UniqueDirectoryCreator dirCreator = new UniqueDirectoryCreator();
        CONFIG_DIR = dirCreator.create("INICommonsConfigurationsTest", "ini",
                FileUtils.getTempDirectory());
        System.setProperty("protempa.inicommonsconfigurations.pathname",
                CONFIG_DIR.getAbsolutePath());

        MockBackendProvider mbp = new MockBackendProvider();
        LOADER = mbp.getAlgorithmSourceBackendSpecLoader();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        LOADER = null;
        BackendProviderManager.setBackendProvider(null);
        FileUtils.deleteDirectory(CONFIG_DIR);
        
        System.clearProperty("protempa.inicommonsconfigurations.pathname");
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
        INICommonsConfigurations configurations =
                new INICommonsConfigurations();
        assertEquals(CONFIG_DIR, configurations.getDirectory());
    }

    /**
     * Test of load method, of class INICommonsConfigurations.
     */
    @Test
    public void testLoad() throws FileNotFoundException,
            BackendSpecNotFoundException, ConfigurationsLoadException,
            InvalidPropertyNameException, BackendProviderSpecLoaderException, 
            ConfigurationsNotFoundException {
        writeTestFile();
        INICommonsConfigurations configurations =
                new INICommonsConfigurations();
        BackendSpec<AlgorithmSourceBackend> backendSpec =
                LOADER.loadSpec("ASBackendSpec1");
        List<BackendInstanceSpec<AlgorithmSourceBackend>> result =
                configurations.load("test", backendSpec);
        assertEquals(1, result.size());
        BackendInstanceSpec bis = result.get(0);
        assertEquals("http://localhost", bis.getProperty("url"));
    }

    /**
     * Test of save method, of class INICommonsConfigurations.
     */
    @Test
    public void testSave() throws Exception {
        INICommonsConfigurations configurations =
                new INICommonsConfigurations();
        BackendSpec backendSpec =
                LOADER.loadSpec("ASBackendSpec1");
        BackendInstanceSpec backendInstanceSpec =
                backendSpec.newBackendInstanceSpec(0);
        backendInstanceSpec.setProperty("url", "http://localhost");
        configurations.save("test",
                Collections.singletonList(backendInstanceSpec));

        StringWriter sw = new StringWriter();
        writeTestFileAndClose(new PrintWriter(sw));
        File f = new File(CONFIG_DIR, "test");
        assertEquals("Contents of " + f.getPath() + " not as expected",
                sw.toString().trim(), FileUtils.readFileToString(f).trim());
    }

    /**
     * Test of loadConfigurationIds method, of class INICommonsConfigurations.
     */
    @Test
    public void testLoadConfigurationIds() throws Exception {
        writeTestFile();
        INICommonsConfigurations instance = new INICommonsConfigurations();
        List<String> result = instance.loadConfigurationIds("test");
        assertEquals(Collections.singletonList("ASBackendSpec1"), result);
    }
    
    private static void writeTestFile() throws FileNotFoundException {
        writeTestFileAndClose(new PrintWriter(new File(CONFIG_DIR, "test")));
        
    }
    
    private static void writeTestFileAndClose(PrintWriter pw) {
        pw.println("[ASBackendSpec1_0]");
        pw.println("url = http://localhost");
        pw.close();
    }
}
