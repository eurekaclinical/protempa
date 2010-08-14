package org.protempa.bp.commons.dsb;

import org.protempa.bp.commons.dsb.sqlgen.SQLOrderBy;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;
import org.arp.javautil.sql.DatabaseAPI;
import org.protempa.bp.commons.AbstractCommonsDataSourceBackend;
import org.protempa.bp.commons.dsb.sqlgen.SQLGenerator;
import org.protempa.bp.commons.dsb.sqlgen.RelationalDatabaseSpec;
import org.protempa.bp.commons.dsb.sqlgen.PropertySpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.arp.javautil.collections.Collections;
import org.arp.javautil.sql.ConnectionSpec;

import org.protempa.proposition.Event;
import org.protempa.proposition.PointInterval;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.ValueFactory;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.protempa.DataSourceBackendInitializationException;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.DataSourceReadException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.BackendProperty;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Proposition;

/**
 * Implements access to relational databases.
 * 
 * @author Andrew Post
 */
public abstract class RelationalDatabaseDataSourceBackend
        extends AbstractCommonsDataSourceBackend {

    private DatabaseAPI databaseAPI;
    private String databaseId;
    private String username;
    private String password;
    private GranularityFactory granularityFactory;
    private UnitFactory unitFactory;
    private SQLGenerator sqlGenerator;
    private ConnectionSpec connectionSpecInstance;
    private final Map<String, PropertySpec> primitiveParameterSpecs;
    private final Map<String, PropertySpec> eventSpecs;
    private final Map<String, PropertySpec> constantSpecs;

    /**
     * Instantiates the backend with the specification of a mapping from
     * propositions to where they are stored in the database.
     *
     * @param relationalDatabaseSpec a {@link RelationalDatabaseSpec}, the
     * specification of where propositions are stored in the database.
     */
    public RelationalDatabaseDataSourceBackend(
            RelationalDatabaseSpec relationalDatabaseSpec) {
        this();
        if (relationalDatabaseSpec != null) {
            populatePropositionMap(this.primitiveParameterSpecs,
                    relationalDatabaseSpec.getPrimitiveParameterSpecs());
            populatePropositionMap(this.eventSpecs,
                    relationalDatabaseSpec.getEventSpecs());
            populatePropositionMap(this.constantSpecs,
                    relationalDatabaseSpec.getConstantParameterSpecs());
        } else {
            throw new IllegalArgumentException(
                    "relationalDatabaseSpec cannot be null");
        }
        this.granularityFactory = relationalDatabaseSpec.getGranularities();
        this.unitFactory = relationalDatabaseSpec.getUnits();
    }

    private RelationalDatabaseDataSourceBackend() {
        this.primitiveParameterSpecs = new HashMap<String, PropertySpec>();
        this.eventSpecs = new HashMap<String, PropertySpec>();
        this.constantSpecs = new HashMap<String, PropertySpec>();
        this.databaseAPI = DatabaseAPI.DRIVERMANAGER;
    }

    /**
     * Collects the database connection information specified in this backend's
     * configuration, and uses it to try and get a SQL generator with which to
     * generate database queries.
     *
     * @param config the {@link BackendInstanceSpec} that specifies this
     * backend's configuration parameters.
     *
     * @throws DataSourceBackendInitializationException if bad database
     * connection information was provided or a SQL generator that is
     * compatible with the database and available drivers is not available.
     */
    @Override
    public void initialize(BackendInstanceSpec config)
            throws DataSourceBackendInitializationException {
        super.initialize(config);

        if (this.databaseAPI == null) {
            throw new DataSourceBackendInitializationException(
                    "A Java database API (DriverManager or DataSource) must "
                    + "be specified in this backend's configuration");
        }

        try {
            this.connectionSpecInstance = 
                    this.databaseAPI.newConnectionSpecInstance(
                    this.databaseId, this.username, this.password);
        } catch (InvalidConnectionSpecArguments ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not initialize database connection information",
                    ex);
        }

        try {
            this.sqlGenerator =
                    new SQLGeneratorFactory(this.connectionSpecInstance)
                    .newInstance();
        } catch (Exception ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not load a SQL generator", ex);
        }
    }

    /**
     * Returns which Java database API this backend is configured to use.
     * @return a {@link DatabaseAPI}. The default value is
     * {@link DatabaseAPI.DRIVERMANAGER}.
     */
    public DatabaseAPI getDatabaseAPI() {
        return this.databaseAPI;
    }

    /**
     * Configures which Java database API to use
     * ({@link java.sql.DriverManager} or {@link javax.sql.DataSource}. Must
     * be set to something not <code>null</code> before any of the read
     * methods are called.
     *
     * @param databaseAPI a {@link DatabaseAPI}.
     */
    public void setDatabaseAPI(DatabaseAPI databaseAPI) {
        this.databaseAPI = databaseAPI;
    }

    /**
     * Configures which Java database API to use
     * ({@link java.sql.DriverManager} or {@link javax.sql.DataSource} by
     * parsing a {@link DatabaseAPI}'s name. Cannot be null.
     *
     * @param databaseAPIString a {@link DatabaseAPI}'s name.
     */
    @BackendProperty(propertyName="databaseAPI")
    public void parseDatabaseAPI(String databaseAPISTring) {
        setDatabaseAPI(DatabaseAPI.valueOf(databaseAPISTring));
    }

    /**
     * Gets the database id for a database.
     *
     * @return a database id {@link String}.
     */
    public String getDatabaseId() {
        return this.databaseId;
    }

    /**
     * Sets the database id for a database. This must be set to something
     * not <code>null</code>.
     *
     * @param databaseId a a database id {@link String}.
     */
    @BackendProperty
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    /**
     * Gets a user for the database.
     *
     * @return a user {@link String}.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a user for the database.
     *
     * @param user a user {@link String}.
     */
    @BackendProperty
    public void setUsername(String user) {
        this.username = user;
    }

    /**
     * Gets the password for the specified user.
     *
     * @return a password {@link String}.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the specified user.
     *
     * @param password a password {@link String}.
     */
    @BackendProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public GranularityFactory getGranularityFactory() {
        return this.granularityFactory;
    }

    @Override
    public UnitFactory getUnitFactory() {
        return this.unitFactory;
    }

    private Map<PropertySpec, List<String>> combinePropSpecMaps(
            Map<PropertySpec, List<String>> propSpecMapFromConstraints,
            Map<PropertySpec, List<String>> propSpecMapFromPropIds) {
        Map<PropertySpec, List<String>> propertySpecToPropIdMap = 
                new HashMap<PropertySpec, List<String>>(
                propSpecMapFromConstraints);
        propertySpecToPropIdMap.putAll(propSpecMapFromPropIds);
        return propertySpecToPropIdMap;
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
//    private Collection<Map<PropertySpec, List<String>>> generateBatches(
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
//        return batchMap.values();
//    }
    private Map<PropertySpec, List<String>> propSpecMapForConstraints(
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        Map<PropertySpec, List<String>> propertySpecToPropIdMap =
                new HashMap<PropertySpec, List<String>>();
        if (dataSourceConstraints != null) {
            for (Iterator<DataSourceConstraint> itr =
                    dataSourceConstraints.andIterator(); itr.hasNext();) {
                DataSourceConstraint dsc = itr.next();
                String propId = dsc.getPropositionId();
                boolean inDataSource =
                        populatePropertySpecToPropIdMap(propId,
                        propertySpecToPropIdMap);
                if (!inDataSource) {
                    //FIXME this error message should refer to the data source
                    //backend, but we have not reference to it.
                    String msg =
                            "Data source constraint {0} on proposition {1} "
                            + "cannot be applied to this data source backend "
                            + "because the backend does not know about {1}";
                    MessageFormat mf = new MessageFormat(msg);
                    throw new DataSourceReadException(
                            mf.format(new Object[]{dsc, propId}));
                }
            }
        }
        return propertySpecToPropIdMap;
    }

    private PropertySpec propertySpec(String propId) {
        //TODO This is where the code goes for PropertySpecs defining a temp table.
        if (this.primitiveParameterSpecs.containsKey(propId))
            return this.primitiveParameterSpecs.get(propId);
        else if (this.eventSpecs.containsKey(propId))
            return this.eventSpecs.get(propId);
        else if (this.constantSpecs.containsKey(propId))
            return this.constantSpecs.get(propId);
        else
            return null;
    }

    private boolean populatePropertySpecToPropIdMap(String propId,
            Map<PropertySpec, List<String>> propertySpecToPropIdMap)
            throws AssertionError {
        PropertySpec propertySpec = propertySpec(propId);
        if (propertySpec == null)
            return false;
        Collections.putList(propertySpecToPropIdMap, propertySpec, propId);
        return true;
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
                Collections.putList(results, keyId, cp);
            }
        }
    }

    @Override
    public Map<String, List<ConstantParameter>> getConstantParameters(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        Map<String, List<ConstantParameter>> results =
                new HashMap<String, List<ConstantParameter>>();

        ConstantParameterResultProcessor resultProcessor =
                new ConstantParameterResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setPropIdToPropertySpecMap(this.constantSpecs);

        return readPropositions(keyIds, paramIds, dataSourceConstraints,
                null, resultProcessor);
    }

    private static abstract class ResultProcessorAllKeyIds<P extends Proposition> implements ResultProcessor {

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
                Map<PropertySpec, List<String>> propertySpecToPropIdMap) {
            this.propertySpecToPropIdMap = propertySpecToPropIdMap;
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
                    p.setTimestamp(propositionSpec.getPositionParser().toLong(resultSet, 3));
                } catch (SQLException e) {
                    DSBUtil.logger().log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                    continue;
                }
                p.setGranularity(propositionSpec.getGranularity());
                p.setValue(vf.getInstance(resultSet.getString(3)));
                Collections.putList(results, keyId, p);
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
                    long d = propertySpec.getPositionParser().toLong(resultSet, 3);
                    event.setInterval(new PointInterval(d, gran, d, gran));
                } catch (SQLException e) {
                    DSBUtil.logger().log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                    continue;
                }
                Collections.putList(results, keyId, event);
            }
        }
    }

    private Map<PropertySpec, List<String>> propSpecMapForPropIds(
            Set<String> propIds) throws AssertionError {
        Map<PropertySpec, List<String>> propertySpecToPropIdMapFromPropIds =
                new HashMap<PropertySpec, List<String>>();
        for (String propId : propIds) {
            boolean inDataSource = 
                    populatePropertySpecToPropIdMap(propId,
                    propertySpecToPropIdMapFromPropIds);
            if (!inDataSource) {
                DSBUtil.logger().log(Level.INFO,
                        "This data source does not know about {0}", propId);
            }
        }
        return propertySpecToPropIdMapFromPropIds;
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
        Map<PropertySpec, List<String>> propSpecMapFromConstraints =
                propSpecMapForConstraints(dataSourceConstraints);
        Map<PropertySpec, List<String>> propSpecMapFromPropIds =
                propSpecMapForPropIds(propIds);
        Map<PropertySpec, List<String>> propSpecMap =
                combinePropSpecMaps(propSpecMapFromConstraints,
                propSpecMapFromPropIds);

        resultProcessor.setPropIds(propIds);
        resultProcessor.setPropertySpecToPropIdMap(propSpecMap);
        //Collection<Map<PropertySpec, List<String>>> batchMap =
        //        generateBatches(propertySpecToPropIdMapFromConstraints);

        Map<String, List<P>> results = new HashMap<String, List<P>>();
        //for (Map<PropertySpec, List<String>> m : batchMap) {
        for (PropertySpec propertySpec : propSpecMapFromPropIds.keySet()) {
            Set<PropertySpec> propertySpecs = new HashSet<PropertySpec>(
                    propSpecMapFromConstraints.keySet());
            propertySpecs.add(propertySpec);
            String query = this.sqlGenerator.generateSelect(propIds, 
                    dataSourceConstraints, propertySpecs, keyIds, order);

            DSBUtil.logger().log(Level.INFO,
                    "Executing the following query for readPropositions: {0}",
                    query);

            try {
                SQLExecutor.executeSQL(this.connectionSpecInstance, query,
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

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
            Set<String> keyIds,
            Set<String> paramIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readPrimitiveParameters(keyIds, paramIds, dataSourceConstraints,
                SQLOrderBy.DESCENDING);
    }

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readPrimitiveParameters(keyIds, paramIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING);
    }

    @Override
    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readEvents(keyIds, eventIds, dataSourceConstraints,
                SQLOrderBy.ASCENDING);
    }

    @Override
    public Map<String, List<Event>> getEventsDesc(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        return readEvents(keyIds, eventIds, dataSourceConstraints,
                SQLOrderBy.DESCENDING);
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
}
