package org.protempa.backend.test;

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.tsb.AbstractTermSourceBackend;

public final class MockTermSourceBackend extends AbstractTermSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
    }
    
    /**
     * Make public so that tests can call it.
     * @see {@link AbstractTermSourceBackend}
     */
    @Override
    public void fireTermSourceBackendUpdated() {
        super.fireTermSourceBackendUpdated();
    }
    
    @Override
    public String getDisplayName() {
        return "Mock Term Source Backend";
    }

}
