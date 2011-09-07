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
