package org.protempa.backend.asb;

import org.protempa.Algorithm;
import org.protempa.AlgorithmSource;
import org.protempa.backend.AlgorithmSourceBackendUpdatedEvent;
import org.protempa.AlgorithmSourceReadException;
import org.protempa.Algorithms;
import org.protempa.backend.Backend;

/**
 * A set of algorithms for use in PROTEMPA pattern detection.
 * 
 * @author Andrew
 */
public interface AlgorithmSourceBackend extends
        Backend<AlgorithmSourceBackendUpdatedEvent, AlgorithmSource> {

    /**
     * Reads an algorithm from this backend.
     * 
     * @param id
     *            an algorithm id {@link String}.
     * @param algorithms
     *            an {@link Algorithms} object.
     * @return an {@link Algorithm} object or <code>null</code> if an
     *         algorithm with the given <code>id</code> was not found.
     * @throws AlgorithmSourceReadException when an error occurs reading the specified
     * algorithm.
     */
    Algorithm readAlgorithm(String id, Algorithms algorithms)
            throws AlgorithmSourceReadException;

    /**
     * Read the available algorithms.
     * 
     * @param algorithms
     *            an <code>Algorithms</code> object.
     * @throws AlgorithmSourceReadException when an error occurs reading an algorithm.
     */
    void readAlgorithms(Algorithms algorithms) throws AlgorithmSourceReadException;
}
