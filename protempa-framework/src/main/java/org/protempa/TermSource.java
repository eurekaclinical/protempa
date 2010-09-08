package org.protempa;

import org.protempa.backend.BackendNewInstanceException;

public final class TermSource extends
        AbstractSource<TermSourceUpdatedEvent, TermSourceBackendUpdatedEvent> {

    private final BackendManager<TermSourceBackendUpdatedEvent, TermSource, TermSourceBackend> backendManager;

    public TermSource(TermSourceBackend[] backends) {
        super(backends);
        this.backendManager = 
                new BackendManager<TermSourceBackendUpdatedEvent, TermSource, 
                TermSourceBackend>(this, backends);
    }
    
    /**
     * Connect to the term source's backend(s).
     */
    private void intitializeIfNeeded() throws BackendInitializationException, 
        BackendNewInstanceException {
        if (isClosed()) {
            throw new IllegalStateException("Term source already closed!");
        }
        this.backendManager.initializeIfNeeded();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.AbstractSource#close()
     */
    @Override
    public void close() {
        clear();
        this.backendManager.close();
        super.close();
    }

    /**
     * Notifies registered listeners that the term source has been updated.
     * 
     * @see {@link TermSourceUpdatedEvent}
     * @see {@link SourceListener}
     */
    private void fireTermSourceUpdated() {
        fireSourceUpdated(new TermSourceUpdatedEvent(this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.BackendListener#backendUpdated(org.protempa.BackendUpdatedEvent
     * )
     */
    @Override
    public void backendUpdated(TermSourceBackendUpdatedEvent event) {
        clear();
        fireTermSourceUpdated();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Module#clear()
     */
    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

}
