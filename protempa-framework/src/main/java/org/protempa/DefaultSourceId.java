/*
 * #%L
 * Protempa Framework
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
package org.protempa;

import org.apache.commons.collections4.map.ReferenceMap;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Andrew Post
 */
public class DefaultSourceId implements SourceId, Serializable {
    private static final long serialVersionUID = -8057854346978674580L;
    
    private static final Map<String, DefaultSourceId> CACHE = new ReferenceMap<>();
    
    public static DefaultSourceId getInstance(String sourceIdStr) {
        if (sourceIdStr == null) {
            throw new IllegalArgumentException("sourceIdStr cannot be null");
        }
        DefaultSourceId result = CACHE.get(sourceIdStr);
        if (result == null) {
            result = new DefaultSourceId(sourceIdStr);
            CACHE.put(sourceIdStr, result);
        }
        return result;
    }
    private final String sourceIdStr;
    
    private DefaultSourceId(String sourceIdStr) {
        this.sourceIdStr = sourceIdStr;
    }

    @Override
    public String getStringRepresentation() {
        return this.sourceIdStr;
    }

    @Override
    public DefaultSourceIdBuilder asBuilder() {
        DefaultSourceIdBuilder builder = new DefaultSourceIdBuilder();
        builder.setSourceIdStr(this.sourceIdStr);
        return builder;
    }
    
}
