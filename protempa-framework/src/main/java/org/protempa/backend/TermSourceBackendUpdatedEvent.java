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

import org.protempa.backend.tsb.TermSourceBackend;

public class TermSourceBackendUpdatedEvent extends BackendUpdatedEvent {

    private static final long serialVersionUID = 1541779603882476882L;
    
    private final TermSourceBackend termSourceBackend;

    public TermSourceBackendUpdatedEvent(TermSourceBackend termSourceBackend) {
        super(termSourceBackend);
        this.termSourceBackend = termSourceBackend;
    }

    @Override
    public TermSourceBackend getBackend() {
        return this.termSourceBackend;
    }

}
