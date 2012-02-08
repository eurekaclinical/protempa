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
package org.protempa.backend.tsb;

import java.util.List;
import java.util.Map;
import org.protempa.backend.AbstractBackend;
import org.protempa.Term;
import org.protempa.TermSource;
import org.protempa.backend.TermSourceBackendUpdatedEvent;
import org.protempa.TermSourceReadException;

/**
 * 
 * @author Michel Mansour
 */
public abstract class AbstractTermSourceBackend extends
        AbstractBackend<TermSourceBackendUpdatedEvent, TermSource> implements
        TermSourceBackend {

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.TermSourceBackend#readTerm(java.lang.String,
     *      org.protempa.Terminology)
     */
    @Override
    public Term readTerm(String id) throws TermSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.TermSourceBackend#readTerms(String[], Terminology)
     */
    @Override
    public Map<String, Term> readTerms(String[] ids)
            throws TermSourceReadException {
        return null;
    }

    /**
     * Implemented as a no-op
     * 
     * @see org.protempa.Backend#close()
     */
    @Override
    public void close() {
    }

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.Backend#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return null;
    }
    

    /**
     * A default implementation that returns null
     * 
     * @see org.protempa.TermSourceBackend#getSubsumption(java.lang.String)
     */
    @Override
    public List<String> getSubsumption(String id)
            throws TermSourceReadException {
        return null;
    }

    protected void fireTermSourceBackendUpdated() {
        fireBackendUpdated(new TermSourceBackendUpdatedEvent(this));
    }

}
