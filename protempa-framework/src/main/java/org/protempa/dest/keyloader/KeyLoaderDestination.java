package org.protempa.dest.keyloader;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

import org.apache.commons.lang3.ArrayUtils;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.dest.AbstractDestination;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerInitException;
import org.protempa.query.QueryMode;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
public class KeyLoaderDestination extends AbstractDestination {
    private final Criteria criteria;

    public KeyLoaderDestination(Criteria criteria) {
        this.criteria = criteria;
    }
    
    @Override
    public QueryResultsHandler getQueryResultsHandler(Query query, DataSource dataSource, KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException {
        if (query.getQueryMode() == QueryMode.UPDATE) {
            throw new QueryResultsHandlerInitException("Update mode not supported");
        }
        return new KeyLoaderQueryResultsHandler(dataSource, knowledgeSource, this.criteria);
    }

    @Override
    public String[] getSupportedPropositionIds(DataSource dataSource, KnowledgeSource knowledgeSource) {
        if (this.criteria != null) {
            return this.criteria.getPropositionIdsSpecified();
        } else {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
    }

}
