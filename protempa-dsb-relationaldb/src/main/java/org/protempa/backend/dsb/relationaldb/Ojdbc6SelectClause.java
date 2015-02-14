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

import org.protempa.backend.dsb.relationaldb.mappings.Mappings;


final class Ojdbc6SelectClause extends AbstractSelectClause {

    Ojdbc6SelectClause(ColumnSpecInfo info, TableAliaser referenceIndices,
            EntitySpec entitySpec, boolean wrapKeyId) {
        super(info, referenceIndices, entitySpec, wrapKeyId);
    }

    @Override
    protected CaseClause getCaseClause(Object[] sqlCodes, ColumnSpec columnSpec,
            Mappings mappings) {
        return new DefaultCaseClause(sqlCodes, getReferenceIndices(), columnSpec,
                mappings);
    }

    @Override
    protected String wrapKeyIdInConversion(String columnStr) {
        return "to_char(" + columnStr + ")";
    }

}
