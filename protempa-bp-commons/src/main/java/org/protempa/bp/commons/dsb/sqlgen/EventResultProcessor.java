package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DataSourceType;
import org.protempa.bp.commons.dsb.PositionFormat;
import org.protempa.proposition.Event;
import org.protempa.proposition.Interval;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

class EventResultProcessor extends AbstractMainResultProcessor<Event> {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private static final int FLUSH_SIZE = 100000;

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<Event> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        boolean hasRefs = entitySpec.getReferenceSpecs().length > 0;
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
        int startColumnType = -1;
        int finishColumnType = -1;
        String[] uniqueIds =
                new String[entitySpec.getUniqueIdSpecs().length];
        DataSourceType dsType =
                DataSourceBackendDataSourceType.getInstance(getDataSourceBackendId());
        PositionFormat positionParser = entitySpec.getPositionParser();
        while (resultSet.next()) {
            int i = 1;
            String keyId = resultSet.getString(i++);


            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifier = generateUniqueIdentifier(
                    entitySpec.getName(), uniqueIds);

            String propId = null;
            if (!isCasePresent()) {
                if (codeSpec == null) {
                    assert propIds.length == 1: "Don't know which proposition id to assign to";
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
                if (finishColumnType == -1) {
                    finishColumnType = resultSetMetaData.getColumnType(i);
                }
                Long d = null;
                try {
                    d = positionParser.toLong(resultSet, i, finishColumnType);
                    i++;
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse timestamp. Leaving the start time/timestamp unset.",
                            e);
                }
                interval = intervalFactory.getInstance(d, gran);
            } else {
                if (startColumnType == -1) {
                    startColumnType = resultSetMetaData.getColumnType(i);
                }
                Long start = null;
                try {
                    start = positionParser.toLong(resultSet, i,
                            startColumnType);
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse start time. Leaving the start time/timestamp unset.",
                            e);
                } finally {
                    i++;
                }
                if (finishColumnType == -1) {
                    finishColumnType = resultSetMetaData.getColumnType(i);
                }
                Long finish = null;
                try {
                    finish = positionParser.toLong(resultSet, i,
                            finishColumnType);
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

            i = extractPropertyValues(propertySpecs, resultSet, i, 
                    propertyValues);

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }

            Event event = new Event(propId);
            event.setDataSourceType(dsType);
            event.setUniqueIdentifier(uniqueIdentifier);
            event.setInterval(interval);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                event.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            logger.log(Level.FINEST, "Created event {0}", event);
            results.add(keyId, event);
            if (++count % FLUSH_SIZE == 0) {
                results.flush(hasRefs);
            }
        }
        results.flush(hasRefs);
    }
}
