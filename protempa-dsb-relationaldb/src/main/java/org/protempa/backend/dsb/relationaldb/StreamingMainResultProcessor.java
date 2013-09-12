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

import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

abstract class StreamingMainResultProcessor<P extends Proposition>
        extends AbstractResultProcessor implements StreamingResultProcessor<P> {
    private ColumnSpec[] lastColumnSpecs;
    private PropertySpec[] propertySpecs;
    private SortedMap<String, ReferenceSpec> inboundRefSpecs;
    private Map<String, ReferenceSpec> bidirectionalRefSpecs;
    private Statement statement;
    
    protected StreamingMainResultProcessor(
            EntitySpec entitySpec, SortedMap<String,
            ReferenceSpec> inboundRefSpecs,
            Map<String, ReferenceSpec> bidirectionalRefSpecs,
            String dataSourceBackendId) {
        super(entitySpec, dataSourceBackendId);
        this.propertySpecs = getEntitySpec().getPropertySpecs();
        this.inboundRefSpecs = inboundRefSpecs;
        this.bidirectionalRefSpecs = bidirectionalRefSpecs;
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

    protected int extractReferenceUniqueIdPairs(
            ResultSet resultSet, UniqueId referredToUniqueId,
            UniqueIdPair[] uniqueIdPairs, int i) throws SQLException {
        int j = 0;
        for (Map.Entry<String, ReferenceSpec> entry : inboundRefSpecs
                .entrySet()) {
            String referringEntityName = entry.getKey();
            ReferenceSpec refSpec = entry.getValue();
            String[] referringUniqueIds = new String[refSpec.getReferringEntitySpec()
                    .getUniqueIdSpecs().length];
            i = readUniqueIds(referringUniqueIds, resultSet, i);
            UniqueId referringUniqueId = generateUniqueId(referringEntityName,
                    referringUniqueIds);
            UniqueIdPair pair = new UniqueIdPair(refSpec.getReferenceName(),
                    referringUniqueId, referredToUniqueId);
            uniqueIdPairs[j] = pair;

            for (Map.Entry<String, ReferenceSpec> bidirRefSpec :
                    bidirectionalRefSpecs.entrySet()) {
                if (bidirRefSpec.getKey().equals(referringEntityName)) {
                    UniqueIdPair reversePair = new UniqueIdPair(bidirRefSpec
                            .getValue().getReferenceName(),
                            referredToUniqueId, referringUniqueId);
                    j++;
                    uniqueIdPairs[j] = reversePair;
                }
            }
            j++;
        }
        return i;
    }

    abstract DataStreamingEventIterator<P> getResults();

    abstract  DataStreamingEventIterator<UniqueIdPair>
            getInboundReferenceResults();

    protected SortedMap<String, ReferenceSpec> getInboundRefSpecs() {
        return this.inboundRefSpecs;
    }

    protected Map<String, ReferenceSpec> getBidirectionalRefSpecs() {
        return this.bidirectionalRefSpecs;
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
