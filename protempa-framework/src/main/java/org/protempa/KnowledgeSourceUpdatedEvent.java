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
package org.protempa;

/**
 * The event generated when the knowledge source is updated.
 *
 * @author Andrew Post
 */
public final class KnowledgeSourceUpdatedEvent extends SourceUpdatedEvent {

    private static final long serialVersionUID = 2757359115821263581L;
    
    private final KnowledgeSource knowledgeSource;

    /**
     * Constructs an event with the source
     * <code>KnowledgeSource</code> that generated the event.
     *
     * @param knowledgeSourceBackend an <code>KnowledgeSourceBackend</code>.
     */
    public KnowledgeSourceUpdatedEvent(KnowledgeSource knowledgeSource) {
        super(knowledgeSource);
        this.knowledgeSource = knowledgeSource;
    }

    @Override
    public KnowledgeSource getProtempaSource() {
        return this.knowledgeSource;
    }
    
    
}
