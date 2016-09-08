package org.protempa.backend.dsb.relationaldb.mappings;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
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
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Andrew Post
 */
public class DelimFileMappingsFactory extends AbstractMappingsFactory {

    private final String pathnamePrefix;

    public DelimFileMappingsFactory(String pathnamePrefix) {
        if (pathnamePrefix == null) {
            throw new IllegalArgumentException("pathNamePrefix cannot be null");
        }
        this.pathnamePrefix = pathnamePrefix;
    }

    @Override
    public DelimFileMappings getInstance(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename cannot be null");
        }
        try {
            DelimFileMappings m = new DelimFileMappings(new File(this.pathnamePrefix, filename));
            addMappings(m);
            return m;
        } catch (IOException ex) {
            throw new IOException("Error reading delim file " + filename, ex);
        }
    }

}
