package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.DatabaseDataSourceType;
import org.protempa.proposition.Event;
import org.protempa.proposition.Interval;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

class EventResultProcessor extends AbstractMainResultProcessor<Event> {
    private static final IntervalFactory intervalFactory = new IntervalFactory();

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        ResultCache<Event> results = getResults();
        EntitySpec entitySpec = getEntitySpec();
        String[] propIds = entitySpec.getPropositionIds();
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            codeSpec = codeSpecL.get(codeSpecL.size() - 1);
        }
        Logger logger = SQLGenUtil.logger();
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Value[] propertyValues = new Value[propertySpecs.length];
        while (resultSet.next()) {
            int i = 1;
            String keyId = resultSet.getString(i++);

            String[] uniqueIds = 
                    new String[entitySpec.getUniqueIdSpecs().length];
            i = readUniqueIds(uniqueIds, resultSet, i);
            UniqueIdentifier uniqueIdentifier = generateUniqueIdentifier(
                    entitySpec.getName(), uniqueIds);

            String propId = null;
            if (!isCasePresent()) {
                if (codeSpec == null) {
                    propId = propIds[0];
                } else {
                    String code = resultSet.getString(i++);
                    propId = sqlCodeToPropositionId(codeSpec, code);
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
                    d = entitySpec.getPositionParser().toLong(resultSet, i++);
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse timestamp. Leaving the finish time unset.",
                            e);
                }
                interval = intervalFactory.getInstance(d, gran);
            } else {
                Long start = null;
                try {
                    start = entitySpec.getPositionParser().toLong(resultSet,
                            i++);
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse start time. Leaving the start time/timestamp unset.",
                            e);
                }
                Long finish = null;
                try {
                    finish = entitySpec.getPositionParser().toLong(resultSet,
                            i++);
                } catch (SQLException e) {
                    logger.log(
                            Level.WARNING,
                            "Could not parse start time. Leaving the finish time unset.",
                            e);
                }
                try {
                    interval = intervalFactory.getInstance(start, gran, finish,
                            gran);
                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING, "Finish is before start", e);
                    interval = intervalFactory.getInstance(null, gran, null,
                            gran);
                }
            }
            
            i = extractPropertyValues(propertySpecs, resultSet, i, propertyValues);

            if (isCasePresent()) {
                propId = resultSet.getString(i++);
            }

            Event event = new Event(propId);
            event.setDataSourceType(DatabaseDataSourceType
                    .getInstance(getDataSourceBackendId()));
            event.setUniqueIdentifier(uniqueIdentifier);
            event.setInterval(interval);
            for (int j = 0; j < propertySpecs.length; j++) {
                PropertySpec propertySpec = propertySpecs[j];
                event.setProperty(propertySpec.getName(), propertyValues[j]);
            }
            logger.log(Level.FINEST, "Created event {0}", event);
            List<Event> propList = results.getPatientPropositions(keyId);
            if (propList == null) {
                propList = new ArrayList<Event>(500);
            }
            propList.add(event);
            results.put(keyId, propList);
        }
    }
}
