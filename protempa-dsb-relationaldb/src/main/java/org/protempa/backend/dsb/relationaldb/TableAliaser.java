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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TableAliaser {

    private static final String DEFAULT_PREFIX = "a";
    
    private final String prefix;
    private final Map<ColumnSpec, Integer> indices;

    TableAliaser(List<IntColumnSpecWrapper> columnSpecs) {
        this(columnSpecs, DEFAULT_PREFIX);
    }

    TableAliaser(List<IntColumnSpecWrapper> columnSpecs, String prefix) {
        this.prefix = prefix;
        this.indices = computeReferenceIndices(columnSpecs);
    }

    int getIndex(ColumnSpec columnSpec) {
        if (indices.containsKey(columnSpec)) {
            return indices.get(columnSpec);
        } else {
            return -1;
        }
    }

    String generateTableReference(ColumnSpec columnSpec) {
        return prefix + getIndex(columnSpec);
    }

    String generateColumnReference(ColumnSpec columnSpec) {
        return generateTableReference(columnSpec) + "."
                + columnSpec.getColumn();
    }

    String generateColumnReferenceWithOp(ColumnSpec columnSpec) {
        StringBuilder result = new StringBuilder();
        result.append(generateColumnReference(columnSpec));
        if (columnSpec.getColumnOp() != null) {
            switch (columnSpec.getColumnOp()) {
                case UPPER:
                    result.append("UPPER");
                    break;
                default:
                    throw new AssertionError("invalid column op: " + columnSpec.getColumnOp());
            }
            result.insert(0, '(');
            result.append(')');
        }

        return result.toString();
    }

    private Map<ColumnSpec, Integer> computeReferenceIndices(
            List<IntColumnSpecWrapper> columnSpecs) {

        Map<ColumnSpec, Integer> tempIndices = new HashMap<>();

        int index = 1;
        JoinSpec currentJoin = null;
        boolean begin = true;
        Logger logger = SQLGenUtil.logger();
        logger.log(Level.SEVERE, "columnSpecs: {0}", columnSpecs);
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            IntColumnSpecWrapper columnSpec = columnSpecs.get(j);
            /*
             * Only generate a table if we're the first table or there is an
             * inbound join.
             */
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                int previousInstanceIndex = -1;
                // if there's no inbound join, then try to reuse an
                // earlier instance.
                if (currentJoin == null) {
                    previousInstanceIndex = findPreviousInstance(0, j,
                            columnSpecs, columnSpec);
                    logger.log(Level.FINEST, "previousInstanceIndex 1: {0}",
                        previousInstanceIndex);
                } else {
                    // If there's an inbound join and an earlier instance, then
                    // use an earlier version only if the inbound join of the
                    // earlier
                    // instance is the same
//                    int startIndex = 0;
//                    int cs2i = -1;
//                    do {
//                        cs2i = findPreviousInstance(startIndex, j, columnSpecs,
//                                columnSpec);
//                        startIndex = cs2i + 1;
//                        if (cs2i >= 0) {
//                            logger.log(Level.FINEST, 
//                                    "found previous instance at {0}", cs2i);
//                            for (int k = 0; k < cs2i; k++) {
//                                ColumnSpec csPrev = columnSpecs.get(k);
//                                JoinSpec prevJoin = csPrev.getJoin();
//                                if (currentJoin.isSameJoin(prevJoin)) {
//                                    previousInstanceIndex = cs2i;
//                                    logger.log(Level.FINEST,
//                                            "setting previousInstanceIndex={0}",
//                                            previousInstanceIndex);
//                                }
//                            }
//                        }
//                    } while (cs2i >= 0);
//                    logger.log(Level.FINEST,
//                            "previousInstanceIndex 2: {0}",
//                            previousInstanceIndex);
                }
                // If we found an earlier instance, then use its index otherwise
                // assign it a new index.
                if (previousInstanceIndex >= 0) {
                    IntColumnSpecWrapper previousInstance = columnSpecs
                            .get(previousInstanceIndex);
                    assert tempIndices.containsKey(previousInstance) : 
                            "doesn't contain columnSpec " + previousInstance;
                    int prevIndex = tempIndices.get(previousInstance);
                    tempIndices.put(columnSpec.getColumnSpec(), prevIndex);
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST,
                                "assigning {0} to previous index {1}",
                                new Object[]{columnSpec.getTable(), prevIndex});
                    }
                } else {
                    tempIndices.put(columnSpec.getColumnSpec(), index++);
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "assigning {0} to {1}",
                                new Object[]{columnSpec.getTable(), index - 1});
                    }
                }
                begin = false;
            }

            if (columnSpec.getJoin() != null) {
                currentJoin = columnSpec.getJoin();
            } else {
                currentJoin = null;
                begin = true;
            }
        }

        return Collections.unmodifiableMap(tempIndices);
    }

    private int findPreviousInstance(int i, int j,
            List<IntColumnSpecWrapper> columnSpecs, IntColumnSpecWrapper columnSpec) {
        for (; i < j; i++) {
            IntColumnSpecWrapper columnSpec2 = columnSpecs.get(i);
            if (columnSpec.isSameSchemaAndTable(columnSpec2)) {
                return i;
            }
        }
        return -1;
    }
}
