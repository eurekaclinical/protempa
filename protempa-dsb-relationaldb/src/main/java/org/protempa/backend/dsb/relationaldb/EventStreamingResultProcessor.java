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
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class EventStreamingResultProcessor extends StreamingMainResultProcessor<Event> {
    private static final DataStreamingEventIterator<UniqueIdPair> 
            EMPTY_UNIQUE_ID_PAIR_ITR = new EmptyDataStreamingEventIterator<>();
    private static final DataStreamingEventIterator<Event>
            EMPTY_EVENT_ITR = new EmptyDataStreamingEventIterator<>();

    private EventIterator itr;
    private final Set<String> queryPropIds;
    

    EventStreamingResultProcessor(
            EntitySpec entitySpec, LinkedHashMap<String,
            ReferenceSpec> inboundRefSpecs, Map<String, ReferenceSpec>
            bidirectionalRefSpecs,
            String dataSourceBackendId, Set<String> propIds) {
        super(entitySpec, inboundRefSpecs, bidirectionalRefSpecs,
                dataSourceBackendId);
        assert propIds != null : "propIds cannot be null";
        this.queryPropIds = propIds;
    }

    class EventIterator extends PropositionResultSetIterator<Event> {

        private final Logger logger;
        private final DataSourceBackendSourceSystem dsType;
        private final IntervalFactory intervalFactory;
        private final JDBCPositionFormat positionParser;
        private EntitySpec entitySpec;
        private final Date now;
        
        EventIterator(Statement statement, ResultSet resultSet, 
                EntitySpec entitySpec, Map<String,
                ReferenceSpec> inboundRefSpecs, Map<String,
                ReferenceSpec> bidirectionalRefSpecs, InboundReferenceResultSetIterator referenceIterator)
                throws SQLException {
            super(statement, resultSet, entitySpec, inboundRefSpecs,
                    bidirectionalRefSpecs, getDataSourceBackendId(), referenceIterator);
            this.logger = SQLGenUtil.logger();
            this.dsType = DataSourceBackendSourceSystem.getInstance(getDataSourceBackendId());
            this.intervalFactory = new IntervalFactory();
            this.positionParser = entitySpec.getPositionParser();
            this.entitySpec = entitySpec;
            this.now = new Date();
        }

        @Override
        void doProcess(ResultSet resultSet,
                String[] uniqueIds, ColumnSpec codeSpec, EntitySpec entitySpec,
                Map<String, ReferenceSpec> bidirectionalRefSpec,
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

            ColumnSpec finishTimeSpec = entitySpec.getFinishTimeSpec();
            Granularity gran = entitySpec.getGranularity();
            Interval interval = null;
            if (finishTimeSpec == null) {
                Long d = null;
                try {
                    d = positionParser.toPosition(resultSet, i, columnTypes[i - 1]);
                    i++;
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse timestamp. Leaving the start time/timestamp unset.",
                            e);
                }
                interval = intervalFactory.getInstance(d, gran);
            } else {
                Long start = null;
                try {
                    start = positionParser.toPosition(resultSet, i,
                            columnTypes[i - 1]);
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse start time. Leaving the start time/timestamp unset.",
                            e);
                } finally {
                    i++;
                }
                Long finish = null;
                try {
                    finish = positionParser.toPosition(resultSet, i,
                            columnTypes[i - 1]);
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse start time. Leaving the finish time unset.",
                            e);
                } finally {
                    i++;
                }
                if (finish != null && start != null && finish.compareTo(start) < 0) {
                    logger.log(Level.WARNING, "Finish {0} is before start {1}: Leaving time unset",
                            new Object[]{finish, start});
                    interval = intervalFactory.getInstance(null, gran, null,
                            gran);
                } else {
                    interval = intervalFactory.getInstance(start, gran, finish,
                            gran);
                }
            }

            i = extractPropertyValues(resultSet, i, propertyValues,
                    columnTypes);
            i = extractReferenceUniqueIdPairs(resultSet, uniqueId,
                    refUniqueIds, i);

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }
            
            if (!queryPropIds.contains(propId)) {
                this.getReferenceIterator().addUniqueIds(kId, null);
                return;
            }
            
            this.getReferenceIterator().addUniqueIds(kId, refUniqueIds);

            Event event = new Event(propId, uniqueId);
            event.setSourceSystem(dsType);
            event.setInterval(interval);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                event.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            if (entitySpec.getCreateDateSpec() != null) {
                event.setCreateDate(resultSet.getTimestamp(i++));
            }
            if (entitySpec.getUpdateDateSpec() != null) {
                event.setUpdateDate(resultSet.getTimestamp(i++));
            }
            if (entitySpec.getDeleteDateSpec() != null) {
                event.setDeleteDate(resultSet.getTimestamp(i++));
            }
            event.setDownloadDate(this.now);
            handleProposition(event);

            logger.log(Level.FINEST, "Created event {0}", event);
        }

        @Override
        void fireResultSetCompleted() {
            this.getReferenceIterator().resultSetComplete();
        }
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        this.itr = new EventIterator(getStatement(), resultSet, entitySpec,
                getInboundRefSpecs(), getBidirectionalRefSpecs(),new InboundReferenceResultSetIterator(entitySpec.getName()) );
    }

    @Override
    final DataStreamingEventIterator<Event> getResults() {
        if (this.itr != null) {
            return this.itr;
        } else {
            return EMPTY_EVENT_ITR;
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
