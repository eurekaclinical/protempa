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
import org.protempa.backend.Backend;
import org.protempa.Term;
import org.protempa.TermSource;
import org.protempa.backend.TermSourceBackendUpdatedEvent;
import org.protempa.TermSourceReadException;

/**
 * Interface for term source backends
 * 
 * @author Michel Mansour
 */
public interface TermSourceBackend extends
        Backend<TermSourceBackendUpdatedEvent, TermSource> {

    /**
     * Reads a term from the given terminology
     * 
     * @param id
     *            the term to find
     * @return a {@link Term} matching the given term in the given terminology
     * @throws TermSourceReadException
     *             if the {@link TermSource} is unreadable
     */
    Term readTerm(String id) throws TermSourceReadException;

    /**
     * Reads an array of terms from the given terminology
     * 
     * @param ids
     *            the terms to find
     * @return an array of {@link Term}s matching the given terms in the given
     *         terminology
     * @throws TermSourceReadException
     *             if the {@link TermSource} is unreadable
     */
    Map<String, Term> readTerms(String[] ids) throws TermSourceReadException;

    /**
     * Retrieves all of the descendants of the term specified by the given ID.
     * In this case, the descendants include the given term itself.
     * 
     * @param id
     *            the term whose descendants are to be retrieved
     * @return a {@link List} of {@link String}s that are the term IDs of the
     *         descendents of the given term
     * @throws TermSourceReadException
     *             if the {@link TermSource} is unreadable
     */
    List<String> getSubsumption(String id) throws TermSourceReadException;
}
