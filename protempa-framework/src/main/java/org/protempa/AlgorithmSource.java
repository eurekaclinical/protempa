/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa;

import java.util.List;
import java.util.Set;
import org.protempa.backend.AlgorithmSourceBackendUpdatedEvent;
import org.protempa.backend.asb.AlgorithmSourceBackend;

/**
 *
 * @author Andrew Post
 */
public interface AlgorithmSource extends Source<AlgorithmSourceUpdatedEvent, AlgorithmSourceBackend, AlgorithmSourceBackendUpdatedEvent>{

    /**
     * Read an algorithm with the given id.
     *
     * @param id an algorithm id {@link String}.
     * @return an {@link Algorithm} object, or <code>null</code> if no algorithm
     * with the specified id exists. If a <code>null</code> id is      * specified, <code>null</code> is returned.
     *
     * @throws AlgorithmSourceReadException when an error occurs in a backend
     * reading the specified algorithm.
     */
    Algorithm readAlgorithm(String id) throws AlgorithmSourceReadException;

    /**
     * Reads all algorithms in this algorithm source.
     *
     * @return an unmodifiable <code>Set</code> of <code>Algorithm</code>
     * objects. Guaranteed not to return <code>null</code>.
     *
     * @throws AlgorithmSourceReadException when an error occurs in a backend
     * reading an algorithm.
     */
    Set<Algorithm> readAlgorithms() throws AlgorithmSourceReadException;

}
