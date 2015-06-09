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
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
class RetrieveAndStoreDataExecutor extends Executor {
    private final String persistentStoreEnvironment;

    RetrieveAndStoreDataExecutor(Query query, QuerySession querySession, AbstractionFinder abstractionFinder, String persistentStoreEnvironment) throws ExecutorInitException {
        super(query, querySession, abstractionFinder);
        this.persistentStoreEnvironment = persistentStoreEnvironment;
    }

    @Override
    protected void doExecute(Set<String> keyIds, final DerivationsBuilder derivationsBuilder, final ExecutionStrategy executionStrategy) throws ExecutorExecuteException {
        final DataStore<String, List<Proposition>> store = new PropositionStoreCreator(persistentStoreEnvironment).getPersistentStore();
        try {
            DataStreamingEventProcessor processor = new DataStreamingEventProcessor(newDataIterator()) {
                @Override
                void doProcess(DataStreamingEvent next, Set<String> propIds) throws ExecutorExecuteException {
                    store.put(next.getKeyId(), next.getData());
                }
            };
            processor.process();
            if (isLoggable(Level.INFO)) {
                log(Level.INFO, "Wrote {0} records into store {1} for query {2}", new Object[]{processor.getCount(), persistentStoreEnvironment, getQuery().getId()});
            }
        } finally {
            store.shutdown();
        }
    }
    
}
