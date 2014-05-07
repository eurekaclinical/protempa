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

import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConstantStreamingResultProcessor extends StreamingMainResultProcessor<Constant> {
    private static final DataStreamingEventIterator<UniqueIdPair> 
            EMPTY_UNIQUE_ID_PAIR_ITR = new EmptyDataStreamingEventIterator<>();
    private static final DataStreamingEventIterator<Constant>
            EMPTY_CONSTANT_ITR = new EmptyDataStreamingEventIterator<>();
    
    private ConstantIterator itr;

    ConstantStreamingResultProcessor(
            EntitySpec entitySpec, LinkedHashMap<String,ReferenceSpec> inboundRefSpecs,
            Map<String, ReferenceSpec> bidirectionalRefSpecs, String
            dataSourceBackendId) {
        super(entitySpec, inboundRefSpecs, bidirectionalRefSpecs,
                dataSourceBackendId);
    }

    class ConstantIterator extends PropositionResultSetIterator<Constant> {

        private final Logger logger;
        private final DataSourceBackendDataSourceType dsType;

        ConstantIterator(Statement statement, ResultSet resultSet, 
                EntitySpec entitySpec, LinkedHashMap<String,
                ReferenceSpec> inboundRefSpecs,
                Map<String, ReferenceSpec> bidirectionalRefSpecs, InboundReferenceResultSetIterator referenceIterator)
                throws SQLException {
            super(statement, resultSet, entitySpec, inboundRefSpecs,
                    bidirectionalRefSpecs, getDataSourceBackendId(), referenceIterator);
            this.logger = SQLGenUtil.logger();
            this.dsType = DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
        }

        @Override
        void doProcess(ResultSet resultSet,
                String[] uniqueIds, ColumnSpec codeSpec, EntitySpec entitySpec,
                Map<String, ReferenceSpec> bidirectionalRefSpecs,
                int[] columnTypes, String[] propIds, PropertySpec[] propertySpecs,
                Value[] propertyValues,
                UniqueIdPair[] refUniqueIds) throws SQLException {
            int i = 1;
            String kId = resultSet.getString(i++);
            if (kId == null) {
                logger.warning("A keyId is null. Skipping record.");
                return;
            }
            handleKeyId(kId);

            i = readUniqueIds(uniqueIds, resultSet, i);
            if (Arrays.contains(uniqueIds, null)) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Unique ids contain null ({0}). Skipping record.",
                            StringUtils.join(uniqueIds, ", "));
                }
                this.getReferenceIterator().addUniqueIds(kId, null);
                return;
            }
            UniqueId uniqueId = generateUniqueId(entitySpec.getName(), uniqueIds);

            String propId = null;
            if (!isCasePresent()) {
                if (codeSpec == null) {
                    assert propIds.length == 1 :
                            "Don't know which proposition id to assign to";
                    propId = propIds[0];
                } else {
                    String code = resultSet.getString(i++);
                    propId = sqlCodeToPropositionId(codeSpec, code);
                    if (propId == null) {
                        this.getReferenceIterator().addUniqueIds(kId, null);
                        return;
                    }
                }
            } else {
                i++;
            }

            i = extractPropertyValues(resultSet, i,
                    propertyValues, columnTypes);
            i = extractReferenceUniqueIdPairs(resultSet, uniqueId,
                    refUniqueIds, i);
            this.getReferenceIterator().addUniqueIds(kId, refUniqueIds);

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }

            Constant cp = new Constant(propId, uniqueId);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                cp.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            cp.setDataSourceType(dsType);
            handleProposition(cp);

            logger.log(Level.FINEST, "Created constant {0}", cp);
        }

        @Override
        void fireResultSetCompleted() {
            this.getReferenceIterator().resultSetComplete();
        }
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        this.itr = new ConstantIterator(getStatement(), resultSet,
                entitySpec, getInboundRefSpecs(), getBidirectionalRefSpecs(), new InboundReferenceResultSetIterator(entitySpec.getName()));
    }

    @Override
    final DataStreamingEventIterator<Constant> getResults() {
        if (this.itr != null) {
            return this.itr;
        } else {
            return EMPTY_CONSTANT_ITR;
        }
    }

    @Override
    final DataStreamingEventIterator<UniqueIdPair> getInboundReferenceResults() {
        if (this.itr != null) {
            return this.itr.getReferenceIterator();
        } else {
            return EMPTY_UNIQUE_ID_PAIR_ITR;
        }
    }
}
