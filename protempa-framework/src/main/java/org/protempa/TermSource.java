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
import org.protempa.backend.TermSourceBackendUpdatedEvent;
import org.protempa.backend.tsb.TermSourceBackend;

/**
 *
 * @author Andrew Post
 */
public interface TermSource extends Source<TermSourceUpdatedEvent, TermSourceBackend, TermSourceBackendUpdatedEvent> {

    /**
     * Gets the term subsumption for the given term ID. The subsumption is the
     * term itself and all of its descendants.
     *
     * @param termId the term ID to subsume
     * @return a {@link List} of term IDs composing the given term's subsumption
     */
    List<String> getTermSubsumption(String termId) throws TermSourceReadException;

    Term readTerm(String id) throws TermSourceReadException;

}
