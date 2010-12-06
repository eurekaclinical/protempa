package org.protempa.ksb.protege;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.test.MockBackendInstanceSpec;
import org.protempa.backend.test.MockBackendPropertySpec;

public final class ProtegeKnowledgeSourceBackendTest {
    private RemoteKnowledgeSourceBackend backend;

    public ProtegeKnowledgeSourceBackendTest() {

    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {

    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        this.backend = new RemoteKnowledgeSourceBackend();

        backend.setHostname("aiwdev02.eushc.org");
        backend.setUsername("mansour");
        backend.setPassword("eHmMrKM2");
        backend.setKnowledgeBaseName("ERATDiagnoses");
        backend.setUnits("ABSOLUTE");

        List<BackendPropertySpec> propSpecs = new ArrayList<BackendPropertySpec>();

        propSpecs.add(new MockBackendPropertySpec("hostname", String.class)
                .getBackendPropertySpec());
        propSpecs.add(new MockBackendPropertySpec("username", String.class)
                .getBackendPropertySpec());
        propSpecs.add(new MockBackendPropertySpec("password", String.class)
                .getBackendPropertySpec());
        propSpecs.add(new MockBackendPropertySpec("knowledgeBaseName",
                String.class).getBackendPropertySpec());
        propSpecs.add(new MockBackendPropertySpec("units", String.class)
                .getBackendPropertySpec());

        MockBackendInstanceSpec<RemoteKnowledgeSourceBackend> mbis = new MockBackendInstanceSpec<RemoteKnowledgeSourceBackend>(
                propSpecs);
        
        try {
            backend.initialize(mbis.getBackendInstanceSpec());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Setup complete!");
    }
    
    @After
    public void tearDown() throws Exception {
        this.backend = null;
    }
    
    @Test
    public void testGetPropositionsByTerm() throws Exception {
        List<String> result = backend.getPropositionsByTerm("ICD9CM:250.0");
        assertEquals(1, result.size());
    }
    
    @Test
    public void testGetPropositionsByTermSubsumption() throws Exception {
        
    }
}
