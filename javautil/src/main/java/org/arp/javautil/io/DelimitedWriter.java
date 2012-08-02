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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import org.arp.javautil.string.StringUtil;

/**
 * Convenience class for writing delimited files.
 * 
 * @author Andrew Post
 */
public final class DelimitedWriter implements Closeable, Flushable {

    private final char delimiter;
    private final Writer writer;
    private boolean firstColumn;
    private final String lineSeparator;
    
    /**
     * Creates the writer with a column delimiter and the {@link Writer} to
     * which to write. Uses the system line separator string.
     * 
     * @param delimiter a delimiter character.
     * @param writer a {@link Writer}.
     */
    public DelimitedWriter(char delimiter, Writer writer) {
        this(delimiter, writer, null);
    }

    /**
     * Creates the writer with a column delimiter, the {@link Writer} to which
     * to write, and a line separator string.
     * 
     * @param delimiter a delimiter character.
     * @param writer a {@link Writer}. Should not be <code>null</code>.
     * @param lineSeparator a line separator string. If <code>null</code>, the
     * system line separator string is used.
     */
    public DelimitedWriter(char delimiter, Writer writer,
            String lineSeparator) {
        this.delimiter = delimiter;
        this.writer = writer;
        this.firstColumn = true;
        if (lineSeparator != null) {
            this.lineSeparator = lineSeparator;
        } else {
            this.lineSeparator = IOUtils.LINE_SEPARATOR;
        }
    }

    /**
     * Writes a column, inserting a delimiter as needed.
     * 
     * @param column the column contents.
     * 
     * @throws IOException if an error occurs writing to the {@link Writer}.
     */
    public void write(String column) throws IOException {
        if (!this.firstColumn) {
            this.writer.write(this.delimiter);
        }
        StringUtil.escapeAndWriteDelimitedColumn(column, this.delimiter,
                this.writer);
        this.firstColumn = false;
    }

    /**
     * Writes the provided line separator string (or the default).
     * 
     * @throws IOException if an error occurs writing to the {@link Writer}.
     */
    public void newLine() throws IOException {
        this.writer.write(this.lineSeparator);
        this.firstColumn = true;
    }

    /**
     * Closes the {@link Writer}.
     * 
     * @throws IOException if an error occurs closing the {@link Writer}.
     */
    @Override
    public void close() throws IOException {
        this.writer.close();
    }

    /**
     * Calls the provided {@link Writer}'s {@link Writer#flush() } method.
     * 
     * @throws IOException if an error occurs flushing the {@link Writer}.
     */
    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }
}
