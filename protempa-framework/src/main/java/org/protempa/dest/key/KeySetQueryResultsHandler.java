package org.protempa.dest.key;

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
import org.protempa.DataSource;
import org.protempa.DataSourceWriteException;
import org.protempa.KnowledgeSource;
import org.protempa.PropositionDefinitionCache;
import org.protempa.dest.AbstractQueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerProcessingException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
public class KeySetQueryResultsHandler extends AbstractQueryResultsHandler {

    private final DataSource dataSource;
    private final String id;
    private final String displayName;
    private int i;

    KeySetQueryResultsHandler(DataSource dataSource, KnowledgeSource knowledgeSource, String id, String displayName) {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        if (knowledgeSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        this.dataSource = dataSource;
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
    public void start(PropositionDefinitionCache cache) throws QueryResultsHandlerProcessingException {
    }

    @Override
    public void handleQueryResult(String keyId,
            List<Proposition> propositions,
            Map<Proposition, Set<Proposition>> forwardDerivations,
            Map<Proposition, Set<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references) throws QueryResultsHandlerProcessingException {
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        try {
            this.dataSource.writeKeysFromKeySet(this);
        } catch (DataSourceWriteException ex) {
            throw new QueryResultsHandlerProcessingException(ex);
        }
    }

}
