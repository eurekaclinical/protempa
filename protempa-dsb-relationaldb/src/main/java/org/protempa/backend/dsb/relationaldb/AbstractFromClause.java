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

import java.util.ArrayList;
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
    
    @Override
    public String generateClause() {
         StringBuilder fromPart = new StringBuilder("FROM ");
         boolean begin = true;
         JoinSpec currentJoin = null;
         for (ColumnSpec columnSpec : this.columnSpecs) {
             if (begin || currentJoin != null) {
                if (!begin) {
                    fromPart.append(getJoinClause(currentJoin.getJoinType())
                            .generateClause());
                }
                fromPart.append(generateFromTable(columnSpec));
                fromPart.append(' ');
                if (!begin) {
                    fromPart.append(
                            getOnClause(currentJoin, this.referenceIndices)
                            .generateClause());
                }
             }
             currentJoin = columnSpec.getJoin();
             begin = false;
         }
         return fromPart.toString();
    }
    
    protected abstract String generateFromTable(ColumnSpec columnSpec);

}
