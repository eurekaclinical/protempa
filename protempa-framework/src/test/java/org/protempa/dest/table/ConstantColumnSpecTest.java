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
import org.junit.Assert;
import org.junit.Test;
import org.protempa.KnowledgeSourceReadException;

/**
 *
 * @author Andrew Post
 */
public class ConstantColumnSpecTest {
    
    @Test
    public void testHeading() throws KnowledgeSourceReadException {
        ConstantColumnSpec ccs = new ConstantColumnSpec("heading", "value");
        Assert.assertArrayEquals(new String[]{"heading"}, ccs.columnNames(null));
    }
    
    @Test
    public void testValue() throws TabularWriterException {
        ConstantColumnSpec ccs = new ConstantColumnSpec("heading", "value");
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", null, null, null, null, null, ftw);
        }
        Assert.assertEquals("value", sw.toString());
    }
    
    @Test
    public void testNullValue() throws TabularWriterException {
        ConstantColumnSpec ccs = new ConstantColumnSpec("heading", null);
        StringWriter sw = new StringWriter();
        try (FileTabularWriter ftw = new FileTabularWriter(new BufferedWriter(sw), '\t')) {
            ccs.columnValues("00001", null, null, null, null, null, ftw);
        }
        Assert.assertEquals("NULL", sw.toString());
    }
}
