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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * BerkeleyDB implementation of a temporary key-value store, which can be used
 * as an on-disk cache. Implements the {@link java.util.Map} interface for
 * interoperability with pre-existing code that uses <code>Map</code>s.
 * 
 * @author Michel Mansour
 * 
 * @param <K>
 *            the key type of the store
 * @param <V>
 *            the value type of the store
 */
public final class BdbCache<K, V> extends BdbMap<K, V> {

    private static final String CLASS_CATALOG = "java_class_catalog";

    private static StoredClassCatalog classCatalog;
    private static Environment env;
    private static String location;
    

    /*
     * to keep track of the caches created so that we can properly close the
     * environment when the system shuts down.
     */
    private static Map<String, Database> caches = Collections
            .synchronizedMap(new HashMap<String, Database>());

    private final String dbName;
    private boolean isClosed;

    static {
        location = System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + UUID.randomUUID().toString();

        Runtime.getRuntime().addShutdownHook(
                new Thread("BdbCacheShutdownHook") {

                    @Override
                    public void run() {
                        if (env != null) {
                            try {
                                for (Database db : caches.values()) {
                                    db.close();
                                }
                                classCatalog.close();
                                if (env.isValid()) {
                                    env.close();
                                }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        File dataDir = new File(location);
                        for (File f : dataDir.listFiles()) {
                            f.delete();
                        }
                        dataDir.delete();

                    }
                });
    }

    BdbCache(String dbName) {
        super(dbName);
        this.dbName = dbName;
        this.isClosed = false;
    }

    BdbCache() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public synchronized void shutdown() {
        try {
            super.shutdown();
            this.isClosed = true;
            caches.remove(dbName);
            if (caches.isEmpty()) {
                classCatalog.close();
                env.close();
            }
        } catch (DatabaseException ex) {
            throw new DataStoreError(ex);
        }
    }
    
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    Database getDatabase(String dbName) {
        createEnvironmentIfNeeded();

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTemporary(true);

        Database result = env.openDatabase(null, dbName, dbConfig);
        if (caches.put(dbName, result) != null) {
            throw new AssertionError("Failed to open BerkeleyDB cache: "
                    + dbName);
        }
        
        return result;
    }

    @Override
    ClassCatalog createOrGetClassCatalog() {
        createEnvironmentIfNeeded();
        createClassCatalogIfNeeded();

        return classCatalog;
    }

    private synchronized void createClassCatalogIfNeeded()
            throws IllegalArgumentException {
        if (classCatalog == null) {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTemporary(true);
            dbConfig.setAllowCreate(true);
            dbConfig.setTransactional(false);
            Database catalogDb = env
                    .openDatabase(null, CLASS_CATALOG, dbConfig);
            classCatalog = new StoredClassCatalog(catalogDb);
        }
    }

    private synchronized void createEnvironmentIfNeeded() {
        if (env == null) {
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

            File envFile = new File(location);
            if (!envFile.exists()) {
                envFile.mkdirs();
            }
            DataStoreUtil.logger().log(Level.INFO,
                    "Initialized BerkeleyDB cache environment at {0}",
                    envFile.getAbsolutePath());
            DataStoreUtil.logger().log(Level.FINE,
                    "BerkeleyDB cache size: {0} bytes", cacheSize);

            env = new Environment(envFile, envConf);
        }
    }
}
