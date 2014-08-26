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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.protempa.DataSource;
import org.protempa.DataSourceWriteException;
import org.protempa.KnowledgeSource;
import org.protempa.dest.AbstractQueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerProcessingException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
public class KeyLoaderQueryResultsHandler extends AbstractQueryResultsHandler {

    private final DataSource dataSource;
    private final Criteria criteria;
    private final KnowledgeSource knowledgeSource;
    private final int batchSize = 1000;
    private int i;
    private Set<String> keyIds;

    KeyLoaderQueryResultsHandler(DataSource dataSource, KnowledgeSource knowledgeSource, Criteria criteria) {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        if (knowledgeSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        this.dataSource = dataSource;
        this.knowledgeSource = knowledgeSource;
        this.criteria = criteria;
    }

    @Override
    public void start() throws QueryResultsHandlerProcessingException {
        try {
            this.dataSource.deleteAllKeys();
        } catch (DataSourceWriteException ex) {
            throw new QueryResultsHandlerProcessingException("Could not delete keys", ex);
        }
        try {
            this.criteria.init(this.knowledgeSource);
        } catch (CriteriaInitException ex) {
            throw new QueryResultsHandlerProcessingException("Error setting up query results handler", ex);
        }
        this.i = 0;
        this.keyIds = new HashSet<>();
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions, Map<Proposition, List<Proposition>> forwardDerivations, Map<Proposition, List<Proposition>> backwardDerivations, Map<UniqueId, Proposition> references) throws QueryResultsHandlerProcessingException {
        System.out.println("KeyId: " + keyId + ": " + propositions);
        try {
            if (this.criteria == null || this.criteria.evaluate(propositions)) {
                i++;
                if (this.i % this.batchSize == 0) {
                    System.out.println("Writing keys 1 " + this.keyIds);
                    this.dataSource.writeKeys(this.keyIds);
                    this.keyIds = new HashSet<>();
                } else {
                    System.out.println("Adding keyId " + keyId);
                    this.keyIds.add(keyId);
                }
            }
        } catch (CriteriaEvaluateException | DataSourceWriteException ex) {
            throw new QueryResultsHandlerProcessingException("Error processing query results", ex);
        }
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        System.out.println("Finish writing keys " + this.keyIds);
        if (!this.keyIds.isEmpty()) {
            try {
                this.dataSource.writeKeys(this.keyIds);
            } catch (DataSourceWriteException ex) {
                throw new QueryResultsHandlerProcessingException(ex);
            }
        }
    }
    
    @Override
    public String[] getPropositionIdsNeeded() throws QueryResultsHandlerProcessingException {
        if (this.criteria != null) {
            return this.criteria.getPropositionIdsSpecified();
        } else {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
    }

}
