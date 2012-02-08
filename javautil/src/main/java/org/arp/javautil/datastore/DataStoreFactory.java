/*
 * #%L
 * JavaUtil
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.arp.javautil.datastore;

/**
 * A factory for building data stores. Two static methods are provided:
 * <ol>
 * <li><code>{@link #newCacheStore}</code> returns a new temporary data store
 * that can act as a cache</li>
 * <li><code>{@link #getPersistentStore}</code> returns a persistent store with
 * the given name</li>
 * </ol>
 * 
 * @author Michel Mansour
 * 
 */
public final class DataStoreFactory {

    private DataStoreFactory() {
    }

    /**
     * Creates and returns a new key-value pair temporary cache.
     * 
     * @param <K>
     *            the key type to store
     * @param <V>
     *            the value type to store
     * @return a {@link DataStore} backed by a temporary cache implementation
     */
    public static <K, V> DataStore<K, V> newCacheStore() {
        return new BdbCache<K, V>();
    }

    /**
     * Gets a key-value pair permanent store with the given name. If the named
     * store doesn't exist, it will be created.
     * 
     * @param <K>
     *            the key type to store
     * @param <V>
     *            the value type to store
     * @param name
     *            the name of the store
     * @return a {@link DataStore} backed by a permanent store implementation
     */
    public static <K, V> DataStore<K, V> getPersistentStore(final String name) {
        return new BdbPersistentStore<K, V>(name);
    }
}
