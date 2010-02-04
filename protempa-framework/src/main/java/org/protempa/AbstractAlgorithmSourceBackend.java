package org.protempa;

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
