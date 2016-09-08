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
public class ResourceMappingsFactory extends AbstractMappingsFactory {

    private final String resourcePrefix;
    private final Class<?> cls;

    public ResourceMappingsFactory(String resourcePrefix, Class<?> cls) {
        if (resourcePrefix == null) {
            throw new IllegalArgumentException("resourcePrefix cannot be null");
        }
        if (cls == null) {
            throw new IllegalArgumentException("cls cannot be null");
        }
        if (resourcePrefix.endsWith("/")) {
            this.resourcePrefix = resourcePrefix;
        } else {
            this.resourcePrefix = resourcePrefix + "/";
        }
        this.cls = cls;
    }

    @Override
    public ResourceMappings getInstance(String resource) throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        try {
            ResourceMappings m = new ResourceMappings(this.resourcePrefix + resource, cls);
            addMappings(m);
            return m;
        } catch (IOException ex) {
            throw new IOException("Error reading resource " + resource, ex);
        }
    }

}
