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
package org.protempa.query.handler;

import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 * Interface defining the operations for handling a single result from a
 * Protempa query.
 * 
 * @author Michel Mansour
 * 
 */
public interface QueryResultsHandler {

    /**
     * Performs all initialization functions to prepare the handler. This method
     * is guaranteed to be called by Protempa before any query result processing
     * is done.
     *
     * @throws FinderException
     *             if any exceptions occur at a lower level
     */
    public void init(KnowledgeSource knowledgeSource) throws FinderException;

    /**
     * Called by Protempa as soon as all query results have
     * been retrieved from the data source.
     *
     * @throws FinderException
     *             if any exceptions occur at a lower level
     */
    public void finish() throws FinderException;

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
            throws FinderException;
    
    /**
     * Validates this query results handler's specification against the
     * knowledge source. Probably will get replaced by a builder pattern like
     * queries in the future.
     * 
     * @param knowledgeSource a {@link KnowledgeSource}. Guaranteed not
     * <code>null</code>.
     * 
     * @throws QueryResultsHandlerValidationFailedException if validation
     * failed.
     * @throws KnowledgeSourceReadException if the knowledge source could
     * not be read.
     */
    public void validate(KnowledgeSource knowledgeSource) 
            throws QueryResultsHandlerValidationFailedException,
            KnowledgeSourceReadException;
}
