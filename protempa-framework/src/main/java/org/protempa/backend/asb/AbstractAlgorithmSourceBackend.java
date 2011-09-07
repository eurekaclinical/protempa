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
        AbstractBackend<AlgorithmSourceBackendUpdatedEvent, AlgorithmSource>
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
