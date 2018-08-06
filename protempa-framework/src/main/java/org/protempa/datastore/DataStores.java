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
import org.eurekaclinical.datastore.DataStore;
import org.protempa.WorkingMemoryFactStore;

/**
 * An interface for defining classes that create and return data stores.
 *
 * @author Michel Mansour
 *
 */
public interface DataStores extends AutoCloseable {
    
    boolean exists() throws IOException;

    /**
     * Returns a permanent store with the given name.
     *
     * @param name the name of the store, or <code>null</code> if no store
     * exists with the given name.
     * @return a {@link DataStore} backed by a permanent store implementation
     * @throws java.io.IOException if an error occurred getting/creating the
     * data store.
     */
    DataStore<String, WorkingMemoryFactStore> getDataStore() throws IOException, DataStoreExistsException;

    @Override
    void close() throws IOException;

}
