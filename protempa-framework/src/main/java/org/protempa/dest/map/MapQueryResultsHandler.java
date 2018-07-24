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
package org.protempa.dest.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.dest.AbstractQueryResultsHandler;
import org.protempa.dest.DefaultStatisticsBuilder;
import org.protempa.dest.QueryResultsHandlerProcessingException;
import org.protempa.dest.Statistics;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 * An implementation of QueryResultsHandler that stores the results in a map
 * that may be accessed later for post-processing. As each call to
 * handleQueryResult is made, each key-proposition list argument pair is added
 * to the map.
 *
 * @author Michel Mansour
 *
 */
public final class MapQueryResultsHandler extends AbstractQueryResultsHandler {

    final Map<String, List<Proposition>> resultMap;

    MapQueryResultsHandler() {
        this.resultMap = new HashMap<>();
    }

    public Statistics collectStatistics() {
        DefaultStatisticsBuilder builder = new DefaultStatisticsBuilder();
        builder.setNumberOfKeys(this.resultMap.size());
        return builder.toDefaultStatistics();
    }
    
    

    /**
     * Puts handled keys and propositions into the map returned by
     * {@link #getResultMap()}.
     *
     * @param keyId a key id {@link String}.
     * @param propositions a {@link List<Proposition>} of propositions.
     */
    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions, 
            Map<Proposition, Set<Proposition>> forwardDerivations, 
            Map<Proposition, Set<Proposition>> backwardDerivations, 
            Map<UniqueId, Proposition> references) throws QueryResultsHandlerProcessingException {
        resultMap.put(keyId, propositions);
    }

    /**
     * Gets the map of query results that have been handled.
     *
     * @return the resultMap
     */
    public final Map<String, List<Proposition>> getResultMap() {
        return Collections.unmodifiableMap(resultMap);
    }

}
