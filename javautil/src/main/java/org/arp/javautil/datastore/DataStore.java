package org.arp.javautil.datastore;

import java.util.Map;

public interface DataStore<K, V> extends Map<K, V> {

    /**
     * Performs any clean up of the store and shuts it down.
     */
    public void shutdown();
}
