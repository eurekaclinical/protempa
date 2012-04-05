/*
 * #%L
 * Protempa Framework
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
package org.protempa.backend;

import org.protempa.backend.ksb.KnowledgeSourceBackend;

/**
 * The event generated when the knowledge source backend is updated.
 * 
 * @author Andrew Post
 */
public final class KnowledgeSourceBackendUpdatedEvent extends BackendUpdatedEvent {

    private static final long serialVersionUID = 2757359115821263581L;

    /**
     * Constructs an event with the source <code>KnowledgeSourceBackend</code>
     * that generated the event.
     * 
     * @param knowledgeSourceBackend
     *            an <code>KnowledgeSourceBackend</code>.
     */
    public KnowledgeSourceBackendUpdatedEvent(
            KnowledgeSourceBackend knowledgeSourceBackend) {
        super(knowledgeSourceBackend);
    }
}
