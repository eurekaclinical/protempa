package org.arp.javautil.datastore;

import java.util.Map;

/**
 * Represents a data store, which can be either temporary or persistent. It can
 * be treated just like a {@link java.util.Map}, but defines two extra methods,
 * <code>shutdown</code>, which closes the store, and <code>isClosed</code>..
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

    /**
     * Checks whether the store has already been shut down
     * 
     * @return <code>true</code> if the store has been shutdown;
     *         <code>false</code> otherwise
     */
    boolean isClosed();
}
