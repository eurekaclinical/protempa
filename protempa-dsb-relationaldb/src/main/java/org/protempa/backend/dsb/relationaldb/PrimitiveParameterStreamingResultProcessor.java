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
import org.protempa.DataSourceBackendSourceSystem;
import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class PrimitiveParameterStreamingResultProcessor extends StreamingMainResultProcessor<PrimitiveParameter> {
    
    private static final DataStreamingEventIterator<UniqueIdPair> 
            EMPTY_UNIQUE_ID_PAIR_ITR = new EmptyDataStreamingEventIterator<>();
    private static final DataStreamingEventIterator<PrimitiveParameter>
            EMPTY_PRIMPARAM_ITR = new EmptyDataStreamingEventIterator<>();

    private PrimParamIterator itr;
    private final Set<String> queryPropIds;

    PrimitiveParameterStreamingResultProcessor(
            RelationalDbDataSourceBackend backend,
            EntitySpec entitySpec, LinkedHashMap<String, ReferenceSpec> inboundRefSpecs,
            Map<String, ReferenceSpec> bidirectionalRefSpecs, String dataSourceBackendId,
            Set<String> propIds) {
        super(backend, entitySpec, inboundRefSpecs, bidirectionalRefSpecs,
                dataSourceBackendId);
        assert propIds != null : "propIds cannot be null";
        this.queryPropIds = propIds;
    }

    class PrimParamIterator extends PropositionResultSetIterator<PrimitiveParameter> {

        private final Logger logger;
        private final DataSourceBackendSourceSystem dsType;
        private final Date now;
        

        PrimParamIterator(Statement statement, ResultSet resultSet, 
                EntitySpec entitySpec, Map<String, ReferenceSpec> inboundRefSpecs, Map<String,
                ReferenceSpec> bidirectionalRefSpecs, InboundReferenceResultSetIterator referenceIterator)
                throws SQLException {
            super(getBackend(), statement, resultSet, entitySpec, inboundRefSpecs,
                    bidirectionalRefSpecs,
                    getDataSourceBackendId(), referenceIterator);
            this.logger = SQLGenUtil.logger();
            this.dsType = DataSourceBackendSourceSystem.getInstance(getDataSourceBackendId());
            this.now = new Date();
        }

        @Override
        void doProcess(ResultSet resultSet,
                String[] uniqueIds, ColumnSpec codeSpec, EntitySpec entitySpec,
                Map<String, ReferenceSpec> bidirectionalRefSpecs,
                int[] columnTypes, String[] propIds, PropertySpec[] propertySpecs,
                Value[] propertyValues, UniqueIdPair[] refUniqueIds)
                throws SQLException {
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

            Long timestamp = null;
            try {
                timestamp = entitySpec.getPositionParser().toPosition(resultSet,
                        i, columnTypes[i - 1]);
                i++;
            } catch (SQLException e) {
                logger.log(Level.WARNING,
                        "Could not parse timestamp. Leaving timestamp unset.", e);
            }

            ValueType valueType = entitySpec.getValueType();
            String cpValStr = resultSet.getString(i++);
            Value cpVal = valueType.parse(cpValStr);

            i = extractPropertyValues(resultSet, i,
                    propertyValues, columnTypes);
            i = extractReferenceUniqueIdPairs(resultSet, uniqueId,
                    refUniqueIds, i);
            this.getReferenceIterator().addUniqueIds(kId, refUniqueIds);

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }
            
            if (!queryPropIds.contains(propId)) {
                return;
            }

            PrimitiveParameter p = new PrimitiveParameter(propId, uniqueId);
            p.setPosition(timestamp);
            p.setGranularity(entitySpec.getGranularity());
            p.setValue(cpVal);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                p.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            p.setSourceSystem(this.dsType);
            if (entitySpec.getCreateDateSpec() != null) {
                p.setCreateDate(resultSet.getTimestamp(i++));
            }
            if (entitySpec.getUpdateDateSpec() != null) {
                p.setUpdateDate(resultSet.getTimestamp(i++));
            }
            if (entitySpec.getDeleteDateSpec() != null) {
                p.setDeleteDate(resultSet.getTimestamp(i++));
            }
            p.setDownloadDate(this.now);
            handleProposition(p);

            logger.log(Level.FINEST, "Created primitive parameter {0}", p);
        }

        @Override
        void fireResultSetCompleted() {
            this.getReferenceIterator().resultSetComplete();
        }
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        this.itr = new PrimParamIterator(getStatement(), resultSet,
                entitySpec, getInboundRefSpecs(), getBidirectionalRefSpecs(),
                new InboundReferenceResultSetIterator(entitySpec.getName()));
    }

    @Override
    final DataStreamingEventIterator<PrimitiveParameter> getResults() {
        if (this.itr != null) {
            return this.itr;
        } else {
            return EMPTY_PRIMPARAM_ITR;
        }
    }

    @Override
    DataStreamingEventIterator<UniqueIdPair> getInboundReferenceResults() {
        if (this.itr != null) {
            return this.itr.getReferenceIterator();
        } else {
            return EMPTY_UNIQUE_ID_PAIR_ITR;
        }
    }
}
