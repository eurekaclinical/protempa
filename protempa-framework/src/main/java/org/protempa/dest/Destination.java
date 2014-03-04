package org.protempa.dest;

import org.protempa.KnowledgeSource;
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
    QueryResultsHandler getQueryResultsHandler(Query query, KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException;
    
    /**
     * Returns an instance that can be used to discover information about 
     * previous runs of data.
     * 
     * @return collects and returns statistics, or <code>null</code> if statistics
     * collection is not supported by this kind of query results handler.
     * @throws CollectStatisticsException if an error occurred.
     */
    Statistics getStatistics() throws StatisticsException;
}