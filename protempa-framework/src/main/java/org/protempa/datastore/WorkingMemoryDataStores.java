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

import java.io.IOException;
import org.drools.RuleBase;
import org.eurekaclinical.datastore.bdb.BdbPersistentStoreFactory;

import org.eurekaclinical.datastore.DataStore;
import org.drools.StatefulSession;
import org.eurekaclinical.datastore.DataStoreFactory;

/**
 * A class to generate permanent stores mapping key IDs to Drools working memory
 * objects.
 *
 * @author Michel Mansour
 */
public final class WorkingMemoryDataStores implements DataStores {

    public static final String DATABASE_NAME = "WorkingMemoryStore";

    private final DataStoreFactory storeFactory;
    private RuleBase ruleBase;
    private final String directory;

    /**
     * Constructs a working memory creator.
     *
     * @param directory the directory in which the working memory data stores
     * will be stored. Cannot be <code>null</code>.
     */
    public WorkingMemoryDataStores(String directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        this.storeFactory = new BdbPersistentStoreFactory(directory);
        this.directory = directory;
    }
    
    @Override
    public boolean exists(String dbname) throws IOException {
        return this.storeFactory.exists(dbname);
    }

    /**
     * Gets the persisted data store with the given name, if it exists. Sets 
     * the <code>ruleBase</code> field to the rule base of that data store.
     *
     * @param name the name of the data store.
     * @return the data store.
     * @throws IOException if an error occurred querying for the data store.
     */
    @Override
    public DataStore<String, StatefulSession> getDataStore(String name, RuleBase ruleBase) throws IOException {
        DataStore dataStore = this.storeFactory.getInstance(name);
        DroolsWorkingMemoryStore result = 
                new DroolsWorkingMemoryStore(dataStore, this.directory, 
                        name, ruleBase);
        this.ruleBase = result.getRuleBase();
        return result;
    }

    /**
     * Gets the rule base that was retrieved by the {@link #getDataStore(java.lang.String)
     * }
     * method or given to the {@link #newDataStore(org.drools.RuleBase, java.lang.String)
     * }
     * method, depending on which of those methods was last called.
     *
     * @return the rule base, or <code>null</code> if neither the
     * {@link #getDataStore(java.lang.String) } nor the
     * {@link #newDataStore(org.drools.RuleBase, java.lang.String) } has ever
     * been called.
     */
    public RuleBase getRuleBase() {
        return ruleBase;
    }

    @Override
    public void close() throws IOException {
        this.storeFactory.close();
    }

}
