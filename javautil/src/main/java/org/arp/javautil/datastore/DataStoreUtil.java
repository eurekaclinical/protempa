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

import java.util.logging.Logger;

/**
 * Utility class for the {@link org.arp.javautil.datastore} class. Contains
 * logging tools, constants, and other utility methods.
 * 
 * @author Michel Mansour
 * 
 */
public final class DataStoreUtil {

    private static class LazyLoggerHolder {
        private static final Logger instance = Logger.getLogger(DataStoreUtil.class
                .getPackage().getName());
    }
    
    /**
     * Gets the logger for this package.
     * 
     * @return a <code>Logger</code> object.
     */
    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    private DataStoreUtil() {} // to prevent instantiation
}
