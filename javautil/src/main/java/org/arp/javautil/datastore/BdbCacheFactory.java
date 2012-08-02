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
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author Andrew Post
 */
public class BdbCacheFactory<E, V> extends BdbStoreFactory<E, V> {
    private static final String CLASS_CATALOG = "java_class_catalog";

    public BdbCacheFactory(String pathname, boolean deleteOnExit) {
        super(pathname, deleteOnExit);
    }

    @Override
    protected DatabaseConfig createDatabaseConfig() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTemporary(true);
        return dbConfig;
    }

    @Override
    protected EnvironmentConfig createEnvConfig() {
        EnvironmentConfig envConf = new EnvironmentConfig();
        envConf.setAllowCreate(true);
        envConf.setTransactional(false);
        envConf.setConfigParam(EnvironmentConfig.EVICTOR_LRU_ONLY, "false");
        envConf.setConfigParam(EnvironmentConfig.EVICTOR_NODES_PER_SCAN,
                "100");
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        long max = memoryUsage.getMax();
        long used = memoryUsage.getUsed();
        long available = max - used;
        long cacheSize = Math.round(available / 4.0);
        envConf.setCacheSize(cacheSize);
        DataStoreUtil.logger().log(Level.FINE,
                "BerkeleyDB cache size: {0} bytes", cacheSize);
        return envConf;
    }

    @Override
    protected StoredClassCatalog createClassCatalog(Environment env)
            throws IllegalArgumentException {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTemporary(true);
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(false);
        Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);
        return new StoredClassCatalog(catalogDb);
    }
    
    
}
