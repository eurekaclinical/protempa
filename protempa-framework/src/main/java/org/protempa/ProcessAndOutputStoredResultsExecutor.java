package org.protempa;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.arp.javautil.datastore.DataStore;
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.dest.Destination;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
class ProcessAndOutputStoredResultsExecutor extends ExecutorWithResultsHandler {
    private final String propositionStoreEnvironment;

    ProcessAndOutputStoredResultsExecutor(Query query, Destination resultsHandlerFactory, QuerySession querySession, AbstractionFinder abstractionFinder, String propositionStoreEnvironment) throws FinderException {
        super(query, resultsHandlerFactory, querySession, ExecutorStrategy.STATELESS, abstractionFinder);
        this.propositionStoreEnvironment = propositionStoreEnvironment;
    }

    @Override
    protected void doExecute(Set<String> keyIds, final DerivationsBuilder derivationsBuilder, final ExecutionStrategy strategy) throws ProtempaException {
        final DataStore<String, List<Proposition>> propStore = new PropositionStoreCreator(propositionStoreEnvironment).getPersistentStore();
        try {
            new KeyIdProcessor(keysToProcess(keyIds, propStore)) {
                @Override
                void doProcess(String keyId, Set<String> propIds) throws FinderException {
                    if (propStore.containsKey(keyId)) {
                        Iterator<Proposition> propositions = strategy.execute(keyId, propIds, propStore.get(keyId), null);
                        processResults(propositions, keyId);
                        derivationsBuilder.reset();
                    }
                }
            }.process();
        } finally {
            propStore.shutdown();
        }
    }
    
}
