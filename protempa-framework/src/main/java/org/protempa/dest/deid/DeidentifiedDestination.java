package org.protempa.dest.deid;

import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.dest.AbstractDestination;
import org.protempa.dest.Destination;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerInitException;
import org.protempa.dest.Statistics;
import org.protempa.dest.StatisticsException;
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
public final class DeidentifiedDestination extends AbstractDestination {
    private final Destination destination;
    
    public DeidentifiedDestination(Destination destination) {
        this.destination = destination;
    }

    @Override
    public QueryResultsHandler getQueryResultsHandler(Query query, DataSource dataSource, KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException {
       return new DeidentifiedQueryResultsHandler(this.destination.getQueryResultsHandler(query, dataSource, knowledgeSource));
    }

    @Override
    public boolean isGetStatisticsSupported() {
        return this.destination.isGetStatisticsSupported();
    }

    @Override
    public Statistics getStatistics() throws StatisticsException {
        return this.destination.getStatistics();
    }
    
}
