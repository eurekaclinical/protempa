/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.backend.dsb.relationaldb;

import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;

/**
 *
 * @author Andrew Post
 */
class SQLGenUtil {

    static final String SYSTEM_PROPERTY_SKIP_EXECUTION =
            "protempa.dsb.relationaldatabase.skipexecution";

    static final String SYSTEM_PROPERTY_FORCE_SQL_GENERATOR =
            "protempa.dsb.relationaldatabase.sqlgenerator";

    private SQLGenUtil() {
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(SQLGenUtil.class.getPackage().getName());
    }

    /**
     * Gets the logger for this package.
     *
     * @return a {@link Logger} object.
     */
    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    static boolean somePropIdsMatch(EntitySpec es1, EntitySpec es2) {
        return Collections.containsAny(Arrays.asSet(es1.getPropositionIds()),
                es2.getPropositionIds());
    }
}
