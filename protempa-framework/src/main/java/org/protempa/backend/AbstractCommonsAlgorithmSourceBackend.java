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
package org.protempa.backend;

import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.asb.AbstractAlgorithmSourceBackend;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsAlgorithmSourceBackend
        extends AbstractAlgorithmSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec<?> backendInstanceSpec)
            throws BackendInitializationException {
        super.initialize(backendInstanceSpec);
        CommonsBackend.initialize(this, backendInstanceSpec);
    }

    @Override
    public final String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }

    protected final String nameForErrors() {
        return CommonsBackend.nameForErrors(this);
    }
    
    @BackendProperty
    @Override
    public final void setId(String id) {
        super.setId(id);
    }
    
    @Override
    public final String getId() {
        return super.getId();
    }

}
