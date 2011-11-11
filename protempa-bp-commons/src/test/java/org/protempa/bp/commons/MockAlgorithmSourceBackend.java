package org.protempa.bp.commons;

import org.protempa.Algorithm;
import org.protempa.AlgorithmSourceReadException;
import org.protempa.Algorithms;

@BackendInfo(displayName = "Mock Algorithm Source Backend")
public class MockAlgorithmSourceBackend 
        extends AbstractCommonsAlgorithmSourceBackend {

    public Algorithm readAlgorithm(String id, Algorithms algorithms)
            throws AlgorithmSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void readAlgorithms(Algorithms algorithms)
            throws AlgorithmSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
