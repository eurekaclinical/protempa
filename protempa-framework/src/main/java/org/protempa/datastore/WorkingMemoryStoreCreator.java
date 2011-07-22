package org.protempa.datastore;

import java.util.HashMap;
import java.util.Map;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.drools.WorkingMemory;

/**
 * A permanent store mapping key IDs to Drools working memory objects.
 * 
 * @author Michel Mansour
 */
public final class WorkingMemoryStoreCreator implements ProtempaDataStoreCreator<String, WorkingMemory> {
    private WorkingMemoryStoreCreator() {
    }

    private static Map<String, DataStore<String, WorkingMemory>> stores = 
        new HashMap<String, DataStore<String, WorkingMemory>>();

    private static final WorkingMemoryStoreCreator INSTANCE = new WorkingMemoryStoreCreator();
    
    public static WorkingMemoryStoreCreator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public DataStore<String, WorkingMemory> getPersistentStore(String name) {
        if (stores.containsKey(name)) {
            return stores.get(name);
        } else {
            DataStore<String, WorkingMemory> store = DataStoreFactory
                    .getPersistentStore(name);
            stores.put(name, store);
            return store;
        }
    }
    
    @Override
    public DataStore<String, WorkingMemory> newCacheStore() {
        return DataStoreFactory.<String, WorkingMemory> newCacheStore();
    }
}
