package org.arp.javautil.map;

import com.sleepycat.bind.serial.ClassCatalog;
import java.io.File;
import java.util.UUID;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

class CacheDatabase {

    private static final String CLASS_CATALOG = "java_class_catalog";
    private static StoredClassCatalog classCatalog;
    private static Environment env;
    private static String location;
    private static Map<String, Database> maps =
            Collections.synchronizedMap(new HashMap<String, Database>());

    static {
        location = System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + UUID.randomUUID().toString();
        Runtime.getRuntime().addShutdownHook(new Thread("CacheDBShutdownHook") {

            @Override
            public void run() {
                if (env != null) {
                    try {
                        for (Database m : maps.values()) {
                            m.close();
                        }
                        classCatalog.close();
                        env.close();
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }
                    File dataDir = new File(location);
                    for (File f : dataDir.listFiles()) {
                        f.delete();
                    }
                    dataDir.delete();
                }
            }
        });
    }

    static Database createDatabase(String dbName) throws DatabaseException {
        createEnvironmentIfNeeded();

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTemporary(true);

        Database result = env.openDatabase(null, dbName, dbConfig);
        if (maps.put(dbName, result) != null) {
            throw new AssertionError("This shouldn't happen");
        }
        return result;
    }

    static ClassCatalog createOrGetClassCatalog() throws DatabaseException {
        createEnvironmentIfNeeded();
        createClassCatalogIfNeeded();

        return classCatalog;
    }

    private synchronized static void createClassCatalogIfNeeded()
            throws IllegalArgumentException,
            DatabaseException {
        if (classCatalog == null) {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTemporary(true);
            dbConfig.setAllowCreate(true);
            Database catalogDb =
                    env.openDatabase(null, CLASS_CATALOG, dbConfig);
            classCatalog = new StoredClassCatalog(catalogDb);
        }
    }

    private synchronized static void createEnvironmentIfNeeded()
            throws DatabaseException {
        if (env == null) {
            EnvironmentConfig envConf = new EnvironmentConfig();
            envConf.setAllowCreate(true);
            envConf.setTransactional(true);
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
            long max = memoryUsage.getMax();
            long used = memoryUsage.getUsed();
            long available = max - used;
            long cacheSize = Math.round(available/2.0);
            envConf.setCacheSize(cacheSize);
            MapUtil.logger().log(Level.FINE, "Cache size set to {0} bytes",
                    cacheSize);

            File envFile = new File(location);
            if (!envFile.exists()) {
                envFile.mkdirs();
            }

            env = new Environment(envFile, envConf);
        }
    }
}
