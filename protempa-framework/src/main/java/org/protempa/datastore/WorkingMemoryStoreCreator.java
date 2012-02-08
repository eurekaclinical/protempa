/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.drools.RuleBase;
import org.drools.WorkingMemory;

/**
 * A class to generate permanent stores mapping key IDs to Drools working memory
 * objects.
 * 
 * @author Michel Mansour
 */
public final class WorkingMemoryStoreCreator implements
        ProtempaDataStoreCreator<String, WorkingMemory> {

    /*
     * To reconstruct Drools WorkingMemory objects from byte arrays, which is
     * how they are serialized by BerkeleyDB.
     */
    private final RuleBase ruleBase;

    private WorkingMemoryStoreCreator(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    private static Map<String, DataStore<String, WorkingMemory>> stores = new HashMap<String, DataStore<String, WorkingMemory>>();

    /**
     * Returns an instance of this class, which can be used to get either a
     * permanent or temporary store. This method accepts a Drools rule base,
     * which may be used by the underlying storage system to facilitate the
     * deserialization of Drools working memory objects.
     * 
     * @param ruleBase
     *            the Drools rule base that all of the working memory objects
     *            are generated from.
     * @return a {@link WorkingMemoryStoreCreator}
     */
    public static WorkingMemoryStoreCreator getInstance(RuleBase ruleBase) {
        return new WorkingMemoryStoreCreator(ruleBase);
    }

    @Override
    public DataStore<String, WorkingMemory> getPersistentStore(String name) {
        Logger logger = DataStoreUtil.logger();
        if (stores.containsKey(name) && !stores.get(name).isClosed()) {
            logger.log(
                    Level.FINEST,
                    "Persistent store {0} has been accessed during this run: using it",
                    name);
            return stores.get(name);
        } else {
            logger.log(
                    Level.FINEST,
                    "Persistent store {0} has not been accessed during this" +
                    " run or does not exist: attempting to get it from the underlying store",
                    name);
            DataStore<String, WorkingMemory> store = new DroolsWorkingMemoryStore(
                    name, this.ruleBase);
            stores.put(name, store);
            return store;
        }
    }

    @Override
    public DataStore<String, WorkingMemory> newCacheStore() {
        return DataStoreFactory.<String, WorkingMemory> newCacheStore();
    }
}
