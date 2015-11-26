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
package org.protempa;

import org.protempa.backend.UnrecoverableBackendErrorEvent;
import org.protempa.backend.BackendUpdatedEvent;
import org.protempa.backend.Backend;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSource<S extends SourceUpdatedEvent, 
        B extends Backend, E extends SourceUpdatedEvent, 
        T extends BackendUpdatedEvent> implements Source<S, B, T> {

    private final List<SourceListener<S>> listenerList;
    private B[] backends;
    private boolean closed;

    /**
     * Makes this {@link Source} a listener to events fired by the provided
     * {@link Backend}s.
     *
     * @param backends a {@link Backend[]}. Cannot be <code>null</code>, and
     * cannot contain <code>null</code> elements or duplicates.
     */
    AbstractSource(B[] backends) {
        assert backends != null : "backends cannot be null";
        ProtempaUtil.checkArrayForNullElement(backends, "backends");
        ProtempaUtil.checkArrayForDuplicates(backends, "backends");
        this.listenerList = new ArrayList<>();
        this.backends = backends.clone();
        for (Backend backend : this.backends) {
            backend.addBackendListener(this);
        }
    }
    
    /**
     * Gets the backends registered to this source.
     * 
     * @return an array of backends. Guaranteed not <code>null</code>.
     */
    @Override
    public final B[] getBackends() {
        return this.backends.clone();
    }

    /**
     * Adds a listener that gets called whenever something changes.
     *
     * @param listener a {@link DataSourceListener}.
     */
    @Override
    public final void addSourceListener(SourceListener<S> listener) {
        if (listener != null) {
            this.listenerList.add(listener);
        }
    }

    /**
     * Removes a listener so that changes to the source are no longer sent.
     *
     * @param listener a {@link DataSourceListener}.
     */
    @Override
    public final void removeSourceListener(SourceListener<S> listener) {
        this.listenerList.remove(listener);
    }

    /**
     * Notifies registered listeners that the source has been updated.
     *
     * @param e a {@link SourceUpdatedEvent} representing an update.
     *
     * @see SourceListener
     */
    protected void fireSourceUpdated(S e) {
        for (int i = 0, n = this.listenerList.size(); i < n; i++) {
            this.listenerList.get(i).sourceUpdated(e);
        }
    }

    protected void fireClosedUnexpectedly(SourceClosedUnexpectedlyEvent<S, B, T> e) {
        for (int i = 0, n = this.listenerList.size(); i < n; i++) {
            this.listenerList.get(i).closedUnexpectedly(e);
        }
    }

    /**
     * Removes this {@link Source} as a listener to the {@link Backend}s
     * provided to the constructor.
     *
     * Must be called by subclasses, or proper cleanup will not occur.
     */
    @Override
    public void close() throws SourceCloseException {
        for (Backend backend : this.backends) {
            backend.removeBackendListener(this);
        }
        List<BackendCloseException> exceptions = new ArrayList<>();
        for (Backend backend : this.backends) {
            try {
                backend.close();
            } catch (BackendCloseException ex) {
                exceptions.add(ex);
            } catch (Error | RuntimeException ex) {
                exceptions.add(new BackendCloseException(ex));
            }
        }
        if (!exceptions.isEmpty()) {
            throw new SourceCloseException(exceptions.toArray(new BackendCloseException[exceptions.size()]));
        }
        this.closed = true;
    }
    
    protected boolean isClosed() {
        return this.closed;
    }

    @Override
    public final void unrecoverableErrorOccurred(
            UnrecoverableBackendErrorEvent e) {
        try {
            close();
        } catch (SourceCloseException ignore) {
        }
        fireClosedUnexpectedly(new SourceClosedUnexpectedlyEvent<>(this));
    }
}
