/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.io;

import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Andrew Post
 */
public class DelimiterWriterTest {

    @Test
    public void testWriteLines() throws IOException {
        String lineSep = IOUtils.LINE_SEPARATOR_UNIX;
        char delim = '\t';
        StringWriter writer = new StringWriter();
        DelimitedWriter dw = new DelimitedWriter(delim, writer, lineSep);
        try {
            dw.write("foo");
            dw.write("ba" + delim + "r");
            dw.write("baz");
            dw.newLine();
            dw.write("oof");
            dw.write("rab");
            dw.write("zab");
            dw.newLine();
            dw.close();
        } finally {
            IOUtils.closeQuietly(dw);
        }
        String actual = writer.toString();
        String expected =
                "foo" + delim + "\"ba" + delim + "r\"" + delim + "baz" + lineSep
                + "oof" + delim + "rab" + delim + "zab" + lineSep;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDefaultNewLineBehavior() throws IOException {
        StringWriter writer = new StringWriter();
        DelimitedWriter dw = new DelimitedWriter('\t', writer);
        try {
            dw.newLine();
            dw.close();
        } finally {
            IOUtils.closeQuietly(dw);
        }
        String newLine = writer.toString();
        Assert.assertEquals(IOUtils.LINE_SEPARATOR, newLine);
    }
}
