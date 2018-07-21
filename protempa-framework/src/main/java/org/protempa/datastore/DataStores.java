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

import java.io.IOException;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.eurekaclinical.datastore.DataStore;

/**
 * An interface for defining classes that create and return data stores.
 *
 * @author Michel Mansour
 *
 */
public interface DataStores extends AutoCloseable {
    
    boolean exists(String dbname) throws IOException;

    /**
     * Returns a permanent store with the given name.
     *
     * @param name the name of the store, or <code>null</code> if no store
     * exists with the given name.
     * @return a {@link DataStore} backed by a permanent store implementation
     * @throws java.io.IOException if an error occurred getting/creating the
     * data store.
     */
    DataStore<String, StatefulSession> getDataStore(String name) throws IOException;

    /**
     * Returns a permanent store with the given name.
     *
     * @param name the name of the store, or <code>null</code> if no store
     * exists with the given name.
     * @param ruleBase the rule base to use for creating working memories. This
     * must be specified if you are creating a new data store. If the requested
     * data store exists, it will use the persisted rule base.
     * @return a {@link DataStore} backed by a permanent store implementation
     * @throws java.io.IOException if an error occurred getting/creating the
     * data store.
     */
    DataStore<String, StatefulSession> newDataStore(String name, RuleBase ruleBase) throws IOException, DataStoreExistsException;

    @Override
    void close() throws IOException;

}
