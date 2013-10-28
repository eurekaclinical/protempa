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
package org.protempa.query.handler;

import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;

/**
 * Interface defining the operations for handling a single result from a
 * Protempa query.
 * 
 * @author Michel Mansour
 * 
 */
public interface QueryResultsHandler {

    /**
     * Performs all initialization functions to prepare the handler. This 
     * method is called by Protempa before {@link #start() }.
     *
     * @throws QueryResultsHandlerInitException
     *             if any exceptions occur during initialization.
     */
    public void init(KnowledgeSource knowledgeSource, Query query) 
            throws QueryResultsHandlerInitException;
    
    /**
     * Called by Protempa prior to the first invocation of 
     * {@link #handleQueryResult}. Implementers of this method may perform 
     * arbitrary processing related to the output of the handler, such as
     * printing out headers of a file or extracting metadata from the knowledge
     * source for the handler's output.
     * 
     * @throws QueryResultsHandlerProcessingException if any exceptions occur 
     * at a lower level.
     */
    public void start() throws QueryResultsHandlerProcessingException;

    /**
     * Called by Protempa as soon as all query results have
     * been retrieved from the data source.
     *
     * @throws QueryResultsHandlerProcessingException
     *             if any exceptions occur at a lower level
     */
    public void finish() throws QueryResultsHandlerProcessingException;

    /**
     * Called by Protempa after {@link #finish()} to clean up any resources
     * used by the handler.
     *
     * @throws QueryResultsHandlerCloseException if any exceptions occur at a lower level
     */
    public void close() throws QueryResultsHandlerCloseException;

    /**
     * Handles a single query result, which is the list of propositions
     * associated with the given key.
     *
     * @param keyId
     *            the identifying key id for the result
     * @param propositions
     *            the proposition results for the given key as a newly created
     *            {@link List<Proposition>}.
     * @param derivationsList a mapping from propositions to derived
     * abstractions.
     * and propositions.
     */
    public void handleQueryResult(String keyId,
            List<Proposition> propositions, 
            Map<Proposition,List<Proposition>> forwardDerivations,
            Map<Proposition,List<Proposition>> backwardDerivations,
            Map<UniqueId,Proposition> references)
            throws QueryResultsHandlerProcessingException;
    
    /**
     * Validates this query results handler's specification against the
     * knowledge source. It is called by the abstraction finder after query
     * results handler initialization.
     * 
     * @throws QueryResultsHandlerValidationFailedException if validation
     * failed.
     * @throws KnowledgeSourceReadException if the knowledge source could
     * not be read.
     */
    public void validate() 
            throws QueryResultsHandlerValidationFailedException,
            KnowledgeSourceReadException;
    
    /**
     * Infers from the query results handler's specification what propositions
     * need to be queried in order to populate the query result handler's 
     * output.
     * 
     * When executing a processing job, Protempa takes the union of
     * the proposition ids returned from this API and the proposition ids
     * specified in the Protempa {@link Query} when determining what
     * propositions to retrieve from the underlying data sources and what
     * propositions to compute.
     * 
     * Implementations of {@link QueryResultsHandler} for which such inference
     * does not make sense may return an empty array.
     * 
     * @return an array of proposition id {@link String}. Guaranteed not
     * <code>null</code>.
     * 
     * @throws KnowledgeSourceReadException if reading from the knowledge 
     * source fails while inferring the proposition ids needed.
     */
    public String[] getPropositionIdsNeeded() 
            throws KnowledgeSourceReadException;
}
