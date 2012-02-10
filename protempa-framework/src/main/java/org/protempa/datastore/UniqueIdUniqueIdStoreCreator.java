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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.protempa.proposition.UniqueId;

public final class UniqueIdUniqueIdStoreCreator implements
        DataStoreCreator<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> {

    private UniqueIdUniqueIdStoreCreator() {
    }

    private final static UniqueIdUniqueIdStoreCreator INSTANCE = new UniqueIdUniqueIdStoreCreator();
    private static Map<String, DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>> stores = new HashMap<String, DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>>();

    public static UniqueIdUniqueIdStoreCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> getPersistentStore(
            String name) {
        if (stores.containsKey(name) && !stores.get(name).isClosed()) {
            return stores.get(name);
        } else {
            DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> store = DataStoreFactory
                    .getPersistentStore(name);
            stores.put(name, store);
            return store;
        }
    }

    @Override
    public DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> newCacheStore() {
        return DataStoreFactory
                .<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> newCacheStore();
    }
    
    public static class Reference implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final UniqueId uniqueId;

        public Reference(String name, UniqueId uniqueId) {
            assert name != null : "name cannot be null";
            assert uniqueId != null : "uniqueId cannot be null";
            
            this.name = name;
            this.uniqueId = uniqueId;
        }

        public String getName() {
            return name;
        }

        public UniqueId getUniqueId() {
            return uniqueId;
        }
    }

}
