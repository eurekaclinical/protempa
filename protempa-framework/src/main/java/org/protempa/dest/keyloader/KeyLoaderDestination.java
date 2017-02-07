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

import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.ProtempaEventListener;
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
    private final String id;
    private final String displayName;
    
    public KeyLoaderDestination(Criteria criteria) {
        this(null, criteria);
    }

    public KeyLoaderDestination(String id, Criteria criteria) {
        this(id, null, criteria);
    }
    
    public KeyLoaderDestination(String id, String displayName, Criteria criteria) {
        this.criteria = criteria;
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName != null ? this.displayName : super.getDisplayName();
    }

    @Override
    public String getId() {
        return this.id != null ? this.id : super.getId();
    }
    
    @Override
    public QueryResultsHandler getQueryResultsHandler(Query query, DataSource dataSource, KnowledgeSource knowledgeSource, List<? extends ProtempaEventListener> eventListeners) throws QueryResultsHandlerInitException {
        if (query.getQueryMode() == QueryMode.UPDATE) {
            throw new QueryResultsHandlerInitException("Update mode not supported");
        }
        return new KeyLoaderQueryResultsHandler(dataSource, knowledgeSource, this.criteria, this.id, this.displayName);
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
