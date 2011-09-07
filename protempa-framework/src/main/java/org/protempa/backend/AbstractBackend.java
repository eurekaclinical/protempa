package org.protempa.backend;

import java.util.ArrayList;
import java.util.List;
import org.protempa.BackendListener;
import org.protempa.Source;

/**
 * Common interface for PROTEMPA backends.
 * 
 * @author Andrew Post
 * 
 * @param <E> the {@link BackendUpdatedEvent} that this backend fires.
 * @param <S> the backend's corresponding {@link Source}.
 */
public abstract class AbstractBackend<E extends BackendUpdatedEvent, S extends Source<E>>  implements Backend<E, S> {

    private final List<BackendListener<E>> listenerList;

    public AbstractBackend() {
        this.listenerList = new ArrayList<BackendListener<E>>();
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
