package org.protempa.bp.commons.dsb;

import org.protempa.bp.commons.dsb.sqlgen.SQLGenerator;
import org.protempa.bp.commons.dsb.sqlgen.RelationalDatabaseSpec;
import org.protempa.bp.commons.dsb.sqlgen.PropertySpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.protempa.dsb.SchemaAdaptorInitializationException;

import org.protempa.proposition.Event;
import org.protempa.proposition.PointInterval;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.ValueFactory;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.DataSourceReadException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.SchemaAdaptorProperty;
import org.protempa.bp.commons.dsb.sqlgen.EntitySpec;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.PropositionUtil;

/**
 * Schema adaptor for the contrast reaction project.
 * 
 * @author Andrew Post
 */
public final class RelationalDatabaseSchemaAdaptor
        extends DriverManagerAbstractSchemaAdaptor {

    private String dbUrl;
    private String username;
    private String password;
    private String driverClass;
    private GranularityFactory granularityFactory;
    private UnitFactory unitFactory;
    private SQLGenerator sqlGenerator;

    @SchemaAdaptorProperty
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @SchemaAdaptorProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @SchemaAdaptorProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @SchemaAdaptorProperty
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public String getDbUrl() {
        return this.dbUrl;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getDriverClass() {
        return this.driverClass;
    }
    private final Map<String, PropertySpec> primitiveParameterSpecs;
    private final Map<String, PropertySpec> eventSpecs;
    private final Map<String, PropertySpec> constantSpecs;

    public RelationalDatabaseSchemaAdaptor(
            RelationalDatabaseSpec relationalDatabaseSpec) {
        this();
        if (relationalDatabaseSpec != null) {
            populatePropositionMap(this.primitiveParameterSpecs,
                    relationalDatabaseSpec.getPrimitiveParameterSpecs());
            populatePropositionMap(this.eventSpecs,
                    relationalDatabaseSpec.getEventSpecs());
            populatePropositionMap(this.constantSpecs,
                    relationalDatabaseSpec.getConstantParameterSpecs());
        }
        this.granularityFactory = relationalDatabaseSpec.getGranularities();
        this.unitFactory = relationalDatabaseSpec.getUnits();
    }

    private static void populatePropositionMap(Map<String, PropertySpec> map,
            PropertySpec[] propertySpecs) {
        if (propertySpecs != null) {
            for (PropertySpec propertySpec : propertySpecs) {
                for (String code : propertySpec.getCodes()) {
                    map.put(code, propertySpec);
                }
            }
        }
    }

    public RelationalDatabaseSchemaAdaptor() {
        this.primitiveParameterSpecs = new HashMap<String, PropertySpec>();
        this.eventSpecs = new HashMap<String, PropertySpec>();
        this.constantSpecs = new HashMap<String, PropertySpec>();
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws SchemaAdaptorInitializationException {
        super.initialize(config);
        try {
            this.sqlGenerator = new SQLGeneratorFactory(getConnectionSpec())
                    .newInstance();
        } catch (Exception ex) {
            throw new SchemaAdaptorInitializationException(
                    "Error getting a SQL generator driver", ex);
        }
    }

    public GranularityFactory getGranularityFactory() {
        return this.granularityFactory;
    }

    public UnitFactory getUnitFactory() {
        return this.unitFactory;
    }

    public List<String> getAllKeyIds(int start, int count,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        String allKeyIdsStmt = this.sqlGenerator.generateGetAllKeyIdsQuery(
                start, count, dataSourceConstraints,
                propSpecsForConstraints(dataSourceConstraints));
        DSBUtil.logger().log(Level.INFO,
                "Executing the following query for getAllKeyIds: {0}",
                allKeyIdsStmt);
        final List<String> result = new ArrayList<String>();
        final int fStart = start;
        final int fCount = count;
        ResultProcessor resultProcessor = new ResultProcessor() {

            public void process(ResultSet resultSet) throws SQLException {
                boolean limitingSupported =
                        RelationalDatabaseSchemaAdaptor.this.sqlGenerator
                        .isLimitingSupported();
                int rowNum = 1;
                while (resultSet.next()) {
                    if (limitingSupported
                            || (rowNum >= fStart && rowNum < fStart + fCount)) {
                        result.add(resultSet.getString(1));
                    }
                    rowNum++;
                }
            }
        };

        try {
            SQLExecutor.executeSQL(getConnectionSpec(), allKeyIdsStmt,
                    resultProcessor);
        } catch (SQLException ex) {
            throw new DataSourceReadException(ex);
        }

        return result;
    }

//    private Map<EntitySpec, Map<PropertySpec, List<String>>> generateBatches(
//            Map<PropertySpec, List<String>> propertySpecToPropIdMap) {
//        Map<EntitySpec, Map<PropertySpec, List<String>>> batchMap =
//                new HashMap<EntitySpec, Map<PropertySpec, List<String>>>();
//        for (Map.Entry<PropertySpec, List<String>> me :
//                propertySpecToPropIdMap.entrySet()) {
//            EntitySpec entitySpec = me.getKey().getEntitySpec();
//            Map<PropertySpec, List<String>> m = batchMap.get(entitySpec);
//            if (m == null) {
//                m = new HashMap<PropertySpec, List<String>>();
//                batchMap.put(entitySpec, m);
//            }
//            m.put(me.getKey(), me.getValue());
//        }
//        return batchMap;
//    }

        private Collection<Map<PropertySpec, List<String>>> generateBatches(
            Map<PropertySpec, List<String>> propertySpecToPropIdMap) {
        Map<EntitySpec, Map<PropertySpec, List<String>>> batchMap =
                new HashMap<EntitySpec, Map<PropertySpec, List<String>>>();
        for (Map.Entry<PropertySpec, List<String>> me :
                propertySpecToPropIdMap.entrySet()) {
            EntitySpec entitySpec = me.getKey().getEntitySpec();
            Map<PropertySpec, List<String>> m = batchMap.get(entitySpec);
            if (m == null) {
                m = new HashMap<PropertySpec, List<String>>();
                batchMap.put(entitySpec, m);
            }
            m.put(me.getKey(), me.getValue());
        }
        return batchMap.values();
    }

    private Map<PropertySpec, List<String>> propSpecsForConstraints(
            DataSourceConstraint dataSourceConstraints) {
        Map<PropertySpec, List<String>> propertySpecToPropIdMap =
                new HashMap<PropertySpec, List<String>>();
        if (dataSourceConstraints != null) {
            for (Iterator<DataSourceConstraint> itr =
                    dataSourceConstraints.andIterator(); itr.hasNext();) {
                DataSourceConstraint dsc = itr.next();
                String propId = dsc.getPropositionId();
                PropertySpec propertySpec = null;
                populatePropertySpecToPropIdMap(propId,
                    propertySpecToPropIdMap, propertySpec);
            }
        }
        return propertySpecToPropIdMap;
    }

    private void populatePropertySpecToPropIdMap(String propId, 
            Map<PropertySpec, List<String>> propertySpecToPropIdMap,
            PropertySpec propertySpec) throws AssertionError {
        if (this.primitiveParameterSpecs.containsKey(propId)) {
            propertySpec = this.primitiveParameterSpecs.get(propId);
        } else if (this.eventSpecs.containsKey(propId)) {
            propertySpec = this.eventSpecs.get(propId);
        } else if (this.constantSpecs.containsKey(propId)) {
            propertySpec = this.constantSpecs.get(propId);
        } else {
            DSBUtil.logger().log(Level.INFO,
                    "This data source does not know about proposition {0}",
                     propId);
            return;
        }
        if (propertySpecToPropIdMap.containsKey(propertySpec)) {
            List<String> propIdList = propertySpecToPropIdMap.get(propertySpec);
            propIdList.add(propId);
        } else {
            List<String> propIdList = new ArrayList<String>();
            propIdList.add(propId);
            propertySpecToPropIdMap.put(propertySpec, propIdList);
        }
    }

    private static class ConstantParameterResultProcessor 
            extends ResultProcessorAllKeyIds<ConstantParameter> {

        public void process(ResultSet resultSet) throws SQLException {
            Map<PropertySpec, List<String>> propositionSpecs =
                    getPropertySpecToPropIdMap();
            Map<String, PropertySpec> reversePropositionSpecs =
                    getPropIdToPropertySpecMap();
            Map<String, List<ConstantParameter>> results = getResults();
            while (resultSet.next()) {
                String propId;
                PropertySpec propositionSpec;
                if (propositionSpecs.size() == 1) {
                    Map.Entry<PropertySpec, List<String>> me =
                            propositionSpecs.entrySet().iterator().next();
                    propositionSpec = me.getKey();
                    if (me.getValue().size() == 1) {
                        propId = me.getValue().get(0);
                    } else {
                        propId = resultSet.getString(3);
                    }
                } else {
                    propId = resultSet.getString(3);
                    propositionSpec = reversePropositionSpecs.get(propId);
                }
                ValueFactory vf = propositionSpec.getValueType();
                ConstantParameter cp =
                        new ConstantParameter(propId);
                String keyId = resultSet.getString(1);
                cp.setValue(vf.getInstance(resultSet.getString(2)));
                if (results.containsKey(keyId)) {
                    List<ConstantParameter> l = results.get(keyId);
                    l.add(cp);
                } else {
                    List<ConstantParameter> l = 
                            new ArrayList<ConstantParameter>();
                    l.add(cp);
                    results.put(keyId, l);
                }
            }
        }
    }

    public List<ConstantParameter> getConstantParameters(String keyId,
            Set<String> paramIds) throws DataSourceReadException {
        Map<String, List<ConstantParameter>> results =
                new HashMap<String, List<ConstantParameter>>();

        ConstantParameterResultProcessor resultProcessor =
                new ConstantParameterResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setPropIdToPropertySpecMap(this.constantSpecs);

        return readPropositions(Collections.singleton(keyId), paramIds, null,
                null, resultProcessor).get(keyId);
    }

    public Map<String, List<ConstantParameter>> getConstantParameters(
            Set<String> keyIds, Set<String> paramIds)
            throws DataSourceReadException {
        Map<String, List<ConstantParameter>> results =
                new HashMap<String, List<ConstantParameter>>();

        ConstantParameterResultProcessor resultProcessor =
                new ConstantParameterResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setPropIdToPropertySpecMap(this.constantSpecs);

        return readPropositions(keyIds, paramIds, null,
                null, resultProcessor);
    }

    private static abstract class ResultProcessorAllKeyIds
            <P extends Proposition> implements ResultProcessor {

        private Map<String, List<P>> results;
        private Map<PropertySpec, List<String>> propertySpecToPropIdMap;
        private Map<String, PropertySpec> propIdToPropertySpecMap;
        private Set<String> propIds;

        public void setPropIds(Set<String> propIds) {
            this.propIds = propIds;
        }

        public Set<String> getPropIds() {
            return this.propIds;
        }

        public Map<PropertySpec, List<String>> getPropertySpecToPropIdMap() {
            return this.propertySpecToPropIdMap;
        }

        public void setPropertySpecToPropIdMap(
                Map<PropertySpec, List<String>> propositionSpecs) {
            this.propertySpecToPropIdMap = propositionSpecs;
        }

        public Map<String, PropertySpec> getPropIdToPropertySpecMap() {
            return this.propIdToPropertySpecMap;
        }

        public void setPropIdToPropertySpecMap(
                Map<String, PropertySpec> propIdToPropertySpecMap) {
            this.propIdToPropertySpecMap = propIdToPropertySpecMap;
        }

        Map<String, List<P>> getResults() {
            return this.results;
        }

        void setResults(Map<String, List<P>> results) {
            this.results = results;
        }
    }

    private static class PrimitiveParameterResultProcessorAllKeyIds
            extends ResultProcessorAllKeyIds<PrimitiveParameter> {

        public void process(ResultSet resultSet) throws SQLException {
            Map<PropertySpec, List<String>> propositionSpecs =
                    getPropertySpecToPropIdMap();
            Map<String, PropertySpec> reversePropositionSpecs =
                    getPropIdToPropertySpecMap();
            Map<String, List<PrimitiveParameter>> results = getResults();
            
            while (resultSet.next()) {
                String keyId = resultSet.getString(1);
                String propId;
                PropertySpec propositionSpec;
                if (propositionSpecs.size() == 1) {
                    Map.Entry<PropertySpec, List<String>> me =
                            propositionSpecs.entrySet().iterator().next();
                    propositionSpec = me.getKey();
                    if (me.getValue().size() == 1) {
                        propId = me.getValue().get(0);
                    } else {
                        propId = resultSet.getString(2);
                    }
                } else {
                    propId = resultSet.getString(4);
                    propositionSpec = reversePropositionSpecs.get(propId);
                }
                ValueFactory vf = propositionSpec.getValueType();
                PrimitiveParameter p = new PrimitiveParameter(propId);
                try {
                    p.setTimestamp(propositionSpec.getPositionParser()
                            .toLong(resultSet, 3));
                } catch (SQLException e) {
                    DSBUtil.logger().log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                    continue;
                }
                p.setGranularity(propositionSpec.getGranularity());
                p.setValue(vf.getInstance(resultSet.getString(3)));
                if (results.containsKey(keyId)) {
                    List<PrimitiveParameter> l = results.get(keyId);
                    l.add(p);
                } else {
                    List<PrimitiveParameter> l =
                            new ArrayList<PrimitiveParameter>();
                    l.add(p);
                    results.put(keyId, l);
                }
            }
        }
    }

    private static class EventResultProcessorAllKeyIds
            extends ResultProcessorAllKeyIds<Event> {

        public void process(ResultSet resultSet) throws SQLException {
            Map<PropertySpec, List<String>> propertySpecToPropIdMap =
                    getPropertySpecToPropIdMap();
            Map<String, List<Event>> results = getResults();

            while (resultSet.next()) {
                String keyId = resultSet.getString(1);
                String propId;
                PropertySpec propertySpec;
                Map.Entry<PropertySpec, List<String>> me =
                    propertySpecToPropIdMap.entrySet().iterator().next();
                propertySpec = me.getKey();
                if (getPropIds().size() == 1) {
                    propId = getPropIds().iterator().next();
                } else {
                    propId = resultSet.getString(2);
                }
                Event event = new Event(propId);
                Granularity gran = propertySpec.getGranularity();
                try {
                    long d = propertySpec
                            .getPositionParser().toLong(resultSet, 3);
                    event.setInterval(new PointInterval(d, gran, d, gran));
                } catch (SQLException e) {
                    DSBUtil.logger().log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                    continue;
                }
                if (results.containsKey(keyId)) {
                    List<Event> l = results.get(keyId);
                    l.add(event);
                } else {
                    List<Event> l = new ArrayList<Event>();
                    l.add(event);
                    results.put(keyId, l);
                }
            }
        }
    }

    private Map<String, List<PrimitiveParameter>> readPrimitiveParameters(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints, SQLOrderBy order)
            throws DataSourceReadException {
        Map<String, List<PrimitiveParameter>> results =
                new HashMap<String, List<PrimitiveParameter>>();

        PrimitiveParameterResultProcessorAllKeyIds resultProcessor =
                new PrimitiveParameterResultProcessorAllKeyIds();
        resultProcessor.setResults(results);
        resultProcessor.setPropIdToPropertySpecMap(
                this.primitiveParameterSpecs);

        return readPropositions(keyIds, paramIds, dataSourceConstraints,
                order, resultProcessor);
    }

    private <P extends Proposition> Map<String, List<P>> readPropositions(
            Set<String> keyIds, Set<String> propIds,
            DataSourceConstraint dataSourceConstraints, SQLOrderBy order,
            ResultProcessorAllKeyIds<P> resultProcessor)
            throws DataSourceReadException {
        Map<PropertySpec, List<String>> propertySpecToPropIdMapFromConstraints =
                propSpecsForConstraints(dataSourceConstraints);
        Map<PropertySpec, List<String>> propertySpecToPropIdMapFromPropIds =
                new HashMap<PropertySpec, List<String>>();
        for (String propId : propIds) {
            PropertySpec propertySpec =
                    resultProcessor.getPropIdToPropertySpecMap().get(propId);
            populatePropertySpecToPropIdMap(propId, 
                    propertySpecToPropIdMapFromPropIds,
                    propertySpec);
        }

        Map<PropertySpec, List<String>> propertySpecToPropIdMap =
                new HashMap<PropertySpec, List<String>>(
                propertySpecToPropIdMapFromConstraints);
        propertySpecToPropIdMap.putAll(propertySpecToPropIdMapFromPropIds);

        resultProcessor.setPropIds(propIds);
        resultProcessor.setPropertySpecToPropIdMap(propertySpecToPropIdMap);
        //Collection<Map<PropertySpec, List<String>>> batchMap =
        //        generateBatches(propertySpecToPropIdMapFromConstraints);

        Map<String, List<P>> results = new HashMap<String, List<P>>();
        //for (Map<PropertySpec, List<String>> m : batchMap) {
        for (Map.Entry<PropertySpec, List<String>> me :
            propertySpecToPropIdMapFromPropIds.entrySet()) {
            Map<PropertySpec, List<String>> m =
                    new HashMap<PropertySpec, List<String>>(
                    propertySpecToPropIdMapFromConstraints);
            m.put(me.getKey(), me.getValue());
            String query = this.sqlGenerator.generateReadPropositionsQuery(
                    dataSourceConstraints, m, keyIds, order);

            DSBUtil.logger().log(Level.INFO,
                    "Executing the following query for readPropositions: {0}",
                    query);

            try {
                SQLExecutor.executeSQL(getConnectionSpec(), query,
                        resultProcessor);
            } catch (SQLException ex) {
                throw new DataSourceReadException(ex);
            }

            Map<String, List<P>> resultsMap = resultProcessor.getResults();
            for (Map.Entry<String, List<P>> me2 : resultsMap.entrySet()) {
                if (me2.getValue() == null) {
                    me2.setValue(new ArrayList<P>(0));
                }
                List<P> rList = results.get(me2.getKey());
                if (rList == null) {
                    results.put(me2.getKey(), me2.getValue());
                } else {
                    throw new AssertionError("never reached");
                }
            }
            results.putAll(resultsMap);
        }
        
        return results;
    }

    private Map<String, List<Event>> readEvents(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints,
            SQLOrderBy order) throws DataSourceReadException {
        Map<String, List<Event>> results = new HashMap<String, List<Event>>();

        EventResultProcessorAllKeyIds resultProcessor =
                new EventResultProcessorAllKeyIds();
        resultProcessor.setResults(results);
        resultProcessor.setPropIdToPropertySpecMap(this.eventSpecs);

        return readPropositions(keyIds, eventIds, dataSourceConstraints,
                order, resultProcessor);
    }

    public List<PrimitiveParameter> getPrimitiveParametersAsc(String keyId,
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        List<PrimitiveParameter> result =
                readPrimitiveParameters(Collections.singleton(keyId),
                paramIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING).get(keyId);
        if (result == null) {
            result = new ArrayList<PrimitiveParameter>(0);
        }
        return result;
    }

    public List<PrimitiveParameter> getPrimitiveParametersDesc(String keyId,
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        List<PrimitiveParameter> result =
                readPrimitiveParameters(Collections.singleton(keyId),
                paramIds, dataSourceConstraints,
                SQLOrderBy.DESCENDING).get(keyId);
        if (result == null) {
            result = new ArrayList<PrimitiveParameter>(0);
        }
        return result;
    }

    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readPrimitiveParameters(null, paramIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING);
    }

    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readPrimitiveParameters(keyIds, paramIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING);
    }

    public Map<String, List<Event>> getEventsAsc(
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readEvents(null, eventIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING);
    }

    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readEvents(keyIds, eventIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING);
    }

    public List<Event> getEventsAsc(String keyId,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        List<Event> result = readEvents(Collections.singleton(keyId),
                eventIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING).get(keyId);
        if (result == null) {
            result = new ArrayList<Event>(0);
        }
        return result;
    }

    public List<Event> getEventsDesc(String keyId,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        List<Event> result = readEvents(Collections.singleton(keyId),
                eventIds, dataSourceConstraints,
                SQLOrderBy.DESCENDING).get(keyId);
        if (result == null) {
            result = new ArrayList<Event>(0);
        }
        return result;
    }

    public String getKeyType() {
        return "PATIENT";
    }

    public String getKeyTypeDisplayName() {
        return "patient";
    }

    public String getKeyTypePluralDisplayName() {
        return "patients";
    }
}
