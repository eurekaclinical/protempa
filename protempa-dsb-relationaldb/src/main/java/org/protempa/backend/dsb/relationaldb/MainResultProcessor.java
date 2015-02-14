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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

abstract class MainResultProcessor<P extends Proposition>
        extends AbstractResultProcessor {

    private ResultCache<P> results;
    private ColumnSpec[] lastColumnSpecs;
    private PropertySpec[] propertySpecs;
    
    protected MainResultProcessor(ResultCache<P> results,
            EntitySpec entitySpec, String dataSourceBackendId) {
        super(entitySpec, dataSourceBackendId);
        assert results != null : "resultCache cannot be null";
        this.results = results;
        this.propertySpecs = getEntitySpec().getPropertySpecs();
        this.lastColumnSpecs = new ColumnSpec[this.propertySpecs.length];
    }
    
    final ResultCache<P> getResults() {
        return this.results;
    }

    protected static String sqlCodeToPropositionId(ColumnSpec codeSpec,
            String code) throws SQLException {
        return codeSpec.getTarget(code);
    }

    protected int extractPropertyValues(ResultSet resultSet, int i, 
            Value[] propertyValues, int[] colTypes) throws SQLException {
        for (int j = 0; j < this.propertySpecs.length; j++) {
            PropertySpec propertySpec = this.propertySpecs[j];
            ValueType valueType = propertySpec.getValueType();
            JDBCValueFormat valueFormat = propertySpec.getJDBCValueFormat();
            Value value;
            if (valueFormat != null) {
                value = valueFormat.toValue(resultSet, i, colTypes[i - 1]);
            } else {
                ColumnSpec columnSpec = this.lastColumnSpecs[j];
                if (columnSpec == null) {
                    ColumnSpec cs = propertySpec.getCodeSpec();
                    List<ColumnSpec> codeSpecL = cs.asList();
                    columnSpec = codeSpecL.get(codeSpecL.size() - 1);
                    this.lastColumnSpecs[j] = columnSpec;
                }
                String valAsString = resultSet.getString(i);
                String propId = columnSpec.getTarget(valAsString);
                if (propId != null) {
                    valAsString = propId;
                }
                value = valueType.parse(valAsString);
            }
            i++;
            propertyValues[j] = value;
        }
        return i;
    }
}
