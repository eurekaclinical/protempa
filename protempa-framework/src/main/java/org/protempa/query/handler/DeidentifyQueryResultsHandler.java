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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;

/**
 * Implements de-identification. Only replaces key ids so far.
 *
 * @author Andrew Post
 */
public final class DeidentifyQueryResultsHandler
        implements QueryResultsHandler {

    private static final ThreadLocal<NumberFormat> disguisedKeyFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return NumberFormat.getInstance();
        }
    };

    private DeidentifyUsingKnowledgeSource usingKnowledgeSource;
    private final QueryResultsHandler handler;

    public class DeidentifyUsingKnowledgeSource implements UsingKnowledgeSource {

        private DeidentifyForQuery forQuery;
        private UsingKnowledgeSource usingKnowledgeSource;

        public class DeidentifyForQuery implements ForQuery {

            private final ForQuery forQuery;
            private final Map<String, String> keyMapper;
            private boolean keyIdDisguised;
            private int nextDisguisedKey;

            /**
             * Initializes the map returned by {@link #getResultMap()}.
             */
            private DeidentifyForQuery(ForQuery forQuery) {
                this.forQuery = forQuery;
                this.nextDisguisedKey = 1;
                this.keyIdDisguised = true;
                this.keyMapper = new HashMap<>();
            }

            /**
             * Returns whether key ids will be disguised. Default is
             * <code>true</code>.
             *
             * @return <code>true</code> or <code>false</code>.
             */
            public boolean isKeyIdDisguised() {
                return keyIdDisguised;
            }

            /**
             * Sets whether to disguise key ids.
             *
             * @param keyDisguised <code>true</code> or <code>false</code>.
             */
            public void setKeyIdDisguised(boolean keyDisguised) {
                this.keyIdDisguised = keyDisguised;
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
                keyId = disguiseKeyIds(keyId);
                this.forQuery.handleQueryResult(keyId, propositions, forwardDerivations, backwardDerivations, references);
            }

            /**
             * Delegates to the query results handler passed into the
             * constructor.
             *
             * @return an array of proposition id {@link String}s.
             */
            @Override
            public String[] getPropositionIdsNeeded() throws QueryResultsHandlerProcessingException {
                return this.forQuery.getPropositionIdsNeeded();
            }

            @Override
            public void start() throws QueryResultsHandlerProcessingException {
                this.forQuery.start();
            }

            @Override
            public void finish() throws QueryResultsHandlerProcessingException {
                this.forQuery.finish();
                this.keyMapper.clear();
            }

            @Override
            public void close() throws QueryResultsHandlerCloseException {
                this.forQuery.close();
            }

            private String disguiseKeyIds(String keyId) {
                if (this.keyIdDisguised) {
                    if (this.keyMapper.containsKey(keyId)) {
                        keyId = this.keyMapper.get(keyId);
                    } else {
                        keyId = disguisedKeyFormat.get().format(nextDisguisedKey++);
                    }
                }
                return keyId;
            }

        }

        private DeidentifyUsingKnowledgeSource(UsingKnowledgeSource usingKnowledgeSource) {
            this.usingKnowledgeSource = usingKnowledgeSource;
        }

        /**
         * Delegates to the query results handler passed into the constructor.
         *
         * @throws QueryResultsHandlerValidationFailedException if the query
         * results handler passed into the constructor is invalid.
         */
        @Override
        public void validate() throws QueryResultsHandlerValidationFailedException {
            this.usingKnowledgeSource.validate();
        }

        @Override
        public DeidentifyForQuery forQuery(Query query) throws QueryResultsHandlerInitException {
            this.forQuery = new DeidentifyForQuery(
                    this.usingKnowledgeSource.forQuery(query));
            return this.forQuery;
        }

        @Override
        public void close() throws QueryResultsHandlerCloseException {
            this.usingKnowledgeSource.close();
        }

    }

    DeidentifyQueryResultsHandler(QueryResultsHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null");
        }
        this.handler = handler;
    }

    @Override
    public DeidentifyUsingKnowledgeSource usingKnowledgeSource(KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException {
        return new DeidentifyUsingKnowledgeSource(
                this.handler.usingKnowledgeSource(knowledgeSource));
    }

    @Override
    public Statistics collectStatistics()
            throws QueryResultsHandlerCollectStatisticsException {
        return this.handler.collectStatistics();
    }

    @Override
    public void close() throws QueryResultsHandlerCloseException {
        this.handler.close();
    }
}
