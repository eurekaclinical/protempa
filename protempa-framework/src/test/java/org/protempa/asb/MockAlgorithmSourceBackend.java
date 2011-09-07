package org.protempa.asb;

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.asb.AbstractAlgorithmSourceBackend;
import org.protempa.*;
import org.protempa.backend.BackendInstanceSpec;

public final class MockAlgorithmSourceBackend
        extends AbstractAlgorithmSourceBackend {

    @Override
    public AbstractAlgorithm readAlgorithm(String id, Algorithms algorithms) {
        return null;
    }

    @Override
    public void readAlgorithms(Algorithms algorithms) {
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
    }

    /**
     * Make public so that tests can call it.
     * @see AbstractAlgorithmSourceBackend
     */
    @Override
    public void fireAlgorithmSourceBackendUpdated() {
        super.fireAlgorithmSourceBackendUpdated();
    }

    @Override
    public String getDisplayName() {
        return "Mock Algorithm Source Backend";
    }
}
