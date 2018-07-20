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
import org.eurekaclinical.datastore.bdb.BdbPersistentStoreFactory;

import org.eurekaclinical.datastore.DataStore;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.eurekaclinical.datastore.DataStoreFactory;

/**
 * A class to generate permanent stores mapping key IDs to Drools working memory
 * objects.
 *
 * @author Michel Mansour
 */
public final class WorkingMemoryDataStores implements
        DataStores<String, StatefulSession> {

    public static final String DATABASE_NAME = "WorkingMemoryStore";

    private final RuleBase ruleBase;
    private final DataStoreFactory storeFactory;

    /**
     * Constructs a working memory creator.
     *
     * @param ruleBase the Drools rule base from which working memory objects 
     * are generated. Cannot be <code>null</code>.
     * @param directory the directory in which the working memory data stores
     * will be stored. Cannot be <code>null</code>.
     */
    public WorkingMemoryDataStores(RuleBase ruleBase, String directory) {
        if (ruleBase == null) {
            throw new IllegalArgumentException("ruleBase cannot be null");
        }
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        this.ruleBase = ruleBase;
        this.storeFactory = new BdbPersistentStoreFactory(directory);
    }
    
    @Override
    public DataStore<String, StatefulSession> getDataStore(String name) throws IOException {
        return new DroolsWorkingMemoryStore(this.storeFactory, name, this.ruleBase);
    }

    @Override
    public void close() throws IOException {
        this.storeFactory.close();
    }

}
