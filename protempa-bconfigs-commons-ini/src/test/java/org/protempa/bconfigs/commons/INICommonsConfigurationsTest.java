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
import org.protempa.AbstractAlgorithmSourceBackend;
import org.protempa.AbstractDataSourceBackend;
import org.protempa.AbstractKnowledgeSourceBackend;
import org.protempa.Algorithm;
import org.protempa.AlgorithmSourceReadException;
import org.protempa.Algorithms;
import org.protempa.BackendInitializationException;
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
        BackendSpec backendSpec = BackendProviderManager.getBackendProvider()
                .getAlgorithmSourceBackendSpecLoader()
                .loadSpec("ASBackendSpec1");
        List<BackendInstanceSpec> result =
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

    private static class MockKnowledgeSourceBackend
            extends AbstractKnowledgeSourceBackend {

        public void initialize(BackendInstanceSpec config)
                throws BackendInitializationException {
        }
    }

    private static class MockDataSourceBackend
            extends AbstractDataSourceBackend {

        MockDataSourceBackend() {
            super(null);
        }
    }

    private static class MockAlgorithmSourceBackend
            extends AbstractAlgorithmSourceBackend {

        public Algorithm readAlgorithm(String id, Algorithms algorithms)
                throws AlgorithmSourceReadException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void readAlgorithms(Algorithms algorithms)
                throws AlgorithmSourceReadException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void initialize(BackendInstanceSpec config)
                throws BackendInitializationException {
        }
    }
}