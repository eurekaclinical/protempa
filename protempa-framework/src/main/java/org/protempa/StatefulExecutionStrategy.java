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

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.arp.javautil.arrays.Arrays;
import org.drools.FactException;
import org.drools.StatefulSession;

import org.eurekaclinical.datastore.DataStore;
import org.protempa.datastore.WorkingMemoryDataStores;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.QueryMode;

class StatefulExecutionStrategy extends AbstractExecutionStrategy {

    private static final Logger LOGGER = Logger.getLogger(StatefulExecutionStrategy.class.getName());

    private final Path databasePath;
    private DataStore<String, WorkingMemoryFactStore> dataStore;
    private WorkingMemoryDataStores workingMemoryDataStores;
    private StatefulSession workingMemory;
    private final DeletedWorkingMemoryEventListener workingMemoryEventListener;
    private List<Proposition> propsToDelete;
    private final String databaseName;
    private final Path databaseDir;

    StatefulExecutionStrategy(AlgorithmSource algorithmSource, Query query) {
        super(algorithmSource, query);
        assert query != null : "query cannot be null";
        String dbPath = query.getDatabasePath();
        assert dbPath != null : "query.getDatabasePath() cannot return a null value";
        this.databasePath = Paths.get(dbPath);
        this.databaseDir = this.databasePath.getParent();
        this.databaseName = this.databasePath.getFileName().toString();
        this.workingMemoryEventListener = new DeletedWorkingMemoryEventListener();
    }

    @Override
    public void initialize(PropositionDefinitionCache cache) throws ExecutionStrategyInitializationException {
        createDataStoreManager(cache);
        super.initialize(cache);
        getOrCreateDataStore();
    }

    @Override
    public Iterator<Proposition> execute(String keyId, Iterator<? extends Proposition> objects) {
        getOrCreateWorkingMemoryInstance(keyId);
        updateWorkingMemory(keyId, objects);
        fireAllRules();
        cleanupAndPersistWorkingMemory(keyId);
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

    public DataStore<String, WorkingMemoryFactStore> getDataStore() {
        return this.dataStore;
    }

    @Override
    protected JBossRuleCreator newRuleCreator() throws ExecutionStrategyInitializationException {
        ValidateAlgorithmCheckedVisitor visitor
                = new ValidateAlgorithmCheckedVisitor(getAlgorithmSource());
        PropositionDefinitionCache cache;
        Collection<PropositionDefinition> propDefs; //same as cache.getAll()
        Query query = getQuery();
        switch (query.getQueryMode()) {
            case REPROCESS_RETRIEVE:
            case REPROCESS_DELETE:
                cache = new PropositionDefinitionCache(Collections.emptyList());
                propDefs = cache.getAll();
                break;
            case REPROCESS_UPDATE:
                cache = this.workingMemoryDataStores.getPropositionDefinitionsInStores();
                String[] queryPropIds = getQuery().getPropositionIds();
                Set<String> propIdsUpdate = cache.getAll().stream()
                        .map(elt -> elt.getPropositionId())
                        .filter(propId -> !Arrays.contains(queryPropIds, propId))
                        .collect(Collectors.toSet());
                Queue<String> propIdsQueue = new LinkedList<>();
                Arrays.addAll(propIdsQueue, queryPropIds);
                String pId;
                Collection<PropositionDefinition> pDefsFromCache = cache.getAll();
                while ((pId = propIdsQueue.poll()) != null) {
                    for (PropositionDefinition propDef : pDefsFromCache) {
                        String[] children = propDef.getChildren();
                        if (Arrays.contains(children, pId)) {
                            propIdsQueue.add(propDef.getId());
                            propIdsUpdate.remove(pId);
                        }
                    }
                }
                propDefs = pDefsFromCache.stream()
                        .filter(propDef -> propDef != null
                        && !propIdsUpdate.contains(propDef.getPropositionId()))
                        .collect(Collectors.toList());
                cache = new PropositionDefinitionCache(propDefs);
                break;
            case REPROCESS_CREATE:
                cache = getCache();
                propDefs = new ArrayList<>(cache.getAll());
                PropositionDefinitionCache dataStorePropDefs = 
                        this.workingMemoryDataStores.getPropositionDefinitionsInStores();
                Set<String> propIdsInStore = dataStorePropDefs.getAll().stream()
                        .map(elt -> elt.getPropositionId())
                        .collect(Collectors.toSet());
                for (String propId : query.getPropositionIds()) {
                    if (propIdsInStore.contains(propId)) {
                        propIdsInStore.remove(propId);
                        dataStorePropDefs.remove(propId);
                        throw new ExecutionStrategyInitializationException(
                                "Proposition id "
                                + propId
                                + " already exists; if you want to update it, use the update query mode");
                    }
                }
                for (Iterator<PropositionDefinition> itr = propDefs.iterator(); itr.hasNext();) {
                    PropositionDefinition propDef = itr.next();
                    if (propDef == null || propIdsInStore.contains(propDef.getPropositionId())) {
                        itr.remove();
                    }
                }
                cache = new PropositionDefinitionCache(propDefs);
                break;
            case REPLACE:
            case UPDATE:
                cache = getCache();
                propDefs = cache.getAll();
                break;
            default:
                throw new AssertionError("Unexpected query mode " + query.getQueryMode());
        }
        try {
            visitor.visit(propDefs);
        } catch (ProtempaException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
        JBossRuleCreator ruleCreator = new JBossRuleCreator(
                visitor.getAlgorithms(), getDerivationsBuilder(),
                cache);
        try {
            ruleCreator.visit(propDefs);
        } catch (ProtempaException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
        return ruleCreator;
    }

    private void createDataStoreManager(PropositionDefinitionCache cache) throws ExecutionStrategyInitializationException {
        try {
            this.workingMemoryDataStores
                    = new WorkingMemoryDataStores(this.databaseDir, this.databaseName, cache);
        } catch (IOException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
    }

    private void getOrCreateDataStore() throws ExecutionStrategyInitializationException {
        try {
            this.dataStore = this.workingMemoryDataStores.getDataStore();
            LOGGER.log(Level.FINE, "Opened data store {0}", this.databasePath.toString());
        } catch (IOException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }

        if (getQuery().getQueryMode() == QueryMode.REPLACE) {
            this.dataStore.clear();
            LOGGER.log(Level.FINE, "Cleared data store {0}", this.databasePath.toString());
        }
    }

    private void getOrCreateWorkingMemoryInstance(String keyId) {
        createWorkingMemory(keyId);
        this.workingMemory.addEventListener(
                this.workingMemoryEventListener);
    }

    private void createWorkingMemory(String keyId) {
        this.workingMemory = getRuleBase().newStatefulSession(true);
        this.workingMemory.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
    }

    private void updateWorkingMemory(String keyId, Iterator<?> objects)
            throws FactException {
        if (objects != null) {
            while (objects.hasNext()) {
                this.workingMemory.insert((Proposition) objects.next());
            }
        }
        if (this.dataStore != null) {
            WorkingMemoryFactStore factStore = this.dataStore.get(keyId);
            QueryMode queryMode = getQuery().getQueryMode();
            switch (queryMode) {
                case REPROCESS_UPDATE:
                case REPROCESS_DELETE:
                    factStore.removeAll(getQuery().getPropositionIds());
                case REPROCESS_RETRIEVE:
                case REPROCESS_CREATE:
                    getDerivationsBuilder().reset(
                            factStore.getForwardDerivations(),
                            factStore.getBackwardDerivations());
                    for (Proposition prop : factStore.getPropositions()) {
                        this.workingMemory.insert(prop);
                    }
                    break;
                case REPLACE:
                case UPDATE:
                    break;
                default:
                    throw new AssertionError("Unexpected query mode " + queryMode);
            }
        }
    }

    private void fireAllRules() throws FactException {
        this.workingMemory.fireAllRules();
        this.propsToDelete = this.workingMemoryEventListener.getPropsToDelete();
    }

    private void cleanupAndPersistWorkingMemory(String keyId) {
        this.workingMemory.removeEventListener(this.workingMemoryEventListener);
        this.workingMemoryEventListener.clear();
        LOGGER.log(Level.FINEST,
                "Persisting working memory for key ID {0}", keyId);
        WorkingMemoryFactStore factStore = new WorkingMemoryFactStore();
        factStore.setForwardDerivations(getDerivationsBuilder().getForwardDerivations());
        factStore.setBackwardDerivations(getDerivationsBuilder().getBackwardDerivations());
        List<Proposition> facts = new ArrayList<>();
        Iterator factItr = this.workingMemory.iterateObjects();
        while (factItr.hasNext()) {
            facts.add((Proposition) factItr.next());
        }
        factStore.setPropositions(facts);
        this.dataStore.put(keyId, factStore);
        LOGGER.log(Level.FINEST,
                "Persisted working memory for key ID {0}", keyId);
    }

    private Iterator<Proposition> getWorkingMemoryIterator() {
        return (Iterator<Proposition>) new IteratorChain(
                this.workingMemory.iterateObjects(),
                this.propsToDelete.iterator());
    }

    private ExecutionStrategyShutdownException closeDataStore() {
        if (this.dataStore != null) {
            try {
                this.dataStore.close();
            } catch (IOError err) {
                return new ExecutionStrategyShutdownException(err);
            }
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
