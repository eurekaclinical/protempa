package org.protempa.ksb.protege;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.protempa.TermSubsumption;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.test.MockBackendInstanceSpec;
import org.protempa.backend.test.MockBackendPropertySpec;
import org.protempa.query.And;

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
        List<String> termIds = new ArrayList<String>();
        Set<And<TermSubsumption>> termSub = new HashSet<And<TermSubsumption>>();
        termIds.add("ICD9CM:250.0");
        termIds.add("ICD9CM:250.00");
        termIds.add("ICD9CM:250.01");
        termIds.add("ICD9CM:250.02");
        termIds.add("ICD9CM:250.03");
        And<TermSubsumption> and = new And<TermSubsumption>(TermSubsumption.fromTerms(termIds));
        termSub.add(and);
        
        List<String> actual = backend.getPropositionsByTermSubsumption(and);
        Set<String> expected = new HashSet<String>();
        expected.add("250.0");
        expected.add("250.00");
        expected.add("250.01");
        expected.add("250.02");
        expected.add("250.03");

        assertEquals(expected, new HashSet<String>(actual));
    }
}
