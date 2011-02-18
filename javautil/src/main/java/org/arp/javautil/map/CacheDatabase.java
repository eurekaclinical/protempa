package org.arp.javautil.map;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class CacheDatabase {

    private static final String CLASS_CATALOG = "java_class_catalog";
    private static StoredClassCatalog classCatalog;
    private static Environment env;
    private static String location;

    static {
        location = System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + UUID.randomUUID().toString();
        Runtime.getRuntime().addShutdownHook(new Thread("CacheDBShutdownHook") {
            @Override
            public void run() {
                if (env != null) {
                    try {
                        @SuppressWarnings("unchecked")
                        List<String> dbNames = CacheDatabase.env
                                .getDatabaseNames();
                        for (String dbName : dbNames) {
                            CacheDatabase.getDatabase(dbName).close();
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

    public static Database getDatabase(String dbName) throws DatabaseException {
        if (env == null) {
            createEnvironment();
        }

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(true);
        return env.openDatabase(null, dbName, dbConfig);
    }

    public static ClassCatalog getClassCatalog() throws DatabaseException {
        if (classCatalog == null) {
            createEnvironment();
        }
        return classCatalog;
    }

    synchronized private static void createEnvironment()
            throws DatabaseException {
        EnvironmentConfig envConf = new EnvironmentConfig();
        envConf.setAllowCreate(true);
        envConf.setTransactional(true);

        File envFile = new File(location);
        if (!envFile.exists()) {
            envFile.mkdirs();
        }

        env = new Environment(envFile, envConf);

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);

        Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);

        classCatalog = new StoredClassCatalog(catalogDb);
    }
}
