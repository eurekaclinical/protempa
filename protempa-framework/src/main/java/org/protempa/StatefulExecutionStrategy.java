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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Iterators;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.rule.Rule;

import org.eurekaclinical.datastore.DataStore;
import org.protempa.datastore.WorkingMemoryDataStores;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.QueryMode;

class StatefulExecutionStrategy extends AbstractExecutionStrategy {

    private static final Logger LOGGER = Logger.getLogger(StatefulExecutionStrategy.class.getName());

    private final File databasePath;
    private DataStore<String, StatefulSession> dataStore;
    private WorkingMemoryDataStores workingMemoryDataStores;
    private StatefulSession workingMemory;
    private final DeletedWorkingMemoryEventListener workingMemoryEventListener;
    private List<Proposition> propsToDelete;
    private RuleBase ruleBase;

    StatefulExecutionStrategy(AlgorithmSource algorithmSource, Query query) {
        super(algorithmSource, query);
        assert query != null : "query cannot be null";
        String dbPath = query.getDatabasePath();
        assert dbPath != null : "query.getDatabasePath() cannot return a null value";
        this.databasePath = new File(dbPath);
        this.workingMemoryEventListener = new DeletedWorkingMemoryEventListener();
    }

    @Override
    public void initialize(Collection<PropositionDefinition> cache) throws ExecutionStrategyInitializationException {
        super.initialize(cache);
        createDataStoreManager();
        grabDataStore();
        prepareDataStore();
    }

    @Override
    public Iterator<Proposition> execute(String keyId, Iterator<? extends Proposition> objects) {
        getOrCreateWorkingMemoryInstance(keyId);
        insertRetrievedDataIntoWorkingMemory(objects);
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

    public DataStore<String, StatefulSession> getDataStore() {
        return this.dataStore;
    }

    private void createDataStoreManager() throws ExecutionStrategyInitializationException {
        this.workingMemoryDataStores
                = new WorkingMemoryDataStores(this.databasePath.getParent());
    }

    private void grabDataStore() throws ExecutionStrategyInitializationException {
        try {
            String dbName = this.databasePath.getName();
            Query query = getQuery();
            QueryMode queryMode = query.getQueryMode();
            if (this.workingMemoryDataStores.exists(dbName)) {
                this.dataStore = this.workingMemoryDataStores.getDataStore(dbName);
            } else {
                if (!Arrays.contains(QueryMode.etlModes(), queryMode)) {
                    throw new ExecutionStrategyInitializationException("No data store with name " + dbName);
                }
                this.dataStore = this.workingMemoryDataStores.newDataStore(dbName, createRuleBase());
            }
            this.ruleBase = this.workingMemoryDataStores.getRuleBase();
            switch (queryMode) {
                case REPROCESS_RETRIEVE:
                    break;
                case REPROCESS_CREATE:
                    addToRuleBase(this.ruleBase, query.getPropositionDefinitions());
                    break;
                case REPROCESS_UPDATE:
                    updateInRuleBase(this.ruleBase, query.getPropositionDefinitions());
                    break;
                case REPROCESS_DELETE:
                    deleteFromRuleBase(this.ruleBase, query.getPropositionDefinitions());
                    break;
                default:
                    break;
            }
            LOGGER.log(Level.FINE, "Opened data store {0}", this.databasePath.getPath());
        } catch (IOException | ProtempaException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
    }

    private void prepareDataStore() {
        if (getQuery().getQueryMode() == QueryMode.REPLACE) {
            this.dataStore.clear();
            LOGGER.log(Level.FINE, "Cleared data store {0}", this.databasePath.getPath());
        }
    }

    private void getOrCreateWorkingMemoryInstance(String keyId) {
        this.workingMemory = this.dataStore.get(keyId);
        if (this.workingMemory == null) {
            createWorkingMemory(keyId);
        } else {
            DerivationsBuilder derivationsBuilder = getDerivationsBuilder();
            derivationsBuilder.reset(
                    (Map<Proposition, List<Proposition>>) this.workingMemory.getGlobal(WorkingMemoryGlobals.FORWARD_DERIVATIONS),
                    (Map<Proposition, List<Proposition>>) this.workingMemory.getGlobal(WorkingMemoryGlobals.BACKWARD_DERIVATIONS));
        }
        this.workingMemory.addEventListener(this.workingMemoryEventListener);
    }

    private void createWorkingMemory(String keyId) {
        this.workingMemory = this.ruleBase.newStatefulSession(true);
        this.workingMemory.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
        DerivationsBuilder derivationsBuilder = getDerivationsBuilder();
        this.workingMemory.setGlobal(WorkingMemoryGlobals.FORWARD_DERIVATIONS,
                derivationsBuilder.getForwardDerivations());
        this.workingMemory.setGlobal(WorkingMemoryGlobals.BACKWARD_DERIVATIONS,
                derivationsBuilder.getBackwardDerivations());
    }

    private void insertRetrievedDataIntoWorkingMemory(Iterator<?> objects)
            throws FactException {
        if (objects != null) {
            while (objects.hasNext()) {
                Proposition prop = (Proposition) objects.next();
                FactHandle factHandle = this.workingMemory.getFactHandle(prop);
                if (factHandle != null) {
                    this.workingMemory.modifyRetract(factHandle);
                    this.workingMemory.modifyInsert(factHandle, prop);
                } else {
                    this.workingMemory.insert(prop);
                }
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
        this.dataStore.put(keyId, this.workingMemory);
        LOGGER.log(Level.FINEST,
                "Persisted working memory for key ID {0}", keyId);
    }

    private Iterator<Proposition> getWorkingMemoryIterator() {
        return (Iterator<Proposition>) new IteratorChain(
                this.workingMemory.iterateObjects(),
                this.propsToDelete.iterator());
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
        } finally {
            this.ruleBase = null;
        }
        return null;
    }

    private void addToRuleBase(RuleBase ruleBase, PropositionDefinition... propositionDefinitions) throws ProtempaException {
        org.drools.rule.Package pkg = new org.drools.rule.Package(ProtempaUtil.DROOLS_PACKAGE_NAME);
        JBossRuleCreator creator = newRuleCreator(Arrays.asList(propositionDefinitions));
        for (Rule rule : creator.getRules()) {
            pkg.addRule(rule);
        }
        try {
            ruleBase.addPackage(pkg); // Drools merges it.
        } catch (Exception ex) {
            throw new QueryException(getQuery().getName(), ex);
        }
    }

    private void updateInRuleBase(RuleBase ruleBase, PropositionDefinition... propositionDefinitions) throws ProtempaException {
        org.drools.rule.Package pkg = new org.drools.rule.Package(ProtempaUtil.DROOLS_PACKAGE_NAME);
        JBossRuleCreator creator = newRuleCreator(Arrays.asList(propositionDefinitions));
        for (Rule rule : creator.getRules()) {
            pkg.addRule(rule);
        }
        try {
            ruleBase.addPackage(pkg); // Drools merges it.
        } catch (Exception ex) {
            throw new QueryException(getQuery().getName(), ex);
        }
    }

    private void deleteFromRuleBase(RuleBase ruleBase, PropositionDefinition... propositionDefinitions) throws ProtempaException {
        JBossRuleCreator creator = newRuleCreator(Arrays.asList(propositionDefinitions));
        for (Rule rule : creator.getRules()) {
            ruleBase.removeRule(ProtempaUtil.DROOLS_PACKAGE_NAME, rule.getName());
        }
    }

}
