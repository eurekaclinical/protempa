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

import java.io.IOException;

/**
 *
 * @author Andrew Post
 */
public interface Mappings {

    /**
     * Reads codes in a resource. The resource is prefixed by the resource
     * prefix specified at construction. Each mapping must be on a separate
     * tab-delimited line. A column number indicates which column holds the
     * knowledge source code for the mapping.
     *
     * @return a {@link String} array containing all of the mapped knowledge source
     *         codes in the resource
     * @throws IOException
     *             if something goes wrong while accessing the resource
     */
    String[] readTargets() throws IOException;
    
    Object[] readSources();
    
    String getTarget(Object source);
    
    int size();
    
    boolean isEmpty();
    
    Mappings subMappingsBySources(Object[] sources);
    
    Mappings subMappingsByTargets(String[] targets);
    
    void close() throws IOException;
    
}
