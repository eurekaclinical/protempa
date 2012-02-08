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

import org.protempa.backend.UnrecoverableBackendErrorEvent;
import org.protempa.backend.BackendUpdatedEvent;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Common interface for backend listeners.
 * 
 * @author Andrew Post
 * 
 * @param <E>
 *            an {@link EventObject}
 */
public interface BackendListener<E extends BackendUpdatedEvent> extends
        EventListener {

    /**
     * Notifies a listener when it needs to reread data in the backend.
     *
     * @param evt
     *            an {@link EventObject}
     */
    void backendUpdated(E evt);

    /**
     * Some unrecoverable error occurred while using a backend module, like a
     * connection to a server was lost.
     * 
     * @param e a {@link UnrecoverableBackendErrorEvent}.
     */
    void unrecoverableErrorOccurred(UnrecoverableBackendErrorEvent e);
}
