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
package org.protempa.backend;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 *
 * @author Andrew Post
 */
public final class BackendSpec<B extends Backend> {

    private BackendProvider backendProvider;
    private String id;
    private String displayName;
    private List<BackendPropertySpec> propertySpecs;

    /**
     *
     * @param backendProvider
     * @param id a unique id, cannot have the | character.
     * @param displayName
     * @param propertySpecs
     */
    public BackendSpec(BackendProvider backendProvider, String id,
            String displayName,
            List<BackendPropertySpec> propertySpecs) {
        if (backendProvider == null) {
            throw new IllegalArgumentException("backendProvider cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (id.contains("|")) {
            throw new IllegalArgumentException("id cannot have the | character");
        }
        this.id = id;
        this.backendProvider = backendProvider;
        this.displayName = displayName;
        this.propertySpecs = propertySpecs;
    }

    public String getId() {
        return this.id;
    }

    public BackendProvider getBackendProvider() {
        return this.backendProvider;
    }

    public String getDisplayName() {
        return this.displayName;
    }
    
    public BackendInstanceSpec<B> newBackendInstanceSpec(int loadOrder) {
        return new BackendInstanceSpec<>(this, propertySpecs, loadOrder);
    }

    @SuppressWarnings("unchecked")
    B newBackendInstance() throws BackendNewInstanceException {
        return (B) this.backendProvider.newInstance(id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
