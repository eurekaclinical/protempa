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
package org.protempa.proposition;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A unique identifier for a data source backend.
 * 
 * @author Himanshu Rathod
 */
public final class DataSourceBackendId extends SourceId {

    private static Map<String, DataSourceBackendId> cache =
            new HashMap<String, DataSourceBackendId>();

    private final String id;
    private transient volatile int hashCode;

    /**
     * Creates a data source backend id.
     * 
     * @param id the id {@link String}. Cannot be <code>null</code>.
     * @return a {@link DataSourceBackendId}.
     */
    public static DataSourceBackendId getInstance(String id) {
        DataSourceBackendId result = cache.get(id);
        if (result == null) {
            id = id.intern();
            result = new DataSourceBackendId(id);
            cache.put(id, result);
        }
        return result;
    }

    @Override
    String getId() {
        return this.id;
    }

    /**
     * Creates a new data source backend id. Only used by
     * {@link #getInstance(java.lang.String)}.
     * 
     * @param newId the id {@link String}. Cannot be <code>null</code>.
     */
    private DataSourceBackendId(String newId) {
        if (newId == null) {
            throw new IllegalArgumentException("newId cannot be null");
        }
        this.id = newId;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + id.hashCode();
            this.hashCode = result;
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataSourceBackendId other = (DataSourceBackendId) obj;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
