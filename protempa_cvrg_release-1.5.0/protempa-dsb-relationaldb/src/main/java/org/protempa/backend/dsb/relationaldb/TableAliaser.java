/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TableAliaser {

    private static final String DEFAULT_PREFIX = "a";
    
    private final String prefix;
    private final Map<ColumnSpec, Integer> indices;

    TableAliaser(List<ColumnSpec> columnSpecs) {
        this(columnSpecs, DEFAULT_PREFIX);
    }

    TableAliaser(List<ColumnSpec> columnSpecs, String prefix) {
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
            List<ColumnSpec> columnSpecs) {

        Map<ColumnSpec, Integer> tempIndices = new HashMap<ColumnSpec, Integer>();

        int index = 1;
        JoinSpec currentJoin = null;
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);
            /*
             * Only generate a table if we're the first table or there is an
             * inbound join.
             */
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                int previousInstanceIndex = -1;
                // if there's no inbound join, then don't try to reuse an
                // earlier instance.
                if (currentJoin == null/* || columnSpec.getJoin() != null */) {
                    previousInstanceIndex = findPreviousInstance(0, j,
                            columnSpecs, columnSpec);
                    // System.out.println("previousInstanceIndex 1: " +
                    // previousInstanceIndex);
                } else {
                    // If there's an inbound join and an earlier instance, then
                    // use an earlier version only if the inbound join of the
                    // earlier
                    // instance is the same
                    int startIndex = 0;
                    int cs2i = -1;
                    do {
                        cs2i = findPreviousInstance(startIndex, j, columnSpecs,
                                columnSpec);
                        startIndex = cs2i + 1;
                        if (cs2i >= 0) {
                            // System.out.println("found previous instance at "
                            // + cs2i);
                            for (int k = 0; k < cs2i; k++) {
                                ColumnSpec csPrev = columnSpecs.get(k);
                                JoinSpec prevJoin = csPrev.getJoin();
                                if (currentJoin.isSameJoin(prevJoin)) {
                                    previousInstanceIndex = cs2i;
                                    // System.out.println("setting previousIstanceIndex="
                                    // + previousInstanceIndex);
                                }
                            }
                        }
                    } while (cs2i >= 0);
                    // System.out.println("previousInstanceIndex 2: " +
                    // previousInstanceIndex);
                }
                // If we found an earlier instance, then use its index otherwise
                // assign it a new index.
                if (previousInstanceIndex >= 0) {
                    ColumnSpec previousInstance = columnSpecs
                            .get(previousInstanceIndex);
                    assert tempIndices.containsKey(previousInstance) : "doesn't contain columnSpec "
                            + previousInstance;
                    int prevIndex = tempIndices.get(previousInstance);
                    tempIndices.put(columnSpec, prevIndex);
                    // System.err.println("assigning " + columnSpec.getTable() +
                    // " to  previous index " + prevIndex);
                } else {
                    tempIndices.put(columnSpec, index++);
                    // System.err.println("assigning " + columnSpec.getTable() +
                    // " to " + (index - 1));
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
            List<ColumnSpec> columnSpecs, ColumnSpec columnSpec) {
        for (; i < j; i++) {
            ColumnSpec columnSpec2 = columnSpecs.get(i);
            if (columnSpec.isSameSchemaAndTable(columnSpec2)) {
                return i;
            }
        }
        return -1;
    }
}
