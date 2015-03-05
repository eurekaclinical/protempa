package org.protempa.backend.dsb.relationaldb.mappings;

/*
 * #%L
 * AIW i2b2 ETL
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

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Andrew Post
 */
public class CSVSupport {

    /**
     * Closes the supplied reader.
     * 
     * @param reader
     * @return
     * @throws IOException 
     */
    public Map<Object, String> read(Reader reader) throws IOException {
        Map<Object, String> cvs = new HashMap<>();
        try (CSVReader r = new CSVReader(reader, '\t')) {
            String[] cols;
            int i = 1;
            while ((cols = r.readNext()) != null) {
                switch (cols.length) {
                    case 0:
                        break;
                    case 1:
                        String col0 = cols[0].trim();
                        if (col0.length() > 1) {
                            cvs.put(col0, "");
                        }
                        break;
                    case 2:
                        cvs.put(cols[1].trim(), cols[0].trim());
                        break;
                    default:
                        throw new AssertionError("Invalid mapping in line " + i + ": mapping has length " + cols.length);
                }
                i++;
            }
        }
        return cvs;
    }

    /**
     * Closes the supplied reader.
     * 
     * @param reader
     * @param colNum
     * @return
     * @throws IOException 
     */
    public String[] readTarget(Reader reader) throws IOException {
        Collection<String> targetColl = read(reader).values();
        Set<String> targets = new HashSet<>();
        for (String target : targetColl) {
            targets.add(target);
        }
        return targets.toArray(new String[targets.size()]);
    }
    
}
