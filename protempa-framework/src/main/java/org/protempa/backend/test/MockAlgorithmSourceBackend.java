package org.protempa.backend.test;

import org.protempa.*;
import org.protempa.backend.BackendInstanceSpec;


public final class MockAlgorithmSourceBackend
        extends AbstractAlgorithmSourceBackend {

	public AbstractAlgorithm readAlgorithm(String id, Algorithms algorithms) {
		return null;
	}

	public void readAlgorithms(Algorithms algorithms) {
	}

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



}
