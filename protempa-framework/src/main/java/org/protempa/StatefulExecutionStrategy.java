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
package org.protempa;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.StatefulSession;

import org.eurekaclinical.datastore.DataStore;
import org.protempa.datastore.WorkingMemoryDataStores;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.QueryMode;

class StatefulExecutionStrategy extends AbstractExecutionStrategy {
    
    private final File databasePath;
    private DataStore<String, StatefulSession> dataStore;
    private WorkingMemoryDataStores workingMemoryStoreCreator;
    private StatefulSession workingMemory;
    private final QueryMode queryMode;

    StatefulExecutionStrategy(AlgorithmSource algorithmSource, Query query) {
        super(algorithmSource);
        assert query != null : "query cannot be null";
        String dbPath = query.getDatabasePath();
        assert dbPath != null : "query.getDatabasePath() cannot return a null value";
        this.databasePath = new File(dbPath);
        this.queryMode = query.getQueryMode();
    }
    
    @Override
    public void initialize(Collection<PropositionDefinition> allNarrowerDescendants,
            DerivationsBuilder listener) throws ExecutionStrategyInitializationException {
        super.initialize(allNarrowerDescendants, listener);
        Logger logger = ProtempaUtil.logger();
        this.workingMemoryStoreCreator = new WorkingMemoryDataStores(getRuleBase(), this.databasePath.getParent());
        try {
            this.dataStore = this.workingMemoryStoreCreator.getDataStore(this.databasePath.getName());
            logger.log(Level.FINE, "Opened data store {}", this.databasePath.getPath());
            if (this.queryMode == QueryMode.REPLACE) {
                this.dataStore.clear();
                logger.log(Level.FINE, "Cleared data store {}", this.databasePath.getPath());
            }
        } catch (IOException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
    }

    @Override
    public Iterator<Proposition> execute(String keyId,
            Set<String> propositionIds, List<?> objects) {
        Logger logger = ProtempaUtil.logger();
        this.workingMemory = this.dataStore.get(keyId);
        if (this.workingMemory == null) {
            this.workingMemory = getRuleBase().newStatefulSession(false);
        }
        this.workingMemory.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
        for (Object obj : objects) {
            this.workingMemory.insert(obj);
        }
        this.workingMemory.fireAllRules();
        logger.log(Level.FINEST,
                "Persisting working memory for key ID {0}", keyId);
        this.dataStore.put(keyId, this.workingMemory);
        logger.log(Level.FINEST,
                "Persisted working memory for key ID {0}", keyId);
        return (Iterator<Proposition>) this.workingMemory.iterateObjects();
    }
    
    @Override
    public void closeCurrentWorkingMemory() {
        this.workingMemory.dispose();
    }

    @Override
    public void shutdown() throws ExecutionStrategyShutdownException {
        ExecutionStrategyShutdownException exception = null;
        try {
            dataStore.close();
        } catch (IOError err) {
            exception = new ExecutionStrategyShutdownException(err);
        }
        try {
            this.workingMemoryStoreCreator.close();
        } catch (IOException ex) {
            if (exception != null) {
                exception.addSuppressed(new ExecutionStrategyShutdownException(ex));
            } else {
                exception = new ExecutionStrategyShutdownException(ex);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
}
