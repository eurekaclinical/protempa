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

public abstract class AbstractOnClause implements OnClause {
    private final JoinSpec joinSpec;
    private final TableAliaser referenceIndices;

    protected AbstractOnClause(JoinSpec joinSpec, TableAliaser referenceIndices) {
        this.joinSpec = joinSpec;
        this.referenceIndices = referenceIndices;
    }

    @Override
    public String generateClause() {
        String onClause = joinSpec.getOnClause();
        
        if (onClause != null) {
            return "ON (" + onClause + ") ";
        } else {
            String fromRef = referenceIndices.generateTableReference(joinSpec
                        .getPrevColumnSpec());
            String toRef = referenceIndices.generateTableReference(joinSpec
                        .getNextColumnSpec());
            return new StringBuilder("ON (")
                    .append(fromRef)
                    .append('.')
                    .append(joinSpec.getFromKey())
                    .append(" = ")
                    .append(toRef)
                    .append('.')
                    .append(joinSpec.getToKey())
                    .append(") ").toString();
        }
    }

}
