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

abstract class AbstractInClause implements InClause {

    private final ColumnSpec columnSpec;
    private final Object[] elements;
    private final boolean not;
    private final TableAliaser referenceIndices;

    AbstractInClause(ColumnSpec columnSpec, Object[] elements, boolean not,
            TableAliaser referenceIndices) {
        this.columnSpec = columnSpec;
        this.elements = elements;
        this.not = not;
        this.referenceIndices = referenceIndices;
    }

    @Override
    public String generateClause() {
        StringBuilder result = new StringBuilder();

        result.append(referenceIndices.generateColumnReference(columnSpec));
        if (not) {
            result.append(" NOT");
        }
        result.append(" IN (");
        for (int k = 0; k < elements.length; k++) {
            Object sqlCode = elements[k];
            result.append(SqlGeneratorUtil.prepareValue(sqlCode));
            if (k + 1 < elements.length) {
                result.append(',');
            }
        }
        result.append(')');

        return result.toString();
    }

}
