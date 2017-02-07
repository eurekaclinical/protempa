package org.protempa.dest.proplist;

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

import java.io.BufferedWriter;
import java.util.List;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.ProtempaEventListener;
import org.protempa.dest.AbstractDestination;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerInitException;
import org.protempa.query.QueryMode;
import org.protempa.query.Query;

/**
 * Destination containing one row per proposition, with delimited columns
 * containing attributes of each proposition.
 * 
 * @author Andrew Post
 */
public final class PropositionListDestination extends AbstractDestination {
    private final BufferedWriter writer;
    
    PropositionListDestination(BufferedWriter writer) {
        this.writer = writer;
    }

    @Override
    public QueryResultsHandler getQueryResultsHandler(Query query, DataSource dataSource, KnowledgeSource knowledgeSource, List<ProtempaEventListener> eventListeners) throws QueryResultsHandlerInitException {
        if (query.getQueryMode() == QueryMode.UPDATE) {
            throw new QueryResultsHandlerInitException("Update mode not supported");
        }
        return new PropositionListQueryResultsHandler(this.writer);
    }

}
