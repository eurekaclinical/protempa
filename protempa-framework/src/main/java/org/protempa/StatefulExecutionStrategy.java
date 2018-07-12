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
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.StatefulSession;

import org.eurekaclinical.datastore.DataStore;
import org.protempa.datastore.WorkingMemoryDataStores;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.QueryMode;

class StatefulExecutionStrategy extends AbstractExecutionStrategy {

    private final File databasePath;
    private DataStore<String, StatefulSession> dataStore;
    private WorkingMemoryDataStores workingMemoryDataStores;
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
        createDataStoreManager();
        try {
            getDataStore();
            prepareDataStore();
        } catch (IOException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
    }

    @Override
    public Iterator<Proposition> execute(String keyId, List<?> objects) {
        getOrCreateWorkingMemoryInstance(keyId);
        insertRetrievedDataIntoWorkingMemory(objects);
        fireAllRules();
        persistWorkingMemory(keyId);
        return getWorkingMemoryIterator();
    }

    @Override
    public void closeCurrentWorkingMemory() {
        this.workingMemory.dispose();
    }

    @Override
    public void shutdown() throws ExecutionStrategyShutdownException {
        ExecutionStrategyShutdownException exception1 = closeDataStore();
        ExecutionStrategyShutdownException exception2 = closeDataStoreManager();
        if (exception1 != null && exception2 != null) {
            exception1.addSuppressed(exception2);
            throw exception1;
        } else if (exception1 != null) {
            throw exception1;
        } else if (exception2 != null) {
            throw exception2;
        }
    }

    private void createDataStoreManager() {
        this.workingMemoryDataStores = new WorkingMemoryDataStores(getRuleBase(), this.databasePath.getParent());
    }

    private void getDataStore() throws IOException {
        Logger logger = ProtempaUtil.logger();
        this.dataStore = this.workingMemoryDataStores.getDataStore(this.databasePath.getName());
        logger.log(Level.FINE, "Opened data store {0}", this.databasePath.getPath());
    }

    private void prepareDataStore() {
        Logger logger = ProtempaUtil.logger();
        if (this.queryMode == QueryMode.REPLACE) {
            this.dataStore.clear();
            logger.log(Level.FINE, "Cleared data store {0}", this.databasePath.getPath());
        }
    }

    private void getOrCreateWorkingMemoryInstance(String keyId) {
        this.workingMemory = this.dataStore.get(keyId);
        if (this.workingMemory == null) {
            this.workingMemory = getRuleBase().newStatefulSession(false);
        }
        this.workingMemory.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
    }

    private void insertRetrievedDataIntoWorkingMemory(List<?> objects) throws FactException {
        for (Object obj : objects) {
            FactHandle factHandle = this.workingMemory.getFactHandle(obj);
            if (factHandle != null) {
                this.workingMemory.update(factHandle, obj);
            } else {
                this.workingMemory.insert(obj);
            }
        }
    }

    private void fireAllRules() throws FactException {
        this.workingMemory.fireAllRules();
    }

    private void persistWorkingMemory(String keyId) {
        Logger logger = ProtempaUtil.logger();
        logger.log(Level.FINEST,
                "Persisting working memory for key ID {0}", keyId);
        this.dataStore.put(keyId, this.workingMemory);
        logger.log(Level.FINEST,
                "Persisted working memory for key ID {0}", keyId);
    }

    private Iterator<Proposition> getWorkingMemoryIterator() {
        return (Iterator<Proposition>) this.workingMemory.iterateObjects();
    }

    private ExecutionStrategyShutdownException closeDataStore() {
        try {
            dataStore.close();
        } catch (IOError err) {
            return new ExecutionStrategyShutdownException(err);
        }
        return null;
    }

    private ExecutionStrategyShutdownException closeDataStoreManager() {
        try {
            this.workingMemoryDataStores.close();
        } catch (IOException ex) {
            return new ExecutionStrategyShutdownException(ex);
        }
        return null;
    }
}
