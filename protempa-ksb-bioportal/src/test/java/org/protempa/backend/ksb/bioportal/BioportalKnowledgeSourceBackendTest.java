package org.protempa.backend.ksb.bioportal;

/*
 * #%L
 * Protempa BioPortal Knowledge Source Backend
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

import org.junit.Before;
import org.junit.Test;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.Protempa;
import org.protempa.ProtempaStartupException;
import org.protempa.SourceFactory;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.bconfigs.ini4j.INIConfigurations;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class BioportalKnowledgeSourceBackendTest {

    private static final String ICD9CM_250_ID = "http://purl.bioontology.org/ontology/ICD9CM/250";
    private BioportalKnowledgeSourceBackend ksb;

    @Before
    public void setUp() {
        SourceFactory sf = null;
        try {
            sf = new SourceFactory(new INIConfigurations(new File("src/test/resources")),
                    "bioportal-test-config");
            Protempa p = Protempa.newInstance(sf);
            KnowledgeSourceBackend ksb = p.getKnowledgeSource().getBackends()[0];
            this.ksb = (BioportalKnowledgeSourceBackend) ksb;
        } catch (BackendProviderSpecLoaderException | ConfigurationsLoadException | InvalidConfigurationException | ConfigurationsNotFoundException | ProtempaStartupException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReadPropositionDefinition() throws KnowledgeSourceReadException {
        PropositionDefinition propDef = this.ksb.readPropositionDefinition("http://purl.bioontology.org/ontology/ICD9CM/250");
        assertNotNull(propDef);
        assertEquals("http://purl.bioontology.org/ontology/ICD9CM/250", propDef.getId());
        assertEquals("Diabetes mellitus", propDef.getDisplayName());
        assertEquals("250", propDef.getAbbreviatedDisplayName());
        assertTrue(propDef.getInDataSource());
    }

    @Test
    public void testReadPropositionDefinitionInverseIsA() throws KnowledgeSourceReadException {
        PropositionDefinition propDef = this.ksb.readPropositionDefinition("http://purl.bioontology.org/ontology/ICD9CM/250");
        assertEquals(10, propDef.getChildren().length);
        Set<String> expectedChildren = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            expectedChildren.add("http://purl.bioontology.org/ontology/ICD9CM/250." + i);
        }
        Set<String> actualChildren = new HashSet<>();
        for (String child : propDef.getChildren()) {
            actualChildren.add(child);
        }
        assertEquals(expectedChildren, actualChildren);
    }

    @Test
    public void testReadIsA() throws KnowledgeSourceReadException {
        String[] isa = this.ksb.readIsA(ICD9CM_250_ID + ".1");
        assertArrayEquals(new String[]{ICD9CM_250_ID}, isa);
    }

    @Test
    public void testGetKnowledgeSourceSearchResults() throws KnowledgeSourceReadException {
        List<String> results = this.ksb.getKnowledgeSourceSearchResults("diabetes");
        assertEquals(95, results.size());
        assertTrue(results.contains(ICD9CM_250_ID));

        // uppercase "Diabetes"
        assertTrue(results.contains("http://purl.bioontology.org/ontology/ICD9CM/250.93"));

        // lowercase "diabetes"
        assertTrue(results.contains("http://purl.bioontology.org/ontology/ICD9CM/249.71"));
    }

    @Test
    public void testReadAbstractionDefinition() throws KnowledgeSourceReadException {
        assertNull(this.ksb.readAbstractionDefinition(ICD9CM_250_ID));
    }

    @Test
    public void testReadContextDefinition() throws KnowledgeSourceReadException {
        assertNull(this.ksb.readContextDefinition(ICD9CM_250_ID));
    }

    @Test
    public void testReadTemporalPropositionDefinition() throws KnowledgeSourceReadException {
        assertNull(this.ksb.readTemporalPropositionDefinition(ICD9CM_250_ID));
    }

    @Test
    public void testReadAbstractedInto() throws KnowledgeSourceReadException {
        assertArrayEquals(new String[0], this.ksb.readAbstractedInto(ICD9CM_250_ID));
    }

    @Test
    public void testReadInduces() throws KnowledgeSourceReadException {
        assertArrayEquals(new String[0], this.ksb.readInduces(ICD9CM_250_ID));
    }

    @Test
    public void testReadSubContextsOf() throws KnowledgeSourceReadException {
        assertArrayEquals(new String[0], this.ksb.readSubContextOfs(ICD9CM_250_ID));
    }
}
