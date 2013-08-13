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

import org.protempa.DataStreamingEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DataStreamingEventIterator;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

class EventStreamingResultProcessor extends StreamingMainResultProcessor<Event> {

    private DataStreamingEventIterator<Event> itr;

    EventStreamingResultProcessor(
            EntitySpec entitySpec, ReferenceSpec[] inboundRefSpecs, String dataSourceBackendId) {
        super(entitySpec, inboundRefSpecs, dataSourceBackendId);
    }

    class EventIterator extends PropositionResultSetIterator<Event> {

        private final Logger logger;
        private final DataSourceBackendDataSourceType dsType;
        private final IntervalFactory intervalFactory;
        private final JDBCPositionFormat positionParser;

        EventIterator(Statement statement, ResultSet resultSet, 
                EntitySpec entitySpec, ReferenceSpec[] inboundRefSpecs) throws SQLException {
            super(statement, resultSet, entitySpec, inboundRefSpecs, getDataSourceBackendId());
            this.logger = SQLGenUtil.logger();
            this.dsType = DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
            this.intervalFactory = new IntervalFactory();
            this.positionParser = entitySpec.getPositionParser();
        }

        @Override
        void doProcess(ResultSet resultSet,
                String[] uniqueIds, ColumnSpec codeSpec, EntitySpec entitySpec,
                int[] columnTypes, String[] propIds, PropertySpec[] propertySpecs,
                Value[] propertyValues, ReferenceSpec[] inboundRefSpecs) throws SQLException {
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
                    return;
                }
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

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }

            Event event = new Event(propId, uniqueId);
            event.setDataSourceType(dsType);
            event.setInterval(interval);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                event.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            handleProposition(event);

            logger.log(Level.FINEST, "Created event {0}", event);
        }
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        EntitySpec entitySpec = getEntitySpec();
        this.itr = new EventIterator(getStatement(), resultSet, entitySpec, getInboundRefSpecs());
    }

    @Override
    final DataStreamingEventIterator<Event> getResults() {
        return this.itr;
    }
}
