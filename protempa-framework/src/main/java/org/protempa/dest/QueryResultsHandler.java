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
package org.protempa.dest;


import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;

/**
 * Interface defining the operations for handling results of a Protempa query.
 *
 * Protempa calls the methods of a provided implementation of this interface in
 * the following order:
 * <ol>
 * <li>{@link #configurationInit() }
 * <li>{@link #knowledgeSourceInit(org.protempa.KnowledgeSource) }
 * <li>{@link #handleInit(org.protempa.KnowledgeSource, org.protempa.query.Query)
 * }
 * <
 * li>{@link #handleStart() }
 * <li>{@link #handleQueryResult(java.lang.String, java.util.List, java.util.Map, java.util.Map, java.util.Map)
 * } called once per key
 * <li>{@link #handleFinish() }
 * <li>{@link #close() }
 * </ol>
 *
 * @author Michel Mansour
 *
 */
public interface QueryResultsHandler extends AutoCloseable {

    /**
     * Validates this query results handler's specification against the provided
     * knowledge source. This method cannot be called until after 
     * {@link #knowledgeSourceInit(org.protempa.KnowledgeSource) }
     * is, and it may not be called after {@link #close() }.
     *
     * @throws QueryResultsHandlerValidationFailedException if validation
     * failed.
     * @throws KnowledgeSourceReadException if the knowledge source could not be
     * read.
     */
    void validate()
            throws QueryResultsHandlerValidationFailedException;

    /**
     * Infers from the query results handler's specification what propositions
     * need to be queried in order to populate the query result handler's
     * output.
     *
     * When executing a processing job, Protempa takes the union of the
     * proposition ids returned from this API and the proposition ids specified
     * in the Protempa {@link Query} when determining what propositions to
     * retrieve from the underlying data sources and what propositions to
     * compute.
     *
     * Implementations of {@link QueryResultsHandler} for which such inference
     * does not make sense may return an empty array.
     *
     * This method may not be called until after {@link #handleInit(org.protempa.KnowledgeSource, org.protempa.query.Query)
     * }
     * is, and it may not be called after {@link #close() }.
     *
     * @return an array of proposition id {@link String}. Guaranteed not
     * <code>null</code>.
     *
     */
    String[] getPropositionIdsNeeded() throws QueryResultsHandlerProcessingException;

    /**
     * Called by Protempa prior to the first invocation of
     * {@link #handleQueryResult}. Implementers of this method may perform
     * arbitrary processing related to the output of the handler, such as
     * printing out headers of a file or extracting metadata from the knowledge
     * source for the handler's output.
     *
     * @throws QueryResultsHandlerProcessingException if any exceptions occur at
     * a lower level.
     */
    void start() throws QueryResultsHandlerProcessingException;

    /**
     * Handles a single query result, which is the list of propositions
     * associated with the given key.
     *
     * @param keyId the identifying key id for the result
     * @param propositions the proposition results for the given key as a newly
     * created {@link List<Proposition>}.
     * @param derivationsList a mapping from propositions to derived
     * abstractions. and propositions.
     */
    void handleQueryResult(String keyId,
            List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references)
            throws QueryResultsHandlerProcessingException;

    /**
     * Called by Protempa as soon as all query results have been retrieved from
     * the data source.
     *
     * @throws QueryResultsHandlerProcessingException if any exceptions occur at
     * a lower level
     */
    void finish() throws QueryResultsHandlerProcessingException;

    /**
     * Called by Protempa after {@link #handleFinish()} to clean up any
     * resources used by the handler.
     *
     * @throws QueryResultsHandlerCloseException if any exceptions occur at a
     * lower level
     */
    void close() throws QueryResultsHandlerCloseException;
}
