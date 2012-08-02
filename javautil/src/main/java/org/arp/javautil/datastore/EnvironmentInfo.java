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
import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;

/**
 *
 * @author Andrew Post
 */
class EnvironmentInfo {
    private Environment environment;
    private StoredClassCatalog classCatalog;
    private BdbStoreFactory<?,?> storeFactory;

    EnvironmentInfo(Environment environment, StoredClassCatalog classCatalog,
            BdbStoreFactory<?,?> storeFactory) {
        this.environment = environment;
        this.classCatalog = classCatalog;
        this.storeFactory = storeFactory;
    }

    public StoredClassCatalog getClassCatalog() {
        return classCatalog;
    }

    public Environment getEnvironment() {
        return environment;
    }

    void closeAndRemoveDatabaseHandle(Database databaseHandle) {
        this.storeFactory.closeAndRemoveDatabaseHandle(databaseHandle);
    }
    
    void closeAndRemoveAllDatabaseHandles() {
        this.storeFactory.closeAndRemoveAllDatabaseHandles();
    }
}
