package org.protempa.backend;

import org.protempa.backend.test.MockBackendProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.protempa.AlgorithmSourceBackend;
import org.protempa.DataSourceBackend;
import org.protempa.KnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class BackendProvidersTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        BackendProviderManager.setBackendProvider(new MockBackendProvider());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of loadDataSourceBackendSpec method, of class BackendProviders.
     */
    @Test
    public void testLoadDataSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        String id = "DSBackendSpec1";
        BackendSpec<DataSourceBackend> result =
                BackendProviderManager.getBackendProvider().
                getDataSourceBackendSpecLoader().loadSpec(id);
        assertEquals(id, result.getId());
    }

    @Test(expected = BackendSpecNotFoundException.class)
    public void testBogusLoadDataSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        String id = "bogus";
        BackendSpec<DataSourceBackend> result =
                BackendProviderManager.getBackendProvider().
                getDataSourceBackendSpecLoader().loadSpec(id);
        assertNull(result);
    }

    /**
     * Test of loadKnowledgeSourceBackendSpec method, of class BackendProviders.
     */
    @Test
    public void testLoadKnowledgeSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        String id = "KSBackendSpec1";
        BackendSpec<KnowledgeSourceBackend> result =
                BackendProviderManager.getBackendProvider().
                getKnowledgeSourceBackendSpecLoader().loadSpec(id);
        assertEquals(id, result.getId());
    }

    @Test(expected = BackendSpecNotFoundException.class)
    public void testBogusLoadKnowledgeSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        String id = "bogus";
        BackendProviderManager.getBackendProvider().
                getKnowledgeSourceBackendSpecLoader().loadSpec(id);
    }

    /**
     * Test of loadAlgorithmSourceBackendSpec method, of class BackendProviders.
     */
    @Test
    public void testLoadAlgorithmSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        String id = "ASBackendSpec1";
        BackendSpec<AlgorithmSourceBackend> result =
                BackendProviderManager.getBackendProvider().
                getAlgorithmSourceBackendSpecLoader().loadSpec(id);
        assertEquals(id, result.getId());
    }

    @Test(expected = BackendSpecNotFoundException.class)
    public void testBogusLoadAlgorithmSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendProviderManager.getBackendProvider().
                getAlgorithmSourceBackendSpecLoader().loadSpec("bogus");
    }
}