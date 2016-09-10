package org.protempa.backend.dsb.relationaldb.mappings;

/*-
 * #%L
 * Protempa Relational Database Data Source Backend
 * %%
 * Copyright (C) 2012 - 2016 Emory University
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
import java.io.FileReader;
import java.io.IOException;

/**
 * Maps proposition IDs from the knowledge source to SQL. Looks for mapping
 * sources (typically files) in a specified resource location (typically a file
 * system directory).
 * 
 * @author Andrew Post
 */
public final class DelimFileMappings extends DefaultMappings {

    /**
     * Initializes the mapper. Accepts the resource location where the mapping
     * resources can be found and the class whose loader to use.
     * 
     * @param file
     *            where the mapping resources are found (as a {@link String}).
     *            Typically a file system directory.
     */
    DelimFileMappings(File file) throws IOException {
        super(file.getName(), new CSVSupport().read(new FileReader(file)));
    }

}
