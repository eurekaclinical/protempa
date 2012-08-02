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

import com.sleepycat.je.Environment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Andrew Post
 */
public final class BdbStoreShutdownHook extends Thread {

    private final List<EnvironmentInfo> envInfos;
    private final boolean deleteOnExit;

    BdbStoreShutdownHook(boolean deleteOnExit) {
        this.envInfos = new ArrayList<EnvironmentInfo>();
        this.deleteOnExit = deleteOnExit;
    }

    void addEnvironmentInfo(EnvironmentInfo environmentInfo) {
        this.envInfos.add(environmentInfo);
    }

    void shutdown() throws IOException {
        synchronized (this) {
            for (EnvironmentInfo envInfo : this.envInfos) {
                envInfo.getClassCatalog().close();
                envInfo.closeAndRemoveAllDatabaseHandles();
                Environment env = envInfo.getEnvironment();
                try {
                    if (this.deleteOnExit) {
                        FileUtils.deleteDirectory(env.getHome());
                    }
                } finally {
                    env.close();
                }
            }
            this.envInfos.clear();
        }
    }

    @Override
    public void run() {
        try {
            shutdown();
        } catch (IOException ex) {
            Logger logger = DataStoreUtil.logger();
            logger.log(Level.SEVERE, "Error during shutdown", ex);
        }
    }
}
