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
import java.sql.Statement;
import java.util.List;
import org.protempa.DataStreamingEventIterator;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

abstract class StreamingMainResultProcessor<P extends Proposition>
        extends AbstractResultProcessor implements StreamingResultProcessor<P> {
    private ColumnSpec[] lastColumnSpecs;
    private PropertySpec[] propertySpecs;
    private ReferenceSpec[] inboundRefSpecs;
    private Statement statement;
    
    protected StreamingMainResultProcessor(
            EntitySpec entitySpec, ReferenceSpec[] inboundRefSpecs, String dataSourceBackendId) {
        super(entitySpec, dataSourceBackendId);
        this.propertySpecs = getEntitySpec().getPropertySpecs();
        this.inboundRefSpecs = inboundRefSpecs;
        this.lastColumnSpecs = new ColumnSpec[this.propertySpecs.length];
    }

    protected static String sqlCodeToPropositionId(ColumnSpec codeSpec,
            String code) throws SQLException {
        return codeSpec.propositionIdFor(code);
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
                    ColumnSpec cs = propertySpec.getSpec();
                    List<ColumnSpec> codeSpecL = cs.asList();
                    columnSpec = codeSpecL.get(codeSpecL.size() - 1);
                    this.lastColumnSpecs[j] = columnSpec;
                }
                String valAsString = resultSet.getString(i);
                String propId = columnSpec.propositionIdFor(valAsString);
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

    protected int extractReferenceValues(ResultSet resultSet, int i, String[] refUniqueIds) {


        return i;
    }
    
    abstract DataStreamingEventIterator<P> getResults();

    protected ReferenceSpec[] getInboundRefSpecs() {
        return this.inboundRefSpecs;
    }

    @Override
    public void setStatement(Statement stmt) {
        this.statement = stmt;
    }
    
    @Override
    public Statement getStatement() {
        return this.statement;
    }
}
