/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
