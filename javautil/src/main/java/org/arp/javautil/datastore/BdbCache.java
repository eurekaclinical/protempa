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
    }

    BdbCache() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public synchronized void shutdown() {
        try {
            super.shutdown();
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
            envConf.setConfigParam(EnvironmentConfig.EVICTOR_NODES_PER_SCAN, "100");
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
