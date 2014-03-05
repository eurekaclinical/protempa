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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 *
 */
public class BioportalKnowledgeSourceBackendTest {

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
    }

    @Test
    public void testReadAbstractionDefinition() {
        fail();
    }

    @Test
    public void testReadContextDefinition() {
        fail();
    }

    @Test
    public void testReadTemporalPropositionDefinition() {
        fail();
    }

    @Test
    public void testReadAbstractedInto() {
        fail();
    }

    @Test
    public void testReadIsA() {
      fail();
    }

    @Test
    public void testReadSubContextsOf() {
        fail();
    }

    @Test
    public void testGetKnowledgeSourceSearchResults() {
        fail();
    }

}
