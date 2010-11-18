package org.protempa.tsb.umls;

import java.util.ArrayList;
import java.util.List;

import org.arp.javautil.sql.DatabaseAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.protempa.Term;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.test.MockBackendInstanceSpec;
import org.protempa.backend.test.MockBackendPropertySpec;

import static org.junit.Assert.assertEquals;

public class UMLSTermSourceBackendTest {
    private UMLSTermSourceBackend backend;

    public UMLSTermSourceBackendTest() {

    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {

        this.backend = new UMLSTermSourceBackend();
        backend.setDatabaseAPI(DatabaseAPI.DRIVERMANAGER);
        backend.setDatabaseId("jdbc:mysql://aiwdev02.eushc.org:3307/umls_2010AA");
        backend.setUsername("umlsuser");
        backend.setPassword("3SqQgPOh");

        List<BackendPropertySpec> propSpecs = new ArrayList<BackendPropertySpec>();

        propSpecs.add(new MockBackendPropertySpec("databaseAPI", String.class).getBackendPropertySpec());
        propSpecs.add(new MockBackendPropertySpec("databaseId", String.class).getBackendPropertySpec());
        propSpecs.add(new MockBackendPropertySpec("username", String.class).getBackendPropertySpec());
        propSpecs.add(new MockBackendPropertySpec("password", String.class).getBackendPropertySpec());

        MockBackendInstanceSpec<UMLSTermSourceBackend> mbis = new MockBackendInstanceSpec<UMLSTermSourceBackend>(
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
    public void testReadTerm() throws Exception {
        Term term = backend.readTerm("ICD9CM:250.0");
        assertEquals("250.0", term.getCode());
    }

    @Test
    public void testGetDescendants() throws Exception {
        assertEquals(1, backend.getSubsumption("ICD9CM:250.02").size());
        assertEquals(5, backend.getSubsumption("ICD9CM:250.0").size());
        assertEquals(51, backend.getSubsumption("ICD9CM:250").size());
    }
}
