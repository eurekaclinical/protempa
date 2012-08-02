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
package org.protempa.backend.ksb;

import java.util.List;

import org.protempa.AbstractionDefinition;
import org.protempa.backend.Backend;
import org.protempa.KnowledgeSource;
import org.protempa.backend.KnowledgeSourceBackendUpdatedEvent;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.TermSubsumption;
import org.protempa.ValueSet;
import org.protempa.query.And;

/**
 * Translates from an arbitrary knowledge base to a PROTEMPA knowledge base.
 * Users of <code>KnowledgeSourceBackend</code> implementations must first call
 * {@link #initialize(java.util.Properties)} with a set of configuration properties that
 * is specific to the <code>KnowledgeSourceBackend</code> implementation.
 * 
 * @author Andrew Post
 */
public interface KnowledgeSourceBackend extends
        Backend<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource> {

    /**
     * Reads a proposition definition into the given PROTEMPA knowledge base.
     * This will only get called if the proposition definition has not already
     * been loaded.
     *
     * @param id
     *            a proposition id {@link String}. Guaranteed not
     *            <code>null</code>.
     * @return the {@link PropositionDefinition}, or <code>null</code> if none
     * with the given id was found.
     */
    PropositionDefinition readPropositionDefinition(String id)
            throws KnowledgeSourceReadException;
    
    AbstractionDefinition readAbstractionDefinition(String id)
            throws KnowledgeSourceReadException;

    /**
     * Gets the proposition definitions that are associated with the given AND
     * clause of term subsumptions. A proposition matches if and only if at
     * least one term in every subsumption is related to that proposition.
     * 
     * @param termSubsumptions
     *            the term subsumptions to match in the knowledge source
     * 
     * @return a {@link List} of proposition definitions associated with the
     *         given term IDs
     * @throws KnowledgeSourceReadException
     *             if there is a problem reading from the knowledge source
     */
    List<String> getPropositionsByTermSubsumption(
            And<TermSubsumption> termSubsumptions)
            throws KnowledgeSourceReadException;

    /**
     * Gets the proposition definitions for a given term.
     * 
     * @param termId
     *            the term ID to look up
     * @return a {@link List} of proposoition IDs related to the given term
     * @throws KnowledgeSourceReadException
     *             if there is a problem reading from the Knowledge source
     */
    String[] getPropositionsByTerm(String termId)
            throws KnowledgeSourceReadException;

    /**
     * Reads the value set from the knowledge base and returns it. This only
     * will get called if the value set has not already been loaded.
     * 
     * @param id
     *            The id of the value set to read
     * @return The ValueSet object
     */
    ValueSet readValueSet(String id)
            throws KnowledgeSourceReadException;
}
