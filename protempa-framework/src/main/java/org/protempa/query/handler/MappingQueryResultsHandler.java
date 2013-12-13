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

    private MappingUsingKnowledgeSource usingKnowledgeSource;

    public class MappingUsingKnowledgeSource extends AbstractUsingKnowledgeSource {

        private MappingForQuery forQuery;

        public class MappingForQuery extends AbstractForQuery {

            private final Map<String, List<Proposition>> resultMap;

            /**
             * Initializes the map returned by {@link #getResultMap()}.
             */
            private MappingForQuery(Query query) {
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
            public void handleQueryResult(String keyId, 
                    List<Proposition> propositions, 
                    Map<Proposition, List<Proposition>> forwardDerivations, 
                    Map<Proposition, List<Proposition>> backwardDerivations, 
                    Map<UniqueId, Proposition> references) 
                    throws QueryResultsHandlerProcessingException {
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

        private MappingUsingKnowledgeSource(KnowledgeSource knowledgeSource) {

        }
        
        @Override
        public MappingForQuery forQuery(Query query) throws QueryResultsHandlerInitException {
            this.forQuery = new MappingForQuery(query);
            return this.forQuery;
        }

    }

    @Override
    public Statistics collectStatistics() {
        DefaultStatisticsBuilder builder = new DefaultStatisticsBuilder();
        if (this.usingKnowledgeSource != null) {
            if (this.usingKnowledgeSource.forQuery != null) {
                builder.setNumberOfKeys(
                        this.usingKnowledgeSource.forQuery.resultMap.size());
            }
        }
        return builder.toDefaultStatistics();
    }

    @Override
    public MappingUsingKnowledgeSource usingKnowledgeSource(KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException {
        this.usingKnowledgeSource = new MappingUsingKnowledgeSource(knowledgeSource);
        return this.usingKnowledgeSource;
    }
}
