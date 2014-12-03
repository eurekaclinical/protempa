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

import java.util.ArrayList;
import java.util.List;
import org.protempa.BackendListener;

/**
 * Common interface for PROTEMPA backends.
 * 
 * @author Andrew Post
 * 
 * @param <E> the {@link BackendUpdatedEvent} that this backend fires.
 */
public abstract class AbstractBackend<E extends BackendUpdatedEvent> 
        implements Backend<E> {

    private final List<BackendListener<E>> listenerList;
    
    private String configurationsId;
    
    private String id;

    public AbstractBackend() {
        this.listenerList = new ArrayList<>();
    }

    @Override
    public void initialize(BackendInstanceSpec<?> config) 
            throws BackendInitializationException {
        this.configurationsId = config.getConfigurationsId();
        if (this.id == null) {
            this.id = config.getBackendSpec().getId();
        }
    }
    
    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String getConfigurationsId() {
        return this.configurationsId;
    }

    @Override
    public void addBackendListener(BackendListener<E> listener) {
        if (listener != null) {
            this.listenerList.add(listener);
        }
    }

    @Override
    public void removeBackendListener(BackendListener<E> listener) {
        this.listenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners when the backend has been updated.
     *
     * @param e a {@link BackendUpdatedEvent} representing an update.
     */
    protected void fireBackendUpdated(E e) {
        for (int i = 0, n = this.listenerList.size(); i < n; i++) {
            this.listenerList.get(i).backendUpdated(e);
        }
    }

    /**
     * Notifies all registered listeners when an unrecoverable error has
     * occurred in a backend.
     *
     * @param e a {@link UnrecoverableBackendErrorEvent} representing the
     * cause of the error.
     */
    protected void fireUnrecoverableError(UnrecoverableBackendErrorEvent e) {
        for (int i = 0, n = this.listenerList.size(); i < n; i++) {
            this.listenerList.get(i).unrecoverableErrorOccurred(e);
        }
    }
}
