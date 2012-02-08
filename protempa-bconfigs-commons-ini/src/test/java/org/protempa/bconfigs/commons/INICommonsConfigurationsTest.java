/*
 * #%L
 * Protempa Commons INI Backend Configurations
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
package org.protempa.bconfigs.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import org.arp.javautil.io.IOUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import static org.junit.Assert.*;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.BackendProviderManager;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.BackendSpecNotFoundException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsProviderManager;
import org.protempa.backend.InvalidPropertyNameException;
import org.protempa.backend.test.MockBackendProvider;

/**
 *
 * @author Andrew Post
 */
public class INICommonsConfigurationsTest {

    static {
        BackendProviderManager.setBackendProvider(new MockBackendProvider());
    }
    

    public INICommonsConfigurationsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        File iniFile = File.createTempFile("INICommonsConfigurationsTest",
                "ini");
        String iniFilePath = iniFile.getPath();
        iniFile.delete();
        iniFile.mkdir();
        iniFile.deleteOnExit();
        System.setProperty("protempa.inicommonsconfigurations.pathname",
                iniFilePath);
        System.setProperty(Configurations.class.getName(),
                INICommonsConfigurations.class.getName());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws FileNotFoundException {
        
    }

    @After
    public void tearDown() {
        File f = new File(
                System.getProperty(
                "protempa.inicommonsconfigurations.pathname"), "test");
        if (f.exists())
            f.delete();
    }

    /**
     * Test of load method, of class INICommonsConfigurations.
     */
    @Test
    public void testLoad() throws FileNotFoundException,
            BackendSpecNotFoundException, ConfigurationsLoadException,
            InvalidPropertyNameException, BackendProviderSpecLoaderException {
        PrintWriter pw = new PrintWriter(new File(
                System.getProperty(
                "protempa.inicommonsconfigurations.pathname"), "test"));
        pw.println("[ASBackendSpec1_0]");
        pw.println("url = http://localhost");
        pw.close();
        Configurations configurations =
                ConfigurationsProviderManager.getConfigurationsProvider()
                .getConfigurations();
        BackendSpec<AlgorithmSourceBackend> backendSpec = BackendProviderManager.getBackendProvider()
                .getAlgorithmSourceBackendSpecLoader()
                .loadSpec("ASBackendSpec1");
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
        Configurations configurations =
                ConfigurationsProviderManager.getConfigurationsProvider()
                .getConfigurations();
        BackendSpec backendSpec =
                BackendProviderManager.getBackendProvider()
                .getAlgorithmSourceBackendSpecLoader()
                .loadSpec("ASBackendSpec1");
        BackendInstanceSpec backendInstanceSpec = backendSpec.newBackendInstanceSpec();
        backendInstanceSpec.setProperty("url", "http://www.virginia.edu");
        configurations.save("test", 
                Collections.singletonList(backendInstanceSpec));
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("[ASBackendSpec1_0]");
        pw.println("url = http://www.virginia.edu");
        pw.close();
        File f = new File(
                System.getProperty(
                "protempa.inicommonsconfigurations.pathname"), "test");
        assertEquals("Contents of " + f.getPath() + " not as expected",
                sw.toString().trim(), IOUtil.readFileAsString(f).trim());
    }

    /**
     * Test of loadConfigurationIds method, of class INICommonsConfigurations.
     */
    @Test
    public void testLoadConfigurationIds() throws Exception {
        PrintWriter pw = new PrintWriter(new File(
                System.getProperty(
                "protempa.inicommonsconfigurations.pathname"), "test"));
        pw.println("[ASBackendSpec1_0]");
        pw.println("url=http://localhost");
        pw.close();
        INICommonsConfigurations instance = new INICommonsConfigurations();
        List<String> result = instance.loadConfigurationIds("test");
        assertEquals(Collections.singletonList("ASBackendSpec1"), result);
    }
}