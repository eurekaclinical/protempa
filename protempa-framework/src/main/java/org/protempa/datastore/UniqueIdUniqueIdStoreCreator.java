package org.protempa.datastore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.protempa.proposition.UniqueId;

public final class UniqueIdUniqueIdStoreCreator implements
        ProtempaDataStoreCreator<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> {

    private UniqueIdUniqueIdStoreCreator() {
    }

    private final static UniqueIdUniqueIdStoreCreator INSTANCE = new UniqueIdUniqueIdStoreCreator();
    private static Map<String, DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>> stores = new HashMap<String, DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>>();

    public static UniqueIdUniqueIdStoreCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> getPersistentStore(
            String name) {
        if (stores.containsKey(name) && !stores.get(name).isClosed()) {
            return stores.get(name);
        } else {
            DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> store = DataStoreFactory
                    .getPersistentStore(name);
            stores.put(name, store);
            return store;
        }
    }

    @Override
    public DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> newCacheStore() {
        return DataStoreFactory
                .<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> newCacheStore();
    }
    
    public static class Reference implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final UniqueId uniqueId;

        public Reference(String name, UniqueId uniqueId) {
            assert name != null : "name cannot be null";
            assert uniqueId != null : "uniqueId cannot be null";
            
            this.name = name;
            this.uniqueId = uniqueId;
        }

        public String getName() {
            return name;
        }

        public UniqueId getUniqueId() {
            return uniqueId;
        }
    }

}
