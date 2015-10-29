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

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.arp.javautil.datastore.DataStore;
import org.drools.WorkingMemory;
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.datastore.WorkingMemoryStoreCreator;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
class ProcessStoredResultsExecutor extends Executor {
    private final String propositionStoreEnvironment;
    private final String workingMemoryStoreEnvironment;

    ProcessStoredResultsExecutor(Query query, QuerySession querySession, AbstractionFinder abstractionFinder, String propositionStoreEnvironment, String workingMemoryStoreEnvironment) throws ExecutorInitException {
        super(query, querySession, ExecutorStrategy.STATEFUL, abstractionFinder);
        this.propositionStoreEnvironment = propositionStoreEnvironment;
        this.workingMemoryStoreEnvironment = workingMemoryStoreEnvironment;
    }

    @Override
    protected void doExecute(Set<String> keyIds, final DerivationsBuilder derivationsBuilder, final ExecutionStrategy strategy) throws ExecutorExecuteException {
        final DataStore<String, List<Proposition>> propStore = new PropositionStoreCreator(propositionStoreEnvironment).getPersistentStore();
        if (isLoggable(Level.INFO)) {
            log(Level.INFO, "Found {0} records in store {1} for query {2}", new Object[]{propStore.size(), propositionStoreEnvironment, getQuery().getName()});
        }
        final DataStore<String, WorkingMemory> wmStore = new WorkingMemoryStoreCreator(null, workingMemoryStoreEnvironment).getPersistentStore();
        final DataStore<String, DerivationsBuilder> dbStore = new DerivationsBuilderStoreCreator(workingMemoryStoreEnvironment).getPersistentStore();
        try {
            new KeyIdProcessor(keysToProcess(keyIds, propStore)) {
                @Override
                void doProcess(String keyId, Set<String> propIds) throws ExecutorExecuteException {
                    // the important part here is that the working memory produced
                    // by the rules engine is being persisted by
                    // StatefulExecutionStrategy.execute()
                    if (propStore.containsKey(keyId)) {
                        strategy.execute(keyId, propIds, propStore.get(keyId), wmStore);
                        dbStore.put(keyId, derivationsBuilder);
                    }
                }
            }.process();
        } finally {
            propStore.shutdown();
            wmStore.shutdown();
            dbStore.shutdown();
        }
    }
    
}
