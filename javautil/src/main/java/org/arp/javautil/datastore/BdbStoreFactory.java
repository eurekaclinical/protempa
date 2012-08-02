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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Andrew Post
 */
public abstract class BdbStoreFactory<E, V> {

    private EnvironmentInfo envInfo;
    private final File envFile;
    private final List<Database> databaseHandles;
    private final BdbStoreShutdownHook shutdownHook;

    protected BdbStoreFactory(String pathname, boolean deleteOnExit) {
        assert pathname != null : "pathname cannot be null";
        this.envFile = new File(pathname);
        this.databaseHandles = 
                Collections.synchronizedList(new ArrayList<Database>());
        this.shutdownHook = new BdbStoreShutdownHook(deleteOnExit);
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    public final void shutdown() throws IOException {
        try {
            this.shutdownHook.shutdown();
        } catch (DatabaseException ex) {
            throw new DataStoreError(ex);
        }
    }

    public BdbMap<E, V> newInstance(String dbName) {
        if (dbName == null) {
            throw new IllegalArgumentException("dbName cannot be null");
        }
        synchronized (this) {
            if (this.envInfo == null) {
                createEnvironmentInfo();
            }
        }
        DatabaseConfig dbConfig = createDatabaseConfig();
        Database databaseHandle =
                this.envInfo.getEnvironment().openDatabase(null, dbName, 
                dbConfig);
        this.databaseHandles.add(databaseHandle);
        return new BdbMap<E, V>(this.envInfo, databaseHandle);
    }

    protected abstract EnvironmentConfig createEnvConfig();

    protected abstract DatabaseConfig createDatabaseConfig();

    protected abstract StoredClassCatalog createClassCatalog(Environment env);

    final void closeAndRemoveDatabaseHandle(Database databaseHandle) {
        databaseHandle.close();
        this.databaseHandles.remove(databaseHandle);
    }

    final void closeAndRemoveAllDatabaseHandles() {
        for (Database databaseHandle : this.databaseHandles) {
            databaseHandle.close();
        }
        this.databaseHandles.clear();
    }
    
    private void createEnvironmentInfo() {
        Environment env = createEnvironment();
        StoredClassCatalog classCatalog = createClassCatalog(env);
        this.envInfo = new EnvironmentInfo(env, classCatalog, this);
        this.shutdownHook.addEnvironmentInfo(this.envInfo);
    }
    
    private Environment createEnvironment() throws DatabaseException,
            IllegalArgumentException {
        EnvironmentConfig envConf = createEnvConfig();
        if (!envFile.exists()) {
            envFile.mkdirs();
        }
        DataStoreUtil.logger().log(Level.INFO,
                "Initialized BerkeleyDB cache environment at {0}",
                envFile.getAbsolutePath());

        return new Environment(this.envFile, envConf);
    }
}
