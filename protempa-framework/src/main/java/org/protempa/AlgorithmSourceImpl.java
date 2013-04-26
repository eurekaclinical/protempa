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
package org.protempa;

import org.protempa.backend.AlgorithmSourceBackendUpdatedEvent;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * A read-only "interface" to an externally maintained set of pattern detection
 * algorithm implementations. Backends implementing
 * {@link AlgorithmSourceBackend} provide implementations of the actual
 * algorithms available.
 *
 * @author Andrew Post
 */
public final class AlgorithmSourceImpl
        extends AbstractSource<AlgorithmSourceUpdatedEvent, AlgorithmSourceBackend, AlgorithmSourceUpdatedEvent, AlgorithmSourceBackendUpdatedEvent> implements AlgorithmSource {

    private final Algorithms algorithms;
    private boolean readAlgorithmsCalled;

    /**
     * Constructor for specifying a mix of {@link AlgorithmSourceBackend}s and
     * {@link Properties} objects specifying algorithm source backends.
     *
     * @param backends an array of {@link AlgorithmSourceBackend}.
     */
    public AlgorithmSourceImpl(AlgorithmSourceBackend[] backends) {
        super(backends != null ? backends : new AlgorithmSourceBackend[0]);
        this.algorithms = new Algorithms();
    }

    /**
     * Connect to the algorithm backend.
     */
    private void initializeIfNeeded() throws AlgorithmSourceReadException {
        if (isClosed()) {
            throw new AlgorithmSourceReadException("Algorithm source already closed!");
        }
    }

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
    @Override
    public Algorithm readAlgorithm(String id) throws AlgorithmSourceReadException {
        Algorithm result = null;
        if (id != null) {
            if (algorithms != null) {
                result = algorithms.getAlgorithm(id);
            }
            if (result == null) {
                initializeIfNeeded();
                for (AlgorithmSourceBackend backend : getBackends()) {
                    result = backend.readAlgorithm(id, algorithms);
                    if (result != null) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Reads all algorithms in this algorithm source.
     *
     * @return an unmodifiable <code>Set</code> of <code>Algorithm</code>
     * objects. Guaranteed not to return <code>null</code>.
     *
     * @throws AlgorithmSourceReadException when an error occurs in a backend
     * reading an algorithm.
     */
    @Override
    public Set<Algorithm> readAlgorithms() throws AlgorithmSourceReadException {
        initializeIfNeeded();
        if (algorithms != null) {
            if (!readAlgorithmsCalled && getBackends() != null) {
                for (AlgorithmSourceBackend backend : getBackends()) {
                    backend.readAlgorithms(algorithms);
                }
                readAlgorithmsCalled = true;
            }
            return algorithms.getAlgorithms();
        }
        return Collections.emptySet();
    }

    @Override
    public void close() throws SourceCloseException {
        clear();
        super.close();
    }

    @Override
    protected void throwCloseException(List<BackendCloseException> exceptions) throws SourceCloseException {
        throw new AlgorithmSourceCloseException(exceptions);
    }
    
    

    @Override
    public void clear() {
        if (algorithms != null) {
            algorithms.closeAndClear();

        }
        readAlgorithmsCalled = false;
    }

    @Override
    public void backendUpdated(AlgorithmSourceBackendUpdatedEvent event) {
        fireAlgorithmSourceUpdated();
    }

    /**
     * Notifies registered listeners that the algorithm source has been updated.
     *
     * @see AlgorithmSourceUpdatedEvent
     * @see SourceListener
     */
    private void fireAlgorithmSourceUpdated() {
        fireSourceUpdated(new AlgorithmSourceUpdatedEvent(this));
    }
}
