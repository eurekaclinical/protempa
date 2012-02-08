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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * A BerkeleyDB implementation of a persistent key-value store. Implements the
 * {@link java.util.Map} interface for interoperability with pre-existing code
 * that uses <code>Map</code>s.
 * 
 * @author Michel Mansour
 * 
 * @param <K>
 *            the key type of the store
 * @param <V>
 *            the value type of the store
 */
public class BdbPersistentStore<K, V> extends BdbMap<K, V> {

    private static final String CLASS_CATALOG = "java_class_catalog";
    private static final String ENV_PROPERTY = "store.env.name";
    private static Environment env;
    private static StoredClassCatalog classCatalog;

    /*
     * to keep track of the databases created in order to properly shut down the
     * environment when the system shuts down and to prevent the creation of
     * multiple databases with the same name
     */
    private static Map<String, Database> stores = new HashMap<String, Database>();

    private final String dbName;
    private boolean isClosed;

    static {
        Runtime.getRuntime().addShutdownHook(
                new Thread("BdbPersistentStoreShutdownHook") {
                    @Override
                    public void run() {
                        if (env != null) {
                            for (Database db : stores.values()) {
                                db.close();
                            }
                            stores.clear();
                            classCatalog.close();
                            env.close();
                        }
                    }
                });
    }

    protected BdbPersistentStore(String dbName) {
        super(dbName);
        this.dbName = dbName;
        this.isClosed = false;
    }

    @Override
    public synchronized void shutdown() {
        try {
            super.shutdown();
            stores.remove(dbName);
            if (stores.isEmpty()) {
                classCatalog.close();
                classCatalog = null;
                env.close();
                env = null;
            }
            this.isClosed = true;
        } catch (DatabaseException ex) {
            throw new DataStoreError(ex);
        }
    }
    
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    Database getDatabase(String dbName) {
        if (stores.containsKey(dbName)) {
            DataStoreUtil
                    .logger()
                    .log(Level.INFO,
                            "BerkeleyDB persistent store {0} already exists: returning it",
                            dbName);
            return stores.get(dbName);
        }
        createEnvironmentIfNeeded();

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTemporary(false);

        boolean dbExists = false;
        for (String name : env.getDatabaseNames()) {
            if (name.equals(dbName)) {
                dbExists = true;
            }
        }
        Database result = env.openDatabase(null, dbName, dbConfig);
        if (result == null) {
            throw new AssertionError("Failed to create BerkeleyDB database "
                    + dbName + " in environment "
                    + env.getHome().getAbsolutePath());
        }
        stores.put(dbName, result);

        DataStoreUtil
                .logger()
                .log(Level.INFO,
                        "{0} BerkeleyDB persistent store with name {1} at: {2}",
                        new Object[] {
                                dbExists ? "Opened" : "Created",
                                dbName,
                                env.getHome().getAbsolutePath()
                                        + System.getProperty("file.separator")
                                        + dbName });

        return result;
    }

    ClassCatalog createOrGetClassCatalog() throws DatabaseException {
        createEnvironmentIfNeeded();
        createClassCatalogIfNeeded();

        return classCatalog;
    }

    private synchronized void createClassCatalogIfNeeded()
            throws IllegalArgumentException, DatabaseException {
        if (classCatalog == null) {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTemporary(false);
            dbConfig.setAllowCreate(true);
            Database catalogDb = env
                    .openDatabase(null, CLASS_CATALOG, dbConfig);
            classCatalog = new StoredClassCatalog(catalogDb);
        }
    }

    private synchronized void createEnvironmentIfNeeded()
            throws DatabaseException {
        if (env == null || !env.isValid()) {
            if (!System.getProperties().containsKey(ENV_PROPERTY)) {
                throw new DataStoreError(
                        "Persistent store requested but failed to specify environment name. Environment name should be specified using system property "
                                + ENV_PROPERTY);
            }
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

            File envFile = new File(System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator")
                    + System.getProperty(ENV_PROPERTY));
            if (!envFile.exists()) {
                logger.log(
                        Level.INFO,
                        "Environment path {0} does not exist: creating directories",
                        envFile.getAbsolutePath());
                envFile.mkdirs();
            } else {
                logger.log(
                        Level.INFO,
                        "Environment path {0} already exists: using existing environment",
                        envFile.getAbsolutePath());
            }

            env = new Environment(envFile, envConf);
            logger.log(
                    Level.INFO,
                    "Initialized BerkeleyDB persistent store environment at {0} with cache size {1}",
                    new Object[] { envFile.getAbsolutePath(),
                            env.getConfig().getCacheSize() });
        }
    }
}
