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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;

/**
 * An implementation of QueryResultsHandler that stores the results in a map
 * that may be accessed later for post-processing. As each call to
 * handleQueryResult is made, each key-proposition list argument pair is added
 * to the map.
 * 
 * @author Michel Mansour
 *
 */
public class MappingQueryResultsHandler extends AbstractQueryResultsHandler {

    private Map<String, List<Proposition>> resultMap;

//	private QueryResults queryResults;

    /**
     * Gets the map of query results that have been handled.
     * 
     * @return the resultMap
     */
    public final Map<String, List<Proposition>> getResultMap() {
        return Collections.unmodifiableMap(resultMap);
    }

    /**
     * @return the queryResults
     */
//	public QueryResults getQueryResults() {
//		if (queryResults == null) {
//			return new QueryResults(resultMap);
//		}
//		return queryResults;
//	}

    /**
     * Initializes the map returned by {@link #getResultMap()}.
     */
    @Override
    public void init(KnowledgeSource knowledgeSource, Query query) {
        this.resultMap = new HashMap<>();
    }

    /**
     * Puts handled keys and propositions into the map returned by
     * {@link #getResultMap()}.
     *
     * @param key a key id {@link String}.
     * @param propositions a {@link List<Proposition>} of propositions.
     */
    @Override
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition,List<Proposition>> forwardDerivations,
            Map<Proposition,List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references) {
        resultMap.put(key, propositions);
    }
}
