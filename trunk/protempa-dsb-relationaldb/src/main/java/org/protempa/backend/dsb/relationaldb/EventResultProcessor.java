/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;

import org.arp.javautil.log.Logging;
import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DataSourceType;
import org.protempa.proposition.Event;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

class EventResultProcessor extends MainResultProcessor<Event> {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private static final int FLUSH_SIZE = 1000000;

    EventResultProcessor(ResultCache<Event> results,
            EntitySpec entitySpec, String dataSourceBackendId) {
        super(results, entitySpec, dataSourceBackendId);
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<Event> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        String entitySpecName = entitySpec.getName();
        //boolean hasRefs = entitySpec.getReferenceSpecs().length > 0;
        String[] propIds = entitySpec.getPropositionIds();
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            codeSpec = codeSpecL.get(codeSpecL.size() - 1);
        }
        Logger logger = SQLGenUtil.logger();
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Value[] propertyValues = new Value[propertySpecs.length];
        int count = 0;
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int[] columnTypes = new int[resultSetMetaData.getColumnCount()];
        for (int i = 0; i < columnTypes.length; i++) {
            columnTypes[i] = resultSetMetaData.getColumnType(i + 1);
        }
        String[] uniqueIds =
                new String[entitySpec.getUniqueIdSpecs().length];
        DataSourceType dsType =
                DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
        JDBCPositionFormat positionParser = entitySpec.getPositionParser();

        while (resultSet.next()) {
            int i = 1;
            String keyId = resultSet.getString(i++);
            if (keyId == null) {
                logger.warning("A keyId is null. Skipping record.");
                continue;
            }

            i = readUniqueIds(uniqueIds, resultSet, i);
            if (Arrays.contains(uniqueIds, null)) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Unique ids contain null ({0}). Skipping record.",
                            StringUtils.join(uniqueIds, ", "));
                    continue;
                }
            }
            UniqueId uniqueId = generateUniqueId(entitySpecName, uniqueIds);

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
                        continue;
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
            logger.log(Level.FINEST, "Created event {0}", event);
            results.add(keyId, event);
            if (++count % FLUSH_SIZE == 0) {
                try {
                    results.flush(this);
                } catch (IOException ex) {
                    throw new QueryResultsCacheException("Flushing primitive parameters to cache failed", ex);
                }
                if (logger.isLoggable(Level.FINE)) {
                    Logging.logCount(logger, Level.FINE, count,
                            "Retrieved {0} record",
                            "Retrieved {0} records");
                }
            }
        }
        try {
            results.flush(this);
        } catch (IOException ex) {
            throw new QueryResultsCacheException("Flushing primitive parameters to cache failed", ex);
        }
        if (logger.isLoggable(Level.FINE)) {
            Logging.logCount(logger, Level.FINE, count,
                    "Retrieved {0} record total",
                    "Retrieved {0} records total");
        }
    }
}
