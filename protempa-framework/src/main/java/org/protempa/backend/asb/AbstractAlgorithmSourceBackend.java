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
package org.protempa.backend.asb;

import org.protempa.backend.AbstractBackend;
import org.protempa.AlgorithmSource;
import org.protempa.backend.AlgorithmSourceBackendUpdatedEvent;

/**
 * Skeletal implementation of the {@link AlgorithmSourceBackend}
 * interface to minimize the effort required to implement it.
 * 
 * @author Andrew Post
 */
public abstract class AbstractAlgorithmSourceBackend extends 
        AbstractBackend<AlgorithmSourceBackendUpdatedEvent>
        implements AlgorithmSourceBackend {

    /**
     * Implemented as a no-op.
     *
     * @see org.protempa.AlgorithmSourceBackend#close()
     */
    @Override
    public void close() {
    }

    /**
     * Notifies registered listeners that the backend has been updated.
     *
     * @see AlgorithmSourceBackendUpdatedEvent
     * @see org.protempa.AlgorithmSourceBackendListener
     */
    protected void fireAlgorithmSourceBackendUpdated() {
        fireBackendUpdated(new AlgorithmSourceBackendUpdatedEvent(this));
    }
}
