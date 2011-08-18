package org.arp.javautil.datastore;

import java.util.Map;

/**
 * Represents a data store, which can be either temporary or persistent. It can
 * be treated just like a {@link java.util.Map}, but defines one extra method,
 * <code>shutdown</code>.
 * 
 * @param <K>
 *            the key type to store
 * @param <V>
 *            the value type to store
 * 
 * @author Michel Mansour
 */
public interface DataStore<K, V> extends Map<K, V> {

    /**
     * Performs any clean up of the store and shuts it down.
     */
    void shutdown();
}
