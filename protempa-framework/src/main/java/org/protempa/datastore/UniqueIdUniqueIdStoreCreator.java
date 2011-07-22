package org.protempa.datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.protempa.proposition.UniqueIdentifier;

public final class UniqueIdUniqueIdStoreCreator implements
        ProtempaDataStoreCreator<UniqueIdentifier, List<UniqueIdentifier>> {

    private UniqueIdUniqueIdStoreCreator() {
    }

    private final static UniqueIdUniqueIdStoreCreator INSTANCE = new UniqueIdUniqueIdStoreCreator();
    private static Map<String, DataStore<UniqueIdentifier, List<UniqueIdentifier>>> stores = new HashMap<String, DataStore<UniqueIdentifier, List<UniqueIdentifier>>>();

    public static UniqueIdUniqueIdStoreCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public DataStore<UniqueIdentifier, List<UniqueIdentifier>> getPersistentStore(
            String name) {
        if (stores.containsKey(name)) {
            return stores.get(name);
        } else {
            DataStore<UniqueIdentifier, List<UniqueIdentifier>> store = DataStoreFactory
                    .getPersistentStore(name);
            stores.put(name, store);
            return store;
        }
    }

    @Override
    public DataStore<UniqueIdentifier, List<UniqueIdentifier>> newCacheStore() {
        return DataStoreFactory
                .<UniqueIdentifier, List<UniqueIdentifier>> newCacheStore();
    }

}
