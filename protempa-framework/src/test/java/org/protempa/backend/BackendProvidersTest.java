/*
 * #%L
 * Protempa Framework
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
package org.protempa.backend;

import org.protempa.backend.test.MockBackendProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;

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
        BackendProviderManager.setBackendProvider(null);
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