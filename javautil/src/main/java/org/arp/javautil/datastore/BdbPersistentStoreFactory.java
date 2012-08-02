/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.datastore;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
public class BdbPersistentStoreFactory<E, V> extends BdbStoreFactory<E, V> {
    private static final String CLASS_CATALOG = "java_class_catalog";
    
    public BdbPersistentStoreFactory(String pathname) {
        super(pathname, false);
    }
    
    @Override
    protected StoredClassCatalog createClassCatalog(Environment env)
            throws IllegalArgumentException, DatabaseException {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTemporary(false);
        dbConfig.setAllowCreate(true);
        Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);
        return new StoredClassCatalog(catalogDb);
    }

    @Override
    protected EnvironmentConfig createEnvConfig() {
        Logger logger = DataStoreUtil.logger();
        EnvironmentConfig envConf = new EnvironmentConfig();
        envConf.setAllowCreate(true);
        envConf.setTransactional(true);

        logger.log(Level.FINE, "Calculating cache size");
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        long max = memoryUsage.getMax();
        long used = memoryUsage.getUsed();
        long available = max - used;
        long cacheSize = Math.round(available / 6.0);
        envConf.setCacheSize(cacheSize);
        logger.log(Level.FINE, "Cache size set to {0}", cacheSize);
        return envConf;
    }

    @Override
    protected DatabaseConfig createDatabaseConfig() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTemporary(false);
        return dbConfig;
    }
}
