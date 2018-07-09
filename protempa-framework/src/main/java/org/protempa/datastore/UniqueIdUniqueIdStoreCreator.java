/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
import java.util.List;
import org.eurekaclinical.datastore.bdb.BdbPersistentStoreFactory;
import org.eurekaclinical.datastore.DataStore;
import org.protempa.proposition.UniqueId;

public final class UniqueIdUniqueIdStoreCreator extends AbstractDataStoreCreator<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> {

    public static final String DATABASE_NAME = "UniqueIdStore";
    private final BdbPersistentStoreFactory storeFactory;
    private int index;

    public UniqueIdUniqueIdStoreCreator() {
        this(null);

    }

    public UniqueIdUniqueIdStoreCreator(String environmentName) {
        super(environmentName);
        if (environmentName != null) {
            this.storeFactory = new BdbPersistentStoreFactory(environmentName);
        } else {
            this.storeFactory = null;
        }
    }

    @Override
    public DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> getPersistentStore() {
        if (this.storeFactory == null) {
            throw new IllegalStateException("null environmentName; cannot get a persistent store");
        }
        DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> store =
                this.storeFactory.newInstance(nextDatabaseName());
        return store;
    }

    @Override
    protected String nextDatabaseName() {
        synchronized (this) {
            return DATABASE_NAME + (index++);
        }
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
