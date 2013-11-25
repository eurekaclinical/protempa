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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.datastore.BdbPersistentStoreFactory;

import org.arp.javautil.datastore.DataStore;
import org.protempa.proposition.Proposition;

/**
 * A permanent store mapping key IDs to lists of propositions.
 *
 * @author Michel Mansour
 */
public final class PropositionStoreCreator<P extends Proposition> extends AbstractDataStoreCreator<String, List<P>> {

    public static final String DATABASE_NAME = "PropositionStore";
    private final BdbPersistentStoreFactory<String, List<P>> storeFactory;
    private int index;
    
    public PropositionStoreCreator() {
        this(null);
    }

    public PropositionStoreCreator(String environmentName) {
        super(environmentName);
        if (environmentName != null) {
            this.storeFactory =
                    new BdbPersistentStoreFactory<>(environmentName);
        } else {
            this.storeFactory = null;
        }
    }

    // The map of instances isn't generic, so we have to cast to the correct
    // parameterized type. This is safe because this method is the only place
    // we access the map.
    @SuppressWarnings("unchecked")
    @Override
    public DataStore<String, List<P>> getPersistentStore() {
        if (this.storeFactory == null) {
            throw new IllegalStateException("null environmentName; cannot get a persistent store");
        }
        Logger logger = DataStoreUtil.logger();
        String dbName = nextDatabaseName();
        logger.log(Level.FINE, "Getting persistent store {0}", dbName);
        DataStore<String, List<P>> store =
                this.storeFactory.newInstance(dbName);
        logger.log(Level.FINEST, "Got persistent store {0}", dbName);
        return store;
    }

    @Override
    protected String nextDatabaseName() {
        synchronized (this) {
            return DATABASE_NAME + (index++);
        }
    }
}
