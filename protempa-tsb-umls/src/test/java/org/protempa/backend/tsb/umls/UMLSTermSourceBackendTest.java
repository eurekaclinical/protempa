/*
 * #%L
 * Protempa UMLS Term Source Backend
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
package org.protempa.backend.tsb.umls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.arp.javautil.sql.DatabaseAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.protempa.Term;
import org.protempa.TermSourceReadException;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.test.MockBackendInstanceSpec;
import org.protempa.backend.test.MockBackendPropertySpec;

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
    public void testGetSubsumption() throws Exception {
        assertEquals(1, backend.getSubsumption("ICD9CM:250.02").size());
        assertEquals(5, backend.getSubsumption("ICD9CM:250.0").size());
        assertEquals(51, backend.getSubsumption("ICD9CM:250").size());
        
        // tests an invalid code
        try {
            backend.getSubsumption("ICD9:250.0");
            fail("exception not thrown");
        } catch (TermSourceReadException ex) {
            
        }
    }
}
