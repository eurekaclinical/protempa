package org.protempa;

import java.util.HashMap;
import java.util.Map;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.protempa.datastore.ProtempaDataStoreCreator;

final class DerivationsBuilderStoreCreator implements
        ProtempaDataStoreCreator<String, DerivationsBuilder> {

    private DerivationsBuilderStoreCreator() {
    }

    private static Map<String, DataStore<String, DerivationsBuilder>> stores = 
        new HashMap<String, DataStore<String, DerivationsBuilder>>();

    private static final DerivationsBuilderStoreCreator INSTANCE = new DerivationsBuilderStoreCreator();

    public static DerivationsBuilderStoreCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public DataStore<String, DerivationsBuilder> getPersistentStore(String name) {
        if (stores.containsKey(name) && !stores.get(name).isClosed()) {
            return stores.get(name);
        } else {
            DataStore<String, DerivationsBuilder> store = DataStoreFactory
                    .getPersistentStore("DerivationsBuilderStore-" + name);
            stores.put(name, store);
            return store;
        }
    }

    @Override
    public DataStore<String, DerivationsBuilder> newCacheStore() {
        throw new UnsupportedOperationException(
                "Temporary caches are not supported for DerivationsBuilder objects");
    }

}
