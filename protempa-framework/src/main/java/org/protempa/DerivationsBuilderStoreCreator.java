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
package org.protempa;

import org.eurekaclinical.datastore.bdb.BdbPersistentStoreFactory;

import org.eurekaclinical.datastore.DataStore;
import org.protempa.datastore.AbstractDataStoreCreator;

final class DerivationsBuilderStoreCreator extends
        AbstractDataStoreCreator<String, DerivationsBuilder> {
    
    public static final String DATABASE_NAME = "DerivationsBuilderStore";
    
    private final BdbPersistentStoreFactory storeFactory;
    private int index;

    public DerivationsBuilderStoreCreator(String environmentName) {
        super(environmentName);
        if (environmentName != null) {
        this.storeFactory = new BdbPersistentStoreFactory(environmentName);
        } else {
            throw new IllegalStateException(
                    "null environmentName; cannot get a persistent store");
        }
    }

    @Override
    public DataStore<String, DerivationsBuilder> getPersistentStore() {
        if (this.storeFactory == null) {
            throw new IllegalStateException("null environmentName; cannot get a persistent store");
        }
        DataStore<String, DerivationsBuilder> store = 
                this.storeFactory.newInstance(nextDatabaseName());
        return store;
    }

    @Override
    protected String nextDatabaseName() {
        synchronized (this) {
            return DATABASE_NAME + (index++);
        }
    }
}
