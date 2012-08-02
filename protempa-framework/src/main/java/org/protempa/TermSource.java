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

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.TermSourceBackendUpdatedEvent;
import org.protempa.backend.tsb.TermSourceBackend;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protempa.backend.BackendNewInstanceException;

public final class TermSource extends
        AbstractSource<TermSourceUpdatedEvent, TermSourceBackendUpdatedEvent> {

    private final BackendManager<TermSourceBackendUpdatedEvent, TermSource, TermSourceBackend> backendManager;

    private final Set<String> notFoundTerms;

    public TermSource(TermSourceBackend[] backends) {
        super(backends);
        this.backendManager = new BackendManager<TermSourceBackendUpdatedEvent, TermSource, TermSourceBackend>(
                this, backends);
        this.notFoundTerms = new HashSet<String>();
    }

    /**
     * Connect to the term source's backend(s).
     */
    private void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
        if (isClosed()) {
            throw new IllegalStateException("Term source already closed!");
        }
        this.backendManager.initializeIfNeeded();
    }

    public Term readTerm(String id) throws TermSourceReadException {
        Term result = null;
        if (!notFoundTerms.contains(id)) {
            try {
                initializeIfNeeded();
            } catch (BackendInitializationException ex) {
                throw new TermSourceReadException(ex);
            } catch (BackendNewInstanceException ex) {
                throw new TermSourceReadException(ex);
            }
            if (this.backendManager.getBackends() != null) {
                for (TermSourceBackend backend : this.backendManager
                        .getBackends()) {
                    result = backend.readTerm(id);
                    if (result != null) {
                        return result;
                    }
                }
                this.notFoundTerms.add(id);
            }
        }

        return result;
    }

    /**
     * Gets the term subsumption for the given term ID. The subsumption is the
     * term itself and all of its descendants.
     * 
     * @param termId
     *            the term ID to subsume
     * @return a {@link List} of term IDs composing the given term's subsumption
     */
    public List<String> getTermSubsumption(String termId)
            throws TermSourceReadException {
        List<String> result = null;
        try {
            initializeIfNeeded();
        } catch (BackendInitializationException ex) {
            throw new TermSourceReadException(ex);
        } catch (BackendNewInstanceException ex) {
            throw new TermSourceReadException(ex);
        }
        if (this.backendManager.getBackends() != null) {
            for (TermSourceBackend backend : this.backendManager.getBackends()) {
                result = backend.getSubsumption(termId);
                if (result != null) {
                    return result;
                }
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.AbstractSource#close()
     */
    @Override
    public void close() {
        clear();
        this.backendManager.close();
        super.close();
    }

    /**
     * Notifies registered listeners that the term source has been updated.
     * 
     * @see {@link TermSourceUpdatedEvent}
     * @see {@link SourceListener}
     */
    private void fireTermSourceUpdated() {
        fireSourceUpdated(new TermSourceUpdatedEvent(this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.BackendListener#backendUpdated(org.protempa.BackendUpdatedEvent
     * )
     */
    @Override
    public void backendUpdated(TermSourceBackendUpdatedEvent event) {
        clear();
        fireTermSourceUpdated();
    }
    
    @Override
    public void clear() {

    }

}
