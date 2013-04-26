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

import org.protempa.backend.TermSourceBackendUpdatedEvent;
import org.protempa.backend.tsb.TermSourceBackend;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class TermSourceImpl extends AbstractSource<TermSourceUpdatedEvent, TermSourceBackend, TermSourceUpdatedEvent, TermSourceBackendUpdatedEvent> implements TermSource {

    private final Set<String> notFoundTerms;

    public TermSourceImpl(TermSourceBackend[] backends) {
        super(backends != null ? backends : new TermSourceBackend[0]);
        this.notFoundTerms = new HashSet<String>();
    }

    @Override
    public Term readTerm(String id) throws TermSourceReadException {
        Term result = null;
        if (!notFoundTerms.contains(id)) {
            if (isClosed()) {
                throw new TermSourceReadException(
                        "Term source already closed!");
            }
            for (TermSourceBackend backend : getBackends()) {
                result = backend.readTerm(id);
                if (result != null) {
                    return result;
                }
            }
            this.notFoundTerms.add(id);
        }

        return result;
    }

    /**
     * Gets the term subsumption for the given term ID. The subsumption is the
     * term itself and all of its descendants.
     *
     * @param termId the term ID to subsume
     * @return a {@link List} of term IDs composing the given term's subsumption
     */
    @Override
    public List<String> getTermSubsumption(String termId)
            throws TermSourceReadException {
        List<String> result = null;
        if (isClosed()) {
            throw new TermSourceReadException("Term source already closed!");
        }
        for (TermSourceBackend backend : getBackends()) {
            result = backend.getSubsumption(termId);
            if (result != null) {
                return result;
            }
        }

        return result;
    }

    @Override
    public void close() throws SourceCloseException {
        clear();
        super.close();
    }

    @Override
    protected void throwCloseException(List<BackendCloseException> exceptions) throws SourceCloseException {
        throw new TermSourceCloseException(exceptions);
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
