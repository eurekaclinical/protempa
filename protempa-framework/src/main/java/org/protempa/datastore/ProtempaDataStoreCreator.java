package org.protempa.datastore;

import org.arp.javautil.datastore.DataStore;

/**
 * An interface for defining classes that return external data stores.
 * 
 * @author Michel Mansour
 * 
 * @param <K>
 *            the key type to store
 * @param <V>
 *            the value type to store
 */
public interface ProtempaDataStoreCreator<K, V> {

    /**
     * Returns a permanent store with the given name. If a store with the name
     * exists, it may be returned. Otherwise, a new store with the name will be
     * created.
     * 
     * @param name
     *            the name of the store
     * @return a {@link DataStore} backed by a permanent store implementation
     * @throws StoreError
     *             if an error occurs while trying to retrieve the store
     */
    public DataStore<K, V> getPersistentStore(String name);

    /**
     * Returns a new cache store. A cache store is temporary and unnamed, so it
     * is the responsibility of the caller to keep a reference to the returned
     * store.
     * 
     * @return a {@link DataStore} backed by a temporary store implementation
     * @throws StoreError
     *             if an error occurs while trying to create the store
     */
    public DataStore<K, V> newCacheStore();
}
