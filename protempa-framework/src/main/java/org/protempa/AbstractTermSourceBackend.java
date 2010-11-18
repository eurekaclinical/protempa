package org.protempa;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Michel Mansour
 */
public abstract class AbstractTermSourceBackend extends
        AbstractBackend<TermSourceBackendUpdatedEvent, TermSource> implements
        TermSourceBackend {

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.TermSourceBackend#readTerm(java.lang.String,
     *      org.protempa.Terminology)
     */
    @Override
    public Term readTerm(String id) throws TermSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.TermSourceBackend#readTerms(String[], Terminology)
     */
    @Override
    public Map<String, Term> readTerms(String[] ids)
            throws TermSourceReadException {
        return null;
    }

    /**
     * Implemented as a no-op
     * 
     * @see org.protempa.Backend#close()
     */
    @Override
    public void close() {
    }

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.Backend#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return null;
    }
    

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.TermSourceBackend#getSubsumption(java.lang.String)
     */
    @Override
    public List<String> getSubsumption(String id)
            throws TermSourceReadException {
        return null;
    }

    protected void fireTermSourceBackendUpdated() {
        fireBackendUpdated(new TermSourceBackendUpdatedEvent(this));
    }

}
