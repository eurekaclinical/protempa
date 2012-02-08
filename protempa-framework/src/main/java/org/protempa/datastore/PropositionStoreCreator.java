/*
 * #%L
 * Protempa Framework
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
package org.protempa.datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static Map<String, DataStore> stores = new HashMap<String, DataStore>();

    // The map of instances isn't generic, so we have to cast to the correct
    // parameterized type. This is safe because this method is the only place
    // we access the map.
    @SuppressWarnings("unchecked")
    @Override
    public DataStore<String, List<P>> getPersistentStore(String name) {
        Logger logger = DataStoreUtil.logger();
        logger.log(Level.FINE, "Getting persistent store {0}", name);
        if (stores.containsKey(name) && !stores.get(name).isClosed()) {
            logger.log(
                    Level.FINEST,
                    "Persistent store {0} has been accessed during this run: using it",
                    name);
            return (DataStore<String, List<P>>) stores.get(name);
        } else {
            logger.log(
                    Level.FINEST,
                    "Persistent store {0} has not been accessed during this" +
                    " run or does not exist: attempting to get it from the underlying store",
                    name);
            DataStore<String, List<P>> store = DataStoreFactory
                    .getPersistentStore(name);
            logger.log(Level.FINEST, "Got persistent store {0}", name);
            stores.put(name, store);
            return store;
        }
    }

    @Override
    public DataStore<String, List<P>> newCacheStore() {
        return DataStoreFactory.<String, List<P>> newCacheStore();
    }
}
