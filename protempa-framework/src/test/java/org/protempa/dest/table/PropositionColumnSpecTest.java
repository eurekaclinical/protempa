package org.protempa.dest.table;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2017 Emory University
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.drools.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.DefaultUniqueIdFactory;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
public class PropositionColumnSpecTest {
    @Test
    public void testHeading() throws KnowledgeSourceReadException {
        PropositionColumnSpec ccs = new PropositionColumnSpec();
        Assert.assertArrayEquals(StringUtils.EMPTY_STRING_ARRAY, ccs.columnNames(null));
    }
    
    @Test
    public void testValueFileTabularWriter() throws IOException, TabularWriterException {
        Event prop = new Event("TESTEVENT", new DefaultUniqueIdFactory().getInstance());
        PropositionColumnSpec ccs = new PropositionColumnSpec();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("", sw.toString());
    }
    
    @Test
    public void testValueFileTabularWriterPrintUniqueId() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        Event prop = new Event("TESTEVENT", uniqueId);
        OutputConfig outputConfig = new OutputConfig.Builder().showUniqueId().showId().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec(null, outputConfig, null);
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals(uniqueId.getStringRepresentation() + '\t' + prop.getId(), sw.toString());
    }
    
    @Test
    public void testValueFileTabularWriterPrintLocalUniqueId() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        Event prop = new Event("TESTEVENT", uniqueId);
        OutputConfig outputConfig = new OutputConfig.Builder().showLocalUniqueId().showId().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec(null, outputConfig, null);
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals(uniqueId.getLocalUniqueId().getId() + '\t' + prop.getId(), sw.toString());
    }
}
