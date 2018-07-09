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
package org.protempa.backend.ksb;

import java.util.ArrayList;
import java.util.List;
import org.drools.util.StringUtils;

import org.protempa.backend.AbstractBackend;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.valueset.ValueSet;
import org.protempa.query.And;

/**
 * Skeletal implementation of the <code>KnowledgeSourceBackend</code> interface
 * to minimize the effort required to implement this interface.
 * 
 * @author Andrew Post
 */
public abstract class AbstractKnowledgeSourceBackend extends
        AbstractBackend<KnowledgeSourceBackendUpdatedEvent>
        implements KnowledgeSourceBackend {

    /**
     * A default implementation that returns <code>null</code>. Override this
     * if your knowledge base contains value sets.
     *
     * @param id a proposition id {@link String}.
     * @return an {@link ValueSet}, always <code>null</code> in
     * this implementation.
     */
    @Override
    public ValueSet readValueSet(String id)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * Implemented as a no-op.
     * 
     * @see org.protempa.KnowledgeSourceBackend#close()
     */
    @Override
    public void close() {
    }

    /**
     * Notifies registered listeners that the backend has been updated.
     * 
     * @see org.protempa.KnowledgeSourceBackendUpdatedEvent
     * @see org.protempa.KnowledgeSourceBackendListener
     */
    protected void fireKnowledgeSourceBackendUpdated() {
        fireBackendUpdated(new KnowledgeSourceBackendUpdatedEvent(this));
    }
}
