/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.backend.dsb.relationaldb;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.arp.javautil.io.IOUtil;
import org.protempa.backend.dsb.relationaldb.KnowledgeSourceIdToSqlCode;

/**
 * Maps proposition IDs from the knowledge source to SQL. Looks for mapping
 * sources (typically files) in a specified resource location (typically a file
 * system directory).
 * 
 * @author Andrew Post
 */
public final class PropIdToSQLCodeMapper {
    private final String resourcePrefix;
    private final Class<?> cls;

    /**
     * Initializes the mapper. Accepts the resource location where the mapping
     * resources can be found and the class whose loader to use.
     * 
     * @param resourcePrefix
     *            where the mapping resources are found (as a {@link String}).
     *            Typically a file system directory.
     * @param cls
     *            the {@link Class} whose resource loader to use
     */
    public PropIdToSQLCodeMapper(String resourcePrefix, Class<?> cls) {
        if (resourcePrefix == null) {
            throw new IllegalArgumentException("resourcePrefix cannot be null");
        }
        if (cls == null) {
            throw new IllegalArgumentException("cls cannot be null");
        }
        this.resourcePrefix = resourcePrefix;
        this.cls = cls;
    }

    /**
     * Reads codes in a resource. The resource is prefixed by the resource
     * prefix specified at construction. Each mapping must be on a separate
     * tab-delimited line. A column number indicates which column holds the 
     * knowledge source code for the mapping.
     * 
     * @param resource
     *            the name of the resource, as a {@link String}. Will be
     *            prefixed by the prefix indicated at construction.
     * @param colNum
     *            an integer indicating which column of the mapping holds the
     *            knowledge source version of a code
     * @return a {@link String} array containing all of the mapped knowledge source
     *         codes in the resource
     * @throws IOException
     *             if something goes wrong while accessing the resource
     */
    public String[] readCodes(String resource, int colNum)
            throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        if (colNum < 0 || colNum > 1) {
            throw new IllegalArgumentException("Invalid colNum: " + colNum);
        }
        resource = this.resourcePrefix + resource;
        List<String> codes = new ArrayList<>();
        SQLGenUtil.logger().log(Level.FINER, "Attempting to get resource: {0}",
                resource);
        InputStream is = IOUtil.getResourceAsStream(resource, this.cls);
        try (CSVReader reader = new CSVReader(new InputStreamReader(is), '\t')) {
            String[] cols;
            int i = 1;
            while ((cols = reader.readNext()) != null) {
                if (cols.length > 0) {
                    if (cols.length < colNum) {
                        throw new AssertionError("Invalid mapping in " + resource
                                + " line " + i + ": mapping has length " + cols.length);
                    }
                    codes.add(cols[colNum].trim());
                }
                i++;
            }
        }
        return codes.toArray(new String[codes.size()]);
    }

    /**
     * Reads codes associated with a property name or proposition ID from a
     * resource. The resource will be prefixed with the prefix specified at
     * construction time. The result of this method can be passed to the
     * {@link ColumnSpec} constructor.
     * 
     * @param resource
     *            a {@link String} that is the name of resource that holds the
     *            mappings. It will be prefixed by the prefix specified at
     *            construction time.
     * @return an array of {@link KnowledgeSourceIdToSqlCode}s read from the
     *         resource
     * @throws IOException
     *             if something goes wrong while accessing the resource
     */
    public KnowledgeSourceIdToSqlCode[] propertyNameOrPropIdToSqlCodeArray(
            String resource) throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        resource = this.resourcePrefix + resource;
        SQLGenUtil.logger().log(Level.FINER, "Attempting to get resource: {0}",
                resource);
        List<KnowledgeSourceIdToSqlCode> cvs = new ArrayList<>(
                1000);
        InputStream is = IOUtil.getResourceAsStream(resource, this.cls);
        try (CSVReader reader = new CSVReader(new InputStreamReader(is), '\t')) {
            String[] cols;
            int i = 1;
            while ((cols = reader.readNext()) != null) {
                if (cols.length > 2) {
                    throw new AssertionError("Invalid mapping in " + resource
                            + " line " + i + ": mapping has length " + cols.length);
                } else if (cols.length == 1) {
                    cvs.add(new KnowledgeSourceIdToSqlCode(cols[0],
                        ""));
                } else if (cols.length == 2) {
                    cvs.add(new KnowledgeSourceIdToSqlCode(cols[0],
                        cols[1]));
                }
                i++;
            }
        }
        return cvs
                .toArray(new KnowledgeSourceIdToSqlCode[cvs.size()]);
    }
}
