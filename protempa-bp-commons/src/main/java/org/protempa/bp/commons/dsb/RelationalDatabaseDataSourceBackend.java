package org.protempa.bp.commons.dsb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.DatabaseAPI;
import org.protempa.DataSourceBackendFailedValidationException;
import org.protempa.DataSourceBackendInitializationException;
import org.protempa.DataSourceReadException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.QuerySession;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.AbstractCommonsDataSourceBackend;
import org.protempa.bp.commons.BackendProperty;
import org.protempa.bp.commons.dsb.sqlgen.RelationalDatabaseSpec;
import org.protempa.bp.commons.dsb.sqlgen.SQLGeneratorFactory;
import org.protempa.bp.commons.dsb.sqlgen.SQLOrderBy;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.ConstantProposition;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

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
    private final RelationalDatabaseSpec relationalDatabaseSpec;
    private SQLGenerator sqlGenerator;
    
    

    /**
     * Instantiates the backend with the specification of a mapping from
     * propositions to where they are stored in the database.
     *
     * @param relationalDatabaseSpec a {@link RelationalDatabaseSpec}, the
     * specification of where propositions are stored in the database.
     */
    public RelationalDatabaseDataSourceBackend(
            RelationalDatabaseSpec relationalDatabaseSpec) {
        this.relationalDatabaseSpec = relationalDatabaseSpec;
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
                    "Data source backend " + nameForErrors() +
                    " requires a Java database API (DriverManager or " +
                    "DataSource) to be specified in its configuration");
        }
        
        try {
            ConnectionSpec connectionSpecInstance =
                    this.databaseAPI.newConnectionSpecInstance(
                    this.databaseId, this.username, this.password);
            this.sqlGenerator = new SQLGeneratorFactory(connectionSpecInstance,
                    this.relationalDatabaseSpec, this).newInstance();
        } catch (Exception ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not initialize data source backend " +
                    nameForErrors(), ex);
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
        return this.sqlGenerator.getGranularities();
    }

    @Override
    public UnitFactory getUnitFactory() {
        return this.sqlGenerator.getUnits();
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

    @Override
    public Map<String, List<ConstantProposition>> getConstantParameters(
            Set<String> keyIds, Set<String> paramIds,
            Filter dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        return this.sqlGenerator.readConstants(keyIds, paramIds,
                dataSourceConstraints);
    }

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersDesc(
            Set<String> keyIds,
            Set<String> paramIds, Filter dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        return this.sqlGenerator.readPrimitiveParameters(keyIds, paramIds,
                dataSourceConstraints,
                SQLOrderBy.DESCENDING);
    }

    @Override
    public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
            Set<String> keyIds, Set<String> paramIds,
            Filter dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        return this.sqlGenerator.readPrimitiveParameters(keyIds, paramIds,
                dataSourceConstraints,
                SQLOrderBy.ASCENDING);
    }

    @Override
    public Map<String, List<Event>> getEventsAsc(Set<String> keyIds,
            Set<String> eventIds, Filter filters, QuerySession qs)
            throws DataSourceReadException {
        return this.sqlGenerator.readEvents(keyIds, eventIds,
                filters, SQLOrderBy.ASCENDING);
    }

    @Override
    public Map<String, List<Event>> getEventsDesc(Set<String> keyIds,
            Set<String> eventIds, Filter dataSourceConstraints,
            QuerySession qs)
            throws DataSourceReadException {
        return this.sqlGenerator.readEvents(keyIds, eventIds,
                dataSourceConstraints, SQLOrderBy.DESCENDING);
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException,
            KnowledgeSourceReadException{
        this.relationalDatabaseSpec.validate(knowledgeSource);
    }
}
