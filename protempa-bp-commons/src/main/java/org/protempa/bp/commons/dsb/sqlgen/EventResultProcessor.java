package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.collections.Collections;
import org.protempa.DatabaseDataSourceType;
import org.protempa.proposition.Event;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

class EventResultProcessor extends AbstractMainResultProcessor<Event> {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        Map<String, List<Event>> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        Logger logger = SQLGenUtil.logger();
        while (resultSet.next()) {
            int i = 1;
            String keyId = resultSet.getString(i++);

            String[] uniqueIds = generateUniqueIdsArray(entitySpec);
            i += uniqueIds.length;
            
            String propId;
            String[] propIds = entitySpec.getPropositionIds();
            if (propIds.length == 1) {
                propId = propIds[0];
            } else {
                propId = resultSet.getString(i++);
            }
            i -=uniqueIds.length;
            Event event = new Event(propId);
            event.setDataSourceType(
                    new DatabaseDataSourceType(getDataSourceBackendId()));
            i = eventSetUniqueIdentifier(uniqueIds, entitySpec, resultSet, i,
                    event);
            Granularity gran = entitySpec.getGranularity();
            ColumnSpec finishTimeSpec = entitySpec.getFinishTimeSpec();
            if (finishTimeSpec == null) {
                try {
                    long d = entitySpec.getPositionParser().toLong(
                            resultSet, i++);
                    event.setInterval(intervalFactory.getInstance(d, gran));
                } catch (SQLException e) {
                    logger.log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                    continue;
                }
            } else {
                long start;
                try {
                    start = entitySpec.getPositionParser().toLong(
                            resultSet, i++);
                } catch (SQLException e) {
                    logger.log(Level.WARNING,
                            "Could not parse start time. Ignoring data value.",
                            e);
                    continue;
                }
                long finish;
                try {
                    finish = entitySpec.getPositionParser().toLong(resultSet,
                            i++);
                } catch (SQLException e) {
                    logger.log(Level.WARNING,
                            "Could not parse start time. Ignoring data value.",
                            e);
                    continue;
                }
                try {
                    event.setInterval(intervalFactory.getInstance(start, gran,
                            finish, gran));
                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING,
                            "Could not parse the time of event \'" + propId +
                            "\' because finish is before start.", e);
                    continue;
                }
            }
            PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
            for (PropertySpec propertySpec : propertySpecs) {
                ValueType vf = propertySpec.getValueType();
                Value value = ValueFactory.get(vf).parseValue(
                        resultSet.getString(i++));
                event.setProperty(propertySpec.getName(), value);
            }
            logger.log(Level.FINEST, "Created event {0}", event);
            Collections.putList(results, keyId, event);
        }
    }

    private int eventSetUniqueIdentifier(String[] uniqueIds,
            EntitySpec entitySpec,
            ResultSet resultSet, int i, Event event) throws SQLException {
        i = readUniqueIds(uniqueIds, resultSet, i);
        UniqueIdentifier uniqueIdentifer = generateUniqueIdentifier(entitySpec,
                uniqueIds);
        event.setUniqueIdentifier(uniqueIdentifer);
        return i;
    }
}
