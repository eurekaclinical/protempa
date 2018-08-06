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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.eurekaclinical.datastore.bdb.BdbPersistentStoreFactory;

import org.eurekaclinical.datastore.DataStore;
import org.eurekaclinical.datastore.DataStoreFactory;
import org.protempa.PropositionDefinition;
import org.protempa.PropositionDefinitionCache;
import org.protempa.WorkingMemoryFactStore;

/**
 * A class to generate permanent stores mapping key IDs to Drools working memory
 * objects.
 *
 * @author Michel Mansour
 */
public final class WorkingMemoryDataStores implements DataStores {

    public static final String DATABASE_NAME = "WorkingMemoryStore";

    private DataStoreFactory storeFactory;
    private Map<String, PropositionDefinitionCache> propositionDefinitionsInStores;
    private PropositionDefinitionCache cache;
    private Path storedPropDefsFile;
    private String databaseName;

    /**
     * Constructs a working memory creator.
     *
     * @param directory the directory in which the working memory data stores
     * will be stored. Cannot be <code>null</code>.
     * @param cache the proposition definitions that were queried.
     */
    public WorkingMemoryDataStores(Path directory, String name, PropositionDefinitionCache cache) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (cache == null) {
            throw new IllegalArgumentException("cache cannot be null");
        }
        
        this.storeFactory = new BdbPersistentStoreFactory(directory.toString());
        this.storedPropDefsFile = directory.resolve(name + ".stored-propdefs");
        if (Files.exists(this.storedPropDefsFile)) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(this.storedPropDefsFile))) {
                int size = ois.readInt();
                Collection<PropositionDefinition> propDefs = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    Object obj = ois.readObject();
                    if (obj == null) {
                        throw new IOException("null object read");
                    } else {
                        propDefs.add((PropositionDefinition) obj);
                    }
                }
                this.propositionDefinitionsInStores.put(name, new PropositionDefinitionCache(propDefs));
            } catch (ClassNotFoundException ex) {
                throw new IOException("Error deserializing proposition definitions", ex);
            }
        }
        cache.merge(this.propositionDefinitionsInStores.get(name));
        this.databaseName = name;
        this.cache = cache;
    }

    public PropositionDefinitionCache getPropositionDefinitionsInStores() {
        return this.propositionDefinitionsInStores.get(this.databaseName);
    }

    @Override
    public boolean exists() throws IOException {
        return this.storeFactory.exists(this.databaseName);
    }

    /**
     * Gets the persisted data store with the given name, if it exists, or
     * creates a new data store with the given name.
     *
     * @param name the name of the data store.
     * @return the data store.
     * @throws IOException if an error occurred querying for the data store.
     */
    @Override
    public DataStore<String, WorkingMemoryFactStore> getDataStore() throws IOException {
        return this.storeFactory.getInstance(this.databaseName);
    }

    @Override
    public void close() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(this.storedPropDefsFile))) {
            Collection<PropositionDefinition> all = cache.getAll();
            oos.writeInt(all.size());
            for (PropositionDefinition pd : all) {
                oos.writeObject(pd);
            }
            this.storeFactory.close();
            this.storeFactory = null;
        } catch (IOException ex) {
            if (this.storeFactory != null) {
                try {
                    this.storeFactory.close();
                } catch (IOException suppress) {
                    ex.addSuppressed(suppress);
                }
                throw ex;
            }
        }
    }
}
