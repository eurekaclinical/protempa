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
package org.protempa;

import org.protempa.backend.UnrecoverableBackendErrorEvent;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.AlgorithmSourceBackendUpdatedEvent;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.protempa.backend.BackendNewInstanceException;

/**
 * A read-only "interface" to an externally maintained set of pattern detection
 * algorithm implementations. Backends implementing
 * {@link AlgorithmSourceBackend}
 * provide implementations of the actual algorithms available.
 * 
 * @author Andrew Post
 */
public final class AlgorithmSource
        extends AbstractSource<AlgorithmSourceUpdatedEvent, AlgorithmSourceBackendUpdatedEvent> {

    private Algorithms algorithms;
    private BackendManager<AlgorithmSourceBackendUpdatedEvent, AlgorithmSource, AlgorithmSourceBackend> backendManager;
    private boolean readAlgorithmsCalled;

    /**
     * Constructor for specifying a mix of {@link AlgorithmSourceBackend}s and
     * {@link Properties} objects specifying algorithm source backends.
     *
     * @param backends an array of {@link AlgorithmSourceBackend}.
     */
    public AlgorithmSource(AlgorithmSourceBackend[] backends) {
        super(backends);
        this.backendManager =
                new BackendManager<AlgorithmSourceBackendUpdatedEvent, AlgorithmSource, AlgorithmSourceBackend>(
                backends);
    }

    /**
     * Connect to the algorithm backend.
     */
    private void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
        if (isClosed()) {
            throw new IllegalStateException("Algorithm source already closed!");
        }
        this.backendManager.initializeIfNeeded();
        if (this.backendManager.getBackends() != null
                && this.algorithms == null) {
            this.algorithms = new Algorithms();
        }
    }

    /**
     * Read an algorithm with the given id.
     *
     * @param id
     *            an algorithm id {@link String}.
     * @return an {@link Algorithm} object, or <code>null</code> if no
     * algorithm with the specified id exists. If a <code>null</code> id is
     * specified, <code>null</code> is returned.
     *
     * @throws AlgorithmSourceReadException when an error occurs in a backend
     * reading the specified algorithm.
     */
    public Algorithm readAlgorithm(String id) throws AlgorithmSourceReadException {
        Algorithm result = null;
        if (id != null) {
            if (algorithms != null) {
                result = algorithms.getAlgorithm(id);
            }
            if (result == null) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new AlgorithmSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new AlgorithmSourceReadException(ex);
                }
                List<AlgorithmSourceBackend> backends =
                        this.backendManager.getBackends();
                if (backends != null) {
                    for (AlgorithmSourceBackend backend : backends) {
                        result = backend.readAlgorithm(id, algorithms);
                        if (result != null) {
                            break;
                        }
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
     *         objects. Guaranteed not to return <code>null</code>.
     *
     * @throws AlgorithmSourceReadException when an error occurs in a backend
     * reading an algorithm.
     */
    public Set<Algorithm> readAlgorithms() throws AlgorithmSourceReadException {
        try {
            initializeIfNeeded();
        } catch (BackendInitializationException ex) {
            throw new AlgorithmSourceReadException(ex);
        } catch (BackendNewInstanceException ex) {
            throw new AlgorithmSourceReadException(ex);
        }
        if (algorithms != null) {
            if (!readAlgorithmsCalled
                    && this.backendManager.getBackends() != null) {
                for (AlgorithmSourceBackend backend : this.backendManager.getBackends()) {
                    backend.readAlgorithms(algorithms);
                }
                readAlgorithmsCalled = true;
            }
            return algorithms.getAlgorithms();
        }
        return Collections.emptySet();
    }

    @Override
    public void close() {
        clear();
        this.backendManager.close();
        super.close();
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

    @Override
    public void unrecoverableErrorOccurred(UnrecoverableBackendErrorEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
