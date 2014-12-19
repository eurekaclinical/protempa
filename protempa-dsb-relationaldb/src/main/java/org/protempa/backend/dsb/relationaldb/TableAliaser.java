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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.builder.ToStringBuilder;

final class TableAliaser {

    private static final String DEFAULT_PREFIX = "a";

    private final String prefix;
    private Map<ColumnSpec, Integer> indices;

    TableAliaser(List<IntColumnSpecWrapper> columnSpecs) {
        this(columnSpecs, DEFAULT_PREFIX);
    }

    TableAliaser(List<IntColumnSpecWrapper> columnSpecs, String prefix) {
        this.prefix = prefix;
        assert !columnSpecs.isEmpty() : "columnSpecs cannot be empty";
        computeReferenceIndices(columnSpecs);
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
        String expr = columnSpec.getExpr();
        if (expr == null) {
            expr = generateTableReference(columnSpec) + "."
                + columnSpec.getColumn();
        }
        return expr;
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

    private void computeReferenceIndices(List<IntColumnSpecWrapper> columnSpecs) {
        Map<ColumnSpec, Integer> tempIndices = new LinkedHashMap<>();

        int index = 1;
        JoinSpec currentJoin = null;
        List<ColumnSpec> baseSpecs
                = columnSpecs.get(0).getColumnSpec().asList();
        boolean first = true;
        for (IntColumnSpecWrapper columnSpec : columnSpecs) {
            if (columnSpec.getColumnSpec().getExpr() == null) {
                if (currentJoin == null && !first) {
                    ColumnSpec cs = columnSpec.getIsSameAs();
                    //ColumnSpec cs = null;
                    if (cs == null) {
                        for (ColumnSpec bs : baseSpecs) {
                            if (bs.isSameSchemaAndTable(columnSpec.getColumnSpec())) {
                                cs = bs;
                                break;
                            }
                        }
                    }
                    if (cs != null) {
                        Integer previousInstanceIndex = tempIndices.get(cs);
                        tempIndices.put(columnSpec.getColumnSpec(), previousInstanceIndex);
                    }
                } else {
                    tempIndices.put(columnSpec.getColumnSpec(), index++);
                    first = false;
                }
            }
            currentJoin = columnSpec.getJoin();
        }
        SQLGenUtil.logger().log(Level.SEVERE, "table aliases: {0}", tempIndices);
        this.indices = tempIndices;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
