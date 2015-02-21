package org.protempa.backend.dsb.file;

/*
 * #%L
 * Protempa File Data Source Backend
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 * @author Andrew Post
 */
public class FixedWidthColumnSpec {

    private int offset;
    private int length;
    private String links;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    void parseDescriptor(String descriptor) throws IOException {
        try (Reader reader = new StringReader(descriptor);
            CSVReader r = new CSVReader(reader, '|')) {
            String[] readNext = r.readNext();
            this.offset = Integer.parseInt(readNext[0]);
            this.length = Integer.parseInt(readNext[1]);
            this.links = readNext[2];
        }
    }

}
