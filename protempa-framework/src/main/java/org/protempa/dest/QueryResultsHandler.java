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


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.PropositionDefinition;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 * Interface defining the operations for handling results of a Protempa query.
 *
 * Protempa calls the methods of a provided implementation of this interface in
 * the following order:
 * <ol>
 * <li>{@link #start() }
 * <li>{@link #handleQueryResult(java.lang.String, java.util.List, java.util.Map, java.util.Map, java.util.Map)
 * } called once per key
 * <li>{@link #finish() }
 * <li>{@link #close() }
 * </ol>
 * 
 * If an exception is thrown by {@link #start() } or {@link #handleQueryResult(java.lang.String, java.util.List, java.util.Map, java.util.Map, java.util.Map) },
 * {@link #finish() } will be skipped.
 * 
 * If {@link #cancel() } is called before {@link #finish() } is called, the
 * latter will be skipped.
 * 
 * The {@link #cancel() } method may be called in parallel
 * with execution of any of the other methods in the interface. The 
 * different methods of this interface may be called by different (albeit 
 * synchronized) threads.
 *
 * @author Michel Mansour
 *
 */
public interface QueryResultsHandler extends AutoCloseable {
    
    /**
     * Gets the query result handler's identifier string for logging and
     * internal purposes.
     * 
     * @return a string.
     */
    String getId();
    
    /**
     * Gets the query result handler's display name for user interfaces.
     * 
     * @return a string.
     */
    String getDisplayName();
    
    /**
     * Validates this query results handler's specification.
     *
     * @throws QueryResultsHandlerValidationFailedException if validation
     * failed.
     */
    void validate()
            throws QueryResultsHandlerValidationFailedException;
    
    /**
     * Called by Protempa prior to the first invocation of
     * {@link #handleQueryResult(java.lang.String, java.util.List, java.util.Map, java.util.Map, java.util.Map) }. 
     * Implementers of this method may perform arbitrary processing related to 
     * the output of the handler, such as printing out headers of a file or 
     * extracting metadata for the handler's output.
     *
     * @param cache a cache of all of the proposition definitions that were
     * queried.
     * @throws QueryResultsHandlerProcessingException if any exceptions occur at
     * a lower level.
     */
    void start(Collection<PropositionDefinition> cache) throws QueryResultsHandlerProcessingException;

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
            Map<Proposition, Set<Proposition>> forwardDerivations,
            Map<Proposition, Set<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references)
            throws QueryResultsHandlerProcessingException;

    /**
     * Called by Protempa as soon as all query results have been retrieved from
     * the data source. Will not be called if a previous step failed.
     *
     * @throws QueryResultsHandlerProcessingException if any exceptions occur at
     * a lower level
     */
    void finish() throws QueryResultsHandlerProcessingException;

    /**
     * Called by Protempa after {@link #handleFinish()} to clean up any
     * resources used by the handler. It is called always, even if a previous 
     * step failed.
     *
     * @throws QueryResultsHandlerCloseException if any exceptions occur at a
     * lower level
     */
    @Override
    void close() throws QueryResultsHandlerCloseException;
    
    /**
     * Requests cancellation of whatever this object is currently doing. If
     * {@link #finish() } has not been called yet, it will be skipped. If
     * {@link #close() } has not been called yet, it will be called, and it 
     * must finish cleaning up all resources associated with this query
     * results handler.
     */
    void cancel();
}
