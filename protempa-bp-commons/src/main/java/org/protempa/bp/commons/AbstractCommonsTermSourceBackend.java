/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.bp.commons;

import org.protempa.backend.tsb.AbstractTermSourceBackend;
import org.protempa.backend.TermSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

public abstract class AbstractCommonsTermSourceBackend extends
        AbstractTermSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws TermSourceBackendInitializationException {
        CommonsBackend.initialize(this, config);
    }

    @Override
    public String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }
    
    protected final String nameForErrors() {
        return CommonsBackend.nameForErrors(this);
    }

}
