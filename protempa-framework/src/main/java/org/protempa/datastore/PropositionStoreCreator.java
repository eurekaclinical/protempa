package org.protempa.datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.protempa.proposition.Proposition;

/**
 * A permanent store mapping key IDs to lists of propositions.
 * 
 * @author Michel Mansour
 */
public final class PropositionStoreCreator<P extends Proposition> implements
        ProtempaDataStoreCreator<String, List<P>> {
    private PropositionStoreCreator() {
    }

    public static <Q extends Proposition> PropositionStoreCreator<Q> getInstance() {
        return new PropositionStoreCreator<Q>();
    }

    // We can't keep a static collection of the class parameter P, so it has
    // to be raw. But the only place this map is manipulated is within this
    // class, so it's safe.
    @SuppressWarnings("rawtypes")
    private static Map stores = new HashMap();

    // The map of instances isn't generic, so we have to cast to the correct
    // parameterized type. This is safe because this method is the only place
    // we access the map.
    @SuppressWarnings("unchecked")
    @Override
    public DataStore<String, List<P>> getPersistentStore(String name) {
        if (stores.containsKey(name)) {
            return (DataStore<String, List<P>>) stores.get(name);
        } else {
            DataStore<String, List<P>> store = DataStoreFactory.getPersistentStore(name);
            stores.put(name, store);
            return store;
        }
    }

    @Override
    public DataStore<String, List<P>> newCacheStore() {
        return DataStoreFactory.<String, List<P>> newCacheStore();
    }
}
