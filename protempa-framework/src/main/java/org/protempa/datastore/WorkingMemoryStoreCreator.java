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
package org.protempa.datastore;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eurekaclinical.datastore.bdb.BdbPersistentStoreFactory;

import org.eurekaclinical.datastore.DataStore;
import org.drools.RuleBase;
import org.drools.WorkingMemory;

/**
 * A class to generate permanent stores mapping key IDs to Drools working memory
 * objects.
 *
 * @author Michel Mansour
 */
public final class WorkingMemoryStoreCreator extends
        AbstractDataStoreCreator<String, WorkingMemory> {
    
    public static final String DATABASE_NAME = "WorkingMemoryStore";

    /*
     * To reconstruct Drools WorkingMemory objects from byte arrays, which is
     * how they are serialized by BerkeleyDB.
     */
    private final RuleBase ruleBase;
    private final BdbPersistentStoreFactory storeFactory;
    private int index;

    public WorkingMemoryStoreCreator(RuleBase ruleBase) {
        this(ruleBase, null);
    }
    
    public WorkingMemoryStoreCreator(RuleBase ruleBase, String environmentName) {
        super(environmentName);
        this.ruleBase = ruleBase;
        if (environmentName != null) {
            this.storeFactory =
                    new BdbPersistentStoreFactory(environmentName);
        } else {
            this.storeFactory = null;
        }
    }

    /**
     * Returns an instance of this class, which can be used to get either a
     * permanent or temporary store. This method accepts a Drools rule base,
     * which may be used by the underlying storage system to facilitate the
     * deserialization of Drools working memory objects.
     *
     * @param ruleBase the Drools rule base that all of the working memory
     * objects are generated from.
     * @return a {@link WorkingMemoryStoreCreator}
     */
    public static WorkingMemoryStoreCreator getInstance(RuleBase ruleBase) {
        return new WorkingMemoryStoreCreator(ruleBase);
    }

    @Override
    public DataStore<String, WorkingMemory> getPersistentStore() {
        if (this.storeFactory == null) {
            throw new IllegalStateException("null environmentName; cannot get a persistent store");
        }
        Logger logger = DataStoreUtil.logger();
        logger.log(
                Level.FINEST,
                "Persistent store {0} has not been accessed during this"
                + " run or does not exist: attempting to get it from the underlying store",
                getEnvironmentName());
        DataStore<String, WorkingMemory> store = new DroolsWorkingMemoryStore(
                this.storeFactory, nextDatabaseName(), this.ruleBase);
        return store;
    }

    @Override
    protected String nextDatabaseName() {
        synchronized (this) {
            return DATABASE_NAME + (index++);
        }
    }
    
}
