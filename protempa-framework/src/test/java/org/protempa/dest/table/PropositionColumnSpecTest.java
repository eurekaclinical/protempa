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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.drools.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.DefaultUniqueIdFactory;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;

/**
 *
 * @author Andrew Post
 */
public class PropositionColumnSpecTest {
    @Test
    public void testHeading() throws KnowledgeSourceReadException {
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().build();
        Assert.assertArrayEquals(StringUtils.EMPTY_STRING_ARRAY, ccs.columnNames(null));
    }
    
    @Test
    public void testValueFileTabularWriter() throws IOException, TabularWriterException {
        Event prop = new Event("TESTEVENT", new DefaultUniqueIdFactory().getInstance());
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("", sw.toString());
    }
    
    @Test
    public void testNullValueFileTabularWriter() throws IOException, TabularWriterException {
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", null, null, null, null, null, ftw);
        }
        Assert.assertEquals("NULL", sw.toString());
    }
    
    @Test
    public void testValueFileTabularWriterPrintUniqueId() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        Event prop = new Event("TESTEVENT", uniqueId);
        OutputConfig outputConfig = new OutputConfig.Builder().showUniqueId().showId().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
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
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals(uniqueId.getLocalUniqueId().getId() + '\t' + prop.getId(), sw.toString());
    }
    
    @Test
    public void testValueFileTabularWriterPrintNumericalId() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        Event prop = new Event("TESTEVENT", uniqueId);
        OutputConfig outputConfig = new OutputConfig.Builder().showNumericalId().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals(String.valueOf(uniqueId.getLocalUniqueId().getNumericalId()), sw.toString());
    }
    
    @Test
    public void testFileTabularWriterPrintStart() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        Event prop = new Event("TESTEVENT", uniqueId);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2018, Calendar.JUNE, 1, 14, 47, 0);
        prop.setInterval(new IntervalFactory().getInstance(cal.getTimeInMillis(), AbsoluteTimeGranularity.SECOND));
        OutputConfig outputConfig = new OutputConfig.Builder().showStartOrTimestamp().dateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("2018-06-01T14:47:00", sw.toString());
    }
    
    @Test
    public void testFileTabularWriterPrintFinish() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        Event prop = new Event("TESTEVENT", uniqueId);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2018, Calendar.JUNE, 1, 14, 47, 0);
        prop.setInterval(new IntervalFactory().getInstance(cal.getTimeInMillis(), AbsoluteTimeGranularity.SECOND));
        OutputConfig outputConfig = new OutputConfig.Builder().showFinish().dateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("2018-06-01T14:47:00", sw.toString());
    }
    
    @Test
    public void testNumberFileTabularWriterPrintNumber() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        PrimitiveParameter prop = new PrimitiveParameter("TESTEVENT", uniqueId);
        prop.setValue(NumberValue.getInstance(10));
        OutputConfig outputConfig = new OutputConfig.Builder().showNumber().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("10", sw.toString());
    }
    
    @Test
    public void testNominalFileTabularWriterPrintNumber() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        PrimitiveParameter prop = new PrimitiveParameter("TESTEVENT", uniqueId);
        prop.setValue(NominalValue.getInstance("foo"));
        OutputConfig outputConfig = new OutputConfig.Builder().showNumber().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("NULL", sw.toString());
    }
    
    @Test
    public void testNominalFileTabularWriterPrintNominal() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        PrimitiveParameter prop = new PrimitiveParameter("TESTEVENT", uniqueId);
        prop.setValue(NominalValue.getInstance("foo"));
        OutputConfig outputConfig = new OutputConfig.Builder().showNominal().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("foo", sw.toString());
    }
    
    @Test
    public void testNumberFileTabularWriterPrintNominal() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        PrimitiveParameter prop = new PrimitiveParameter("TESTEVENT", uniqueId);
        prop.setValue(NumberValue.getInstance(10));
        OutputConfig outputConfig = new OutputConfig.Builder().showNominal().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("NULL", sw.toString());
    }
    
    @Test
    public void testInequalityNumberFileTabularWriterPrintNumber() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        PrimitiveParameter prop = new PrimitiveParameter("TESTEVENT", uniqueId);
        prop.setValue(InequalityNumberValue.parse("<10"));
        OutputConfig outputConfig = new OutputConfig.Builder().showNumber().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("10", sw.toString());
    }
    
    @Test
    public void testInequalityNumberFileTabularWriterPrintInequality() throws IOException, TabularWriterException {
        UniqueId uniqueId = new DefaultUniqueIdFactory().getInstance();
        PrimitiveParameter prop = new PrimitiveParameter("TESTEVENT", uniqueId);
        prop.setValue(InequalityNumberValue.parse("<10"));
        OutputConfig outputConfig = new OutputConfig.Builder().showInequality().build();
        PropositionColumnSpec ccs = new PropositionColumnSpec.Builder().outputConfig(outputConfig).build();
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", prop, null, null, null, null, ftw);
        }
        Assert.assertEquals("<", sw.toString());
    }
}
