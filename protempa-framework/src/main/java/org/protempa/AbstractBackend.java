package org.protempa;

import java.util.ArrayList;
import java.util.List;

/**
 * Common interface for PROTEMPA backends.
 * 
 * @author Andrew Post
 * 
 * @param <E> the {@link BackendUpdatedEvent} that this backend fires.
 * @param <S> the backend's corresponding {@link Source}.
 */
public abstract class AbstractBackend<E extends BackendUpdatedEvent, S extends Source<E>>
        implements Backend<E, S> {

    private final List<BackendListener<E>> listenerList;

    public AbstractBackend() {
        this.listenerList = new ArrayList<BackendListener<E>>();
    }

    public void addBackendListener(BackendListener<E> listener) {
        if (listener != null)
            this.listenerList.add(listener);
    }

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
}
