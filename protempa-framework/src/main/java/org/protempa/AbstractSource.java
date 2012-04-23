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
import org.protempa.backend.Backend;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSource<E extends SourceUpdatedEvent,
        T extends BackendUpdatedEvent> implements Source<T> {

    private final List<SourceListener<E>> listenerList;
    private Backend[] backends;
    private boolean closed;

    /**
     * Makes this {@link Source} a listener to events fired by the provided
     * {@link Backend}s.
     * 
     * @param backends a {@link Backend[]}.
     */
    AbstractSource(Backend[] backends) {
        this.listenerList = new ArrayList<SourceListener<E>>();
        if (backends != null) {
            this.backends = backends;
            for (Backend backend : this.backends) {
                backend.addBackendListener(this);
            }
        } else {
            this.backends = null;
        }
    }

    /**
     * Adds a listener that gets called whenever something changes.
     *
     * @param listener
     *            a {@link DataSourceListener}.
     */
    public void addSourceListener(SourceListener<E> listener) {
        if (listener != null) {
            this.listenerList.add(listener);
        }
    }

    /**
     * Removes a listener so that changes to the source are no longer sent.
     *
     * @param listener
     *            a {@link DataSourceListener}.
     */
    public void removeSourceListener(SourceListener<E> listener) {
        this.listenerList.remove(listener);
    }

    /**
     * Notifies registered listeners that the source has been updated.
     *
     * @param e a {@link SourceUpdatedEvent} representing an update.
     *
     * @see SourceListener
     */
    protected void fireSourceUpdated(E e) {
        for (int i = 0, n = this.listenerList.size(); i < n; i++) {
            this.listenerList.get(i).sourceUpdated(e);
        }
    }

    protected void fireClosedUnexpectedly(SourceClosedUnexpectedlyEvent<T> e) {
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
    public void close() {
        if (this.backends != null) {
            for (Backend backend : this.backends) {
                backend.removeBackendListener(this);
            }
        }
        this.backends = null;
        this.closed = true;
    }

    protected boolean isClosed() {
        return this.closed;
    }

    @Override
    public void unrecoverableErrorOccurred(UnrecoverableBackendErrorEvent e) {
        close();
        this.fireClosedUnexpectedly(new SourceClosedUnexpectedlyEvent<T>(this));
    }
}