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
