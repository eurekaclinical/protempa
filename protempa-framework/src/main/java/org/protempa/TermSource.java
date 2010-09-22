package org.protempa;

import java.util.HashSet;
import java.util.Set;

import org.protempa.backend.BackendNewInstanceException;

public final class TermSource extends
        AbstractSource<TermSourceUpdatedEvent, TermSourceBackendUpdatedEvent> {

    private final BackendManager<TermSourceBackendUpdatedEvent, TermSource, TermSourceBackend> backendManager;

    private final Set<String> notFoundTerms;

    public TermSource(TermSourceBackend[] backends) {
        super(backends);
        this.backendManager = new BackendManager<TermSourceBackendUpdatedEvent, TermSource, TermSourceBackend>(
                this, backends);
        this.notFoundTerms = new HashSet<String>();
    }

    /**
     * Connect to the term source's backend(s).
     */
    private void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
        if (isClosed()) {
            throw new IllegalStateException("Term source already closed!");
        }
        this.backendManager.initializeIfNeeded();
    }

    public Term readTerm(String id)
            throws TermSourceReadException {
        Term result = null;
        if (!notFoundTerms.contains(id)) {
            try {
                initializeIfNeeded();
            } catch (BackendInitializationException ex) {
                throw new TermSourceReadException(ex);
            } catch (BackendNewInstanceException ex) {
                throw new TermSourceReadException(ex);
            }
            if (this.backendManager.getBackends() != null) {
                for (TermSourceBackend backend : this.backendManager
                        .getBackends()) {
                    result = backend.readTerm(id);
                    if (result != null) {
                        return result;
                    }
                }
                this.notFoundTerms.add(id);
            }
        }

        return result;

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
