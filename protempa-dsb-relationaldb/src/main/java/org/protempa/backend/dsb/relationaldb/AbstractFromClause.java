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

abstract class AbstractFromClause implements FromClause {

    private final EntitySpec currentSpec;
    private final List<ColumnSpec> columnSpecs;
    private final TableAliaser referenceIndices;

    protected AbstractFromClause(EntitySpec currentSpec,
            List<ColumnSpec> columnSpecs, TableAliaser referenceIndices) {
        this.columnSpecs = Collections.unmodifiableList(columnSpecs);
        this.referenceIndices = referenceIndices;
        this.currentSpec = currentSpec;
    }

    protected EntitySpec getCurrentSpec() {
        return currentSpec;
    }

    protected List<ColumnSpec> getColumnSpecs() {
        return columnSpecs;
    }

    protected TableAliaser getReferenceIndices() {
        return referenceIndices;
    }

    protected abstract JoinClause getJoinClause(JoinSpec.JoinType joinType);

    protected abstract OnClause getOnClause(JoinSpec joinSpec,
            TableAliaser referenceIndices);

    public String generateClause() {
        Map<Integer, ColumnSpec> columnSpecCache = new HashMap<>();
        StringBuilder fromPart = new StringBuilder("FROM ");
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);

            JoinSpec currentJoin = null;

            /*
             * To find something to join to, first we see if there is a join to
             * it.
             */
            for (int k = j - 1; k >= 0; k--) {
                ColumnSpec prevColumnSpec = columnSpecs.get(k);
                JoinSpec js = prevColumnSpec.getJoin();
                if (js != null && js.getNextColumnSpec() == columnSpec) {
                    currentJoin = js;
                    break;
                }
            }

            /*
             * Next, if there is not a join, we see if there is a join specified
             * to another column spec with the same schema and table as this
             * one.
             */
            if (currentJoin == null) {
                for (int k = 0; k < j; k++) {
                    ColumnSpec prevColumnSpec = columnSpecs.get(k);
                    JoinSpec js = prevColumnSpec.getJoin();
                    if (js != null
                            && js.getNextColumnSpec().isSameSchemaAndTable(
                                    columnSpec)) {
                        currentJoin = js;
                        break;
                    }
                }
            }

            Integer i = referenceIndices.getIndex(columnSpec);
            if (i >= 0 && !columnSpecCache.containsKey(i)) {
                assert begin || currentJoin != null : "No 'on' clause can be generated for "
                        + columnSpec + " because there is no incoming join.";
                if (!begin) {
                    fromPart.append(getJoinClause(currentJoin.getJoinType())
                            .generateClause());
                }
                fromPart.append(generateFromTable(columnSpec));
                fromPart.append(' ');
                columnSpecCache.put(i, columnSpec);

                if (currentJoin != null) {
                    fromPart.append(getOnClause(currentJoin, referenceIndices)
                            .generateClause());
                }
                begin = false;
            }
        }
        return fromPart.toString();
    }

    protected abstract String generateFromTable(ColumnSpec columnSpec);

}
