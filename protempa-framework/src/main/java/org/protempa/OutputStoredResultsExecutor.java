/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Set;
import java.util.logging.Level;
import org.arp.javautil.datastore.DataStore;
import org.drools.WorkingMemory;
import org.protempa.datastore.WorkingMemoryStoreCreator;
import org.protempa.dest.Destination;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

/**
 *
 * @author arpost
 */
class OutputStoredResultsExecutor extends ExecutorWithResultsHandler {
    private final String workingMemoryStoreEnvironment;

    OutputStoredResultsExecutor(Query query, Destination resultsHandlerFactory, QuerySession querySession, AbstractionFinder abstractionFinder, String workingMemoryStoreEnvironment) throws FinderException {
        super(query, resultsHandlerFactory, querySession, ExecutorStrategy.STATEFUL, abstractionFinder);
        this.workingMemoryStoreEnvironment = workingMemoryStoreEnvironment;
    }

    @Override
    protected void doExecute(Set<String> keyIds, final DerivationsBuilder ignored, final ExecutionStrategy strategy) throws ProtempaException {
        final DataStore<String, DerivationsBuilder> dbStore = new DerivationsBuilderStoreCreator(workingMemoryStoreEnvironment).getPersistentStore();
        DataStore<String, WorkingMemory> wmStore = null;
        try {
            wmStore = new WorkingMemoryStoreCreator(strategy.getRuleBase(), workingMemoryStoreEnvironment).getPersistentStore();
            final DataStore<String, WorkingMemory> fwmStore = wmStore;
            new KeyIdProcessor(keysToProcess(keyIds, wmStore)) {
                @Override
                void doProcess(String keyId, Set<String> propIds) throws FinderException {
                    if (isLoggable(Level.FINEST)) {
                        log(Level.FINEST, "Determining output for key {0} for query {1}", new Object[]{keyId, getQuery().getId()});
                    }
                    if (fwmStore.containsKey(keyId)) {
                        WorkingMemory wm = fwmStore.get(keyId);
                        DerivationsBuilder derivationsBuilder = dbStore.get(keyId);
                        @SuppressWarnings(value = "unchecked")
                        Iterator<Proposition> propositions = wm.iterateObjects();
                        processResults(propositions, derivationsBuilder, keyId);
                    }
                }
            }.process();
        } finally {
            dbStore.shutdown();
            wmStore.shutdown();
        }
    }
    
}
