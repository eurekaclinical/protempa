package org.arp.javautil.datastore;

public final class DataStoreFactory {
    
    private DataStoreFactory() { }
    
    /**
     * Creates and returns a new key-value pair temporary cache.
     * 
     * @param <K> the key type to store
     * @param <V> the value type to store
     * @return a {@link DataStore} backed by a temporary cache implementation
     */
    public static <K, V> DataStore<K, V> newCacheStore() {
        return new BdbCache<K, V>();
    }
    
    /**
     * Gets a key-value pair permanent store with the given name. If the named
     * store doesn't exist, it will be created.
     * 
     * @param <K> the key type to store
     * @param <V> the value type to store
     * @param name the name of the store
     * @return a {@link DataStore} backed by a permanent store implementation
     */
    public static <K, V> DataStore<K, V> getPersistentStore(String name) {
        return new BdbPersistentStore<K, V>(name);
    }
}
