package org.protempa.dest;

import java.util.List;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.ProtempaEventListener;
import org.protempa.query.Query;

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

/**
 *
 * @author Andrew Post
 */
public interface Destination {
    /**
     * Gets the destination's identifier string for logging and internal
     * purposes.
     * 
     * @return a string.
     */
    String getId();
    
    /**
     * Gets the destination's display name for user interfaces.
     * 
     * @return a string.
     */
    String getDisplayName();
    
    /**
     * Performs any initialization required for the query results handler's
     * configuration. After calling this method, calls to 
     * {@link #collectStatistics() } will work. This method is called by
     * Protempa before 
     * {@link #handleKnowledgeSource(org.protempa.KnowledgeSource) }.
     *
     * @return a query results handler.
     * @throws org.protempa.query.handler.QueryResultsHandlerInitException if
     * any exceptions occur. There may be a nested exception with more
     * information.
     */
    QueryResultsHandler getQueryResultsHandler(Query query, DataSource dataSource, KnowledgeSource knowledgeSource, List<ProtempaEventListener> eventListeners) throws QueryResultsHandlerInitException;
    
    boolean isGetStatisticsSupported();
    
    /**
     * Returns an instance that can be used to discover information about 
     * previous runs of data.
     * 
     * @return collects and returns statistics, or <code>null</code> if statistics
     * collection is not supported by this kind of query results handler.
     * @throws CollectStatisticsException if an error occurred.
     */
    Statistics getStatistics() throws StatisticsException;
    
    /**
     * Infers from the query results handler's specification what propositions
     * this query results handler supports in its output.
     *
     * When executing a processing job, Protempa uses the proposition ids thus
     * returned to filter those that are queried. 
     *
     * Implementations of {@link QueryResultsHandler} for which such inference
     * does not make sense may return an empty array, in which case, no
     * filtering occurs.
     *
     * @return an array of proposition id {@link String}. Guaranteed not
     * <code>null</code>.
     * @throws org.protempa.dest.QueryResultsHandlerProcessingException if
     * an error occurs.
     *
     */
    String[] getSupportedPropositionIds(DataSource dataSource, KnowledgeSource knowledgeSource) throws GetSupportedPropositionIdsException;
    
}
