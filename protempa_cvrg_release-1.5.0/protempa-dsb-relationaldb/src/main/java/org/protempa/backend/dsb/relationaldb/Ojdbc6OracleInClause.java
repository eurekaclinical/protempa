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


final class Ojdbc6OracleInClause extends AbstractInClause {
    
    private final ColumnSpec columnSpec;
    private final Object[] elements;
    private final boolean not;
    private final TableAliaser referenceIndices;
    
    Ojdbc6OracleInClause(ColumnSpec columnSpec, Object[] elements,
            boolean not, TableAliaser referenceIndices) {
        super(columnSpec, elements, not, referenceIndices);
        
        this.columnSpec = columnSpec;
        this.elements = elements;
        this.not = not;
        this.referenceIndices = referenceIndices;
    }

    /**
     * Oracle doesn't allow more than 1000 elements in an IN clause, so if we
     * want more than 1000 we create multiple IN clauses chained together by OR.
     */
    @Override
    public String generateClause() {
        StringBuilder wherePart = new StringBuilder();
        wherePart.append(referenceIndices.generateColumnReference(columnSpec));
        if (not) {
            wherePart.append(" NOT");
        }
        wherePart.append(" IN (");
        for (int k = 0; k < elements.length; k++) {
            Object val = elements[k];
            wherePart.append(SqlGeneratorUtil.prepareValue(val));
            if (k + 1 < elements.length) {
                if ((k + 1) % 1000 == 0) {
                    wherePart.append(") OR ");
                    wherePart.append(referenceIndices.generateColumnReference(columnSpec));
                    wherePart.append(" IN (");
                } else {
                    wherePart.append(',');
                }
            }
        }
        wherePart.append(')');
        
        return wherePart.toString();
    }
}
