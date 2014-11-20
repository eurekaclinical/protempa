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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class DataSourceBackendSourceSystem extends SourceSystem {
    private static final long serialVersionUID = 1L;
    
    private static Map<String, DataSourceBackendSourceSystem> cache =
            new HashMap<>();

    public static DataSourceBackendSourceSystem getInstance(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        DataSourceBackendSourceSystem result = cache.get(id);
        if (result == null) {
            result = new DataSourceBackendSourceSystem(id);
            cache.put(id, result);
        }
        return result;
    }
    
    private final String id;

    private DataSourceBackendSourceSystem(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public boolean isDerived() {
        return false;
    }

    @Override
    public String getStringRepresentation() {
        return this.id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataSourceBackendSourceSystem other = (DataSourceBackendSourceSystem) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
