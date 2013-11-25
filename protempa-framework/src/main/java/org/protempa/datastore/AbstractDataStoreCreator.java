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
import org.apache.commons.io.FileUtils;
import org.arp.javautil.datastore.BdbCacheFactory;
import org.arp.javautil.datastore.BdbUtil;
import org.arp.javautil.datastore.DataStore;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractDataStoreCreator<K, V>
        implements DataStoreCreator<K, V> {

    private final String environmentName;
    
    protected AbstractDataStoreCreator() {
        this(null);
    }

    protected AbstractDataStoreCreator(String environmentName) {
        this.environmentName = environmentName;
    }

    protected String getEnvironmentName() {
        return this.environmentName;
    }

    protected abstract String nextDatabaseName();

    @Override
    public DataStore<K, V> newCacheStore() throws IOException {
        String tempEnvironment;
        if (this.environmentName == null) {
            tempEnvironment =
                    BdbUtil.uniqueEnvironment("cache-store", null,
                    FileUtils.getTempDirectory());
        } else {
            tempEnvironment = this.environmentName;
        }
        BdbCacheFactory<K, V> bdbCacheFactory =
                new BdbCacheFactory<>(tempEnvironment, true);
        return bdbCacheFactory.newInstance(nextDatabaseName());
    }
}
