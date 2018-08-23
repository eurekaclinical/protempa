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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;
import org.protempa.BackendCloseException;
import org.protempa.DataSourceReadException;
import org.protempa.DataSourceWriteException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;
import org.protempa.KeySetSpec;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaEvent;
import org.protempa.backend.AbstractCommonsDataSourceBackend;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.DataSourceBackendFailedConfigurationValidationException;
import org.protempa.backend.DataSourceBackendFailedDataValidationException;
import org.protempa.backend.DataSourceBackendInitializationException;
import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.dsb.DataValidationEvent;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.relationaldb.mappings.DelimFileMappingsFactory;
import org.protempa.backend.dsb.relationaldb.mappings.MappingsFactory;
import org.protempa.backend.dsb.relationaldb.mappings.ResourceMappingsFactory;
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.key.KeyLoaderQueryResultsHandler;
import org.protempa.dest.key.KeySetQueryResultsHandler;
import org.protempa.proposition.Proposition;

/**
 * Implements access to relational databases. Backend properties set the
 * database connection information. It uses a {@link SQLGenerator} service
 * loadable using Java's {@link java.util.ServiceLoader} for generating SQL that
 * is appropriate for your database and JDBC driver. Service providers are
 * built-in for MySQL 4.1 and 5 using version 5 of the Connector/J JDBC driver,
 * and Oracle 10g using the ojdbc6 driver.
 *
 * Two system properties control the behavior of the backend and are useful for
 * debugging. The <code>protempa.dsb.relationaldatabase.sqlgenerator</code>
 * property can be set with the full class name of a {@link SQLGenerator}
 * service provider to force its use. This circumvents the built-in algorithm
 * for picking a service provider to use. The
 * <code>protempa.dsb.relationaldatabase.skipexecution</code> property can be
 * set to <code>true</code> to cause the backend to generate SQL queries but not
 * execute them. Together with turning on logging to see the SQL queries, this
 * can be useful for debugging the generated SQL without having to wait for them
 * to execute.
 *
 * The backend and various classes that it invokes support extensive logging.
 * Logging at the FINE level on the
 * <code>org.protempa.bp.commons.dsb.sqlgen</code> package will activate logging
 * of the generated SQL queries.
 *
 * @author Andrew Post
 */
public abstract class RelationalDbDataSourceBackend
        extends AbstractCommonsDataSourceBackend {

    private static final DataValidationEvent[] EMPTY_VALIDATION_EVENT_ARRAY
            = new DataValidationEvent[0];
    private DatabaseAPI databaseAPI;
    private String databaseId;
    protected String username;
    private String password;
    private SQLGenerator sqlGenerator;
    private Integer queryTimeout;
    private boolean dryRun;
    private String schemaName;
    private String defaultKeyIdTable;
    private String defaultKeyIdColumn;
    private String defaultKeyIdJoinKey;
    private String keyLoaderKeyIdSchema;
    private String keyLoaderKeyIdTable;
    private String keyLoaderKeyIdColumn;
    private String keyLoaderKeyIdJoinKey;
    private String keyFile;
    private FromBackendRelationalDatabaseSpecBuilder relationalDatabaseSpecBuilder;
    private MappingsFactory mappingsFactory;
    private Integer queryThreadCount;

    public RelationalDbDataSourceBackend() {
        this.databaseAPI = DatabaseAPI.DRIVERMANAGER;

        this.dryRun
                = Boolean.getBoolean(SQLGenUtil.SYSTEM_PROPERTY_SKIP_EXECUTION);
    }

    public MappingsFactory getMappingsFactory() {
        return mappingsFactory;
    }

    public void setMappingsFactory(MappingsFactory mappingsFactory) {
        if (mappingsFactory == null) {
            this.mappingsFactory = new ResourceMappingsFactory("/etc/i2b2dsb/", getClass());
        } else {
            this.mappingsFactory = mappingsFactory;
        }
    }

    @BackendProperty(propertyName = "mappings")
    public void parseMappingsFactory(String pathname) {
        this.mappingsFactory = new DelimFileMappingsFactory(pathname);
    }

    @BackendProperty
    public final void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public final String getSchemaName() {
        return this.schemaName;
    }

    @BackendProperty
    public final void setDefaultKeyIdTable(String defaultKeyIdTable) {
        this.defaultKeyIdTable = defaultKeyIdTable;
    }

    public final String getDefaultKeyIdTable() {
        return defaultKeyIdTable;
    }

    @BackendProperty
    public final void setDefaultKeyIdColumn(String defaultKeyIdColumn) {
        this.defaultKeyIdColumn = defaultKeyIdColumn;
    }

    public final String getDefaultKeyIdColumn() {
        return defaultKeyIdColumn;
    }

    @BackendProperty
    public final void setKeyLoaderKeyIdSchema(String keyLoaderKeyIdSchema) {
        this.keyLoaderKeyIdSchema = keyLoaderKeyIdSchema;
    }

    public final String getKeyLoaderKeyIdSchema() {
        if (this.keyLoaderKeyIdSchema != null) {
            return this.keyLoaderKeyIdSchema;
        } else {
            return this.schemaName;
        }
    }

    @BackendProperty
    public final void setDefaultKeyIdJoinKey(String defaultKeyIdJoinKey) {
        this.defaultKeyIdJoinKey = defaultKeyIdJoinKey;
    }

    public final String getDefaultKeyIdJoinKey() {
        return defaultKeyIdJoinKey;
    }

    public Integer getQueryThreadCount() {
        return queryThreadCount;
    }

    @BackendProperty
    public void setQueryThreadCount(Integer queryThreadCount) {
        this.queryThreadCount = queryThreadCount;
    }

    public String getKeyLoaderKeyIdTable() {
        if (this.keyLoaderKeyIdTable != null) {
            return this.keyLoaderKeyIdTable;
        } else {
            return this.defaultKeyIdTable;
        }
    }

    @BackendProperty
    public void setKeyLoaderKeyIdTable(String keyLoaderKeyIdTable) {
        this.keyLoaderKeyIdTable = keyLoaderKeyIdTable;
    }

    public String getKeyLoaderKeyIdColumn() {
        if (this.keyLoaderKeyIdColumn != null) {
            return this.keyLoaderKeyIdColumn;
        } else {
            return this.defaultKeyIdColumn;
        }
    }

    @BackendProperty
    public void setKeyLoaderKeyIdColumn(String keyLoaderKeyIdColumn) {
        this.keyLoaderKeyIdColumn = keyLoaderKeyIdColumn;
    }

    public String getKeyLoaderKeyIdJoinKey() {
        if (this.keyLoaderKeyIdJoinKey != null) {
            return this.keyLoaderKeyIdJoinKey;
        } else {
            return this.defaultKeyIdJoinKey;
        }
    }

    @BackendProperty
    public void setKeyLoaderKeyIdJoinKey(String keyLoaderKeyIdJoinKey) {
        this.keyLoaderKeyIdJoinKey = keyLoaderKeyIdJoinKey;
    }

    public String getKeyFile() {
        return keyFile;
    }

    @BackendProperty
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
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
     * connection information was provided or a SQL generator that is compatible
     * with the database and available drivers is not available.
     */
    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        super.initialize(config);
        if (this.mappingsFactory == null) {
            setMappingsFactory(null);
        }
        this.relationalDatabaseSpecBuilder
                = createRelationalDatabaseSpecBuilder();
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    @BackendProperty(propertyName = "dryRun")
    public void parseDryRun(String dryRunString) {
        setDryRun(Boolean.parseBoolean(dryRunString));
    }

    /**
     * Returns which Java database API this backend is configured to use.
     *
     * @return a {@link DatabaseAPI}. The default value is
     * {@link DatabaseAPI.DRIVERMANAGER}.
     */
    public DatabaseAPI getDatabaseAPI() {
        return this.databaseAPI;
    }

    /**
     * Configures which Java database API to use ({@link java.sql.DriverManager}
     * or {@link javax.sql.DataSource}. If <code>null</code>, the default is
     * assigned ({@link DatabaseAPI.DRIVERMANAGER}).
     *
     * @param databaseAPI a {@link DatabaseAPI}.
     */
    public void setDatabaseAPI(DatabaseAPI databaseAPI) {
        this.sqlGenerator = null;
        if (databaseAPI == null) {
            databaseAPI = DatabaseAPI.DRIVERMANAGER;
        }
        this.databaseAPI = databaseAPI;
    }

    /**
     * Configures which Java database API to use ({@link java.sql.DriverManager}
     * or {@link javax.sql.DataSource} by parsing a {@link DatabaseAPI}'s name.
     * Cannot be null.
     *
     * @param databaseAPIString a {@link DatabaseAPI}'s name.
     */
    @BackendProperty(propertyName = "databaseAPI")
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
     * Sets the database id for a database. This must be set to something not
     * <code>null</code>.
     *
     * @param databaseId a a database id {@link String}.
     */
    @BackendProperty
    public void setDatabaseId(String databaseId) {
        this.sqlGenerator = null;
        this.databaseId = databaseId;
    }

    /**
     * Sets the query timeout. If <code>null</code>, no timeout will be set (the
     * default).
     *
     * @param seconds the timeout in seconds, or <code>null</code> to disable
     * query timeout.
     */
    @BackendProperty
    public void setQueryTimeout(Integer seconds) {
        if (seconds != null && seconds.intValue() < 0) {
            throw new IllegalArgumentException("invalid seconds: " + seconds);
        }
        this.queryTimeout = seconds;
    }

    /**
     * Returns the query timeout in seconds. The query timeout setting halts
     * query execution if execution does not complete within the specified
     * number of seconds. A value of <code>0</code> or a negative number (the
     * default) means that no query timeout is set.
     *
     * @return the query timeout in seconds, or <code>null</code> if query
     * timeout is disabled.
     */
    public Integer getQueryTimeout() {
        return this.queryTimeout;
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
        this.sqlGenerator = null;
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
        this.sqlGenerator = null;
        this.password = password;
    }

    public boolean isInKeySetMode() {
        return this.keyLoaderKeyIdSchema != null || this.keyLoaderKeyIdTable != null;
    }

    @Override
    public KeySetSpec[] getSelectedKeySetSpecs() throws DataSourceReadException {
        if (isInKeySetMode()) {
            return new KeySetSpec[]{new KeySetSpec(getSourceSystem(), "Cohort", "Cohort", null)};
        } else {
            return KeySetSpec.EMPTY_KEY_SET_SPEC_ARRAY;
        }
    }

    @Override
    public DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds, Set<String> propIds, Filter filters,
            QueryResultsHandler queryResultsHandler)
            throws DataSourceReadException {
        if (this.sqlGenerator == null) {
            try {
                ConnectionSpec connectionSpecInstance
                        = getConnectionSpecInstance();
                this.sqlGenerator = new SQLGeneratorFactory(
                        connectionSpecInstance,
                        this.relationalDatabaseSpecBuilder.build(queryResultsHandler),
                        this).newInstance();
            } catch (InvalidConnectionSpecArguments | SQLException | SQLGeneratorLoadException | NoCompatibleSQLGeneratorException ex) {
                throw new DataSourceReadException(
                        "Could not initialize data source backend "
                        + nameForErrors(), ex);
            }
        }
        return this.sqlGenerator.readPropositionsStreaming(keyIds, propIds,
                filters);
    }

    @Override
    public void deleteAllKeys() throws DataSourceWriteException {
        if (isInKeySetMode()) {
            try {
                ConnectionSpec connectionSpecInstance
                        = getConnectionSpecInstance();
                try (Connection con = connectionSpecInstance.getOrCreate()) {
                    try (Statement stmt = con.createStatement()) {
                        StringBuilder stmtBuilder = new StringBuilder();
                        stmtBuilder.append("DELETE FROM ");
                        if (getKeyLoaderKeyIdSchema() != null) {
                            stmtBuilder.append(getKeyLoaderKeyIdSchema());
                            stmtBuilder.append('.');
                        }
                        stmtBuilder.append(getKeyLoaderKeyIdTable());
                        stmt.execute(stmtBuilder.toString());
                        con.commit();
                    } catch (SQLException sqlex) {
                        try {
                            con.rollback();
                        } catch (SQLException ignore) {
                            sqlex.addSuppressed(ignore);
                        }
                    }
                }
            } catch (InvalidConnectionSpecArguments | SQLException ex) {
                throw new DataSourceWriteException("Could not delete all key ids in data source backend " + nameForErrors(), ex);
            }
        }
    }

    @Override
    public void writeKeysFromKeySet(KeySetQueryResultsHandler qrh) throws DataSourceWriteException {
        if (this.keyFile != null) {
            try {
                Set<String> localKeyIds = Files.lines(Paths.get(this.keyFile))
                        .collect(Collectors.toSet());
                try (DataStreamingEventIterator<Proposition> readPropositions = 
                        readPropositions(null, Collections.singleton(getKeyType()), null, qrh)) {
                    while (readPropositions.hasNext()) {
                        DataStreamingEvent<Proposition> next = readPropositions.next();
                        if (next.getData() != null) {
                            //writeKeys(next.getKeyId());
                        }
                    }
                }
            } catch (IOException | DataSourceReadException ex) {
                throw new DataSourceWriteException(ex);
            }
        }
    }

    @Override
    public void writeKeys(Set<String> keyIds) throws DataSourceWriteException {
        if (isInKeySetMode() && keyIds != null) {
            for (String keyId : keyIds) {
                writeKeys(keyId);
            }
        }
    }

    private void writeKeys(String keyId) throws DataSourceWriteException {
        final int batchSize = 1000;
        int commitSize = 10000;
        try {
            ConnectionSpec connectionSpecInstance
                    = getConnectionSpecInstance();

            try (Connection con = connectionSpecInstance.getOrCreate()) {
                try {
                    int i = 0;
                    List<String> subKeyIds = new ArrayList<>(batchSize);
                    String stmt = buildWriteKeysInsertStmt(batchSize);
                    SQLGenUtil.logger().log(Level.FINER, "Statement for writing keys: {0}", stmt);
                    try (PreparedStatement prepareStatement = con.prepareStatement(stmt)) {
                        subKeyIds.add(keyId);
                        if (++i % batchSize == 0) {
                            for (int j = 0, n = subKeyIds.size(); j < n; j++) {
                                prepareStatement.setObject(j + 1, subKeyIds.get(j));
                            }
                            prepareStatement.execute();
                        }
                        if (i >= commitSize) {
                            con.commit();
                            commitSize = 0;
                        }
                    }
                    if (!subKeyIds.isEmpty()) {
                        stmt = buildWriteKeysInsertStmt(subKeyIds.size());
                        SQLGenUtil.logger().log(Level.FINER, "Statement for writing keys: {0}", stmt);
                        i = 0;
                        try (PreparedStatement prepareStatement = con.prepareStatement(stmt)) {
                            for (String subKeyId : subKeyIds) {
                                prepareStatement.setObject(++i, subKeyId);
                            }
                            prepareStatement.execute();
                        }
                    }
                    if (i >= commitSize) {
                        con.commit();
                        commitSize = 0;
                    }
                } catch (SQLException ex) {
                    if (commitSize > 0) {
                        try {
                            con.rollback();
                        } catch (SQLException ignore) {
                            ex.addSuppressed(ignore);
                        }
                    }
                }
            }
        } catch (InvalidConnectionSpecArguments | SQLException ex) {
            throw new DataSourceWriteException("Could not write key ids in data source backend " + nameForErrors(), ex);
        }
    }

    private String buildWriteKeysInsertStmt(int size) {
        StringBuilder stmtBuilder = new StringBuilder();
        stmtBuilder.append("INSERT INTO ");
        if (getKeyLoaderKeyIdSchema() != null) {
            stmtBuilder.append(getKeyLoaderKeyIdSchema());
            stmtBuilder.append('.');
        }
        stmtBuilder.append(getKeyLoaderKeyIdTable());
        stmtBuilder.append(" (");
        stmtBuilder.append(getKeyLoaderKeyIdColumn());
        stmtBuilder.append(", ");
        stmtBuilder.append(getKeyLoaderKeyIdJoinKey());
        stmtBuilder.append(") ");
        stmtBuilder.append(" SELECT ");
        stmtBuilder.append(getDefaultKeyIdColumn());
        stmtBuilder.append(", ");
        stmtBuilder.append(getDefaultKeyIdJoinKey());
        stmtBuilder.append(" FROM ");
        if (getSchemaName() != null) {
            stmtBuilder.append(getSchemaName());
            stmtBuilder.append('.');
        }
        stmtBuilder.append(getDefaultKeyIdTable());
        stmtBuilder.append(" WHERE ");
        stmtBuilder.append(getDefaultKeyIdColumn());
        stmtBuilder.append(" IN (");
        stmtBuilder.append(StringUtils.join(Collections.nCopies(size, "?"), ','));
        stmtBuilder.append(')');
        String stmt = stmtBuilder.toString();
        return stmt;
    }

    @Override
    public DataValidationEvent[] validateData(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedDataValidationException,
            KnowledgeSourceReadException {
        return EMPTY_VALIDATION_EVENT_ARRAY;
    }

    @Override
    public void validateConfiguration(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedConfigurationValidationException,
            KnowledgeSourceReadException {
        validate(knowledgeSource);
    }

    private void validate(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException,
            DataSourceBackendFailedConfigurationValidationException {
        List<EntitySpec> allSpecs = Arrays.asList(
                this.relationalDatabaseSpecBuilder.getEventSpecs(),
                this.relationalDatabaseSpecBuilder.getConstantSpecs(),
                this.relationalDatabaseSpecBuilder.getPrimitiveParameterSpecs());

        Logger logger = SQLGenUtil.logger();
        for (EntitySpec entitySpec : allSpecs) {
            String entitySpecName = entitySpec.getName();
            logger.log(Level.FINER, "Validating entity spec {0}",
                    entitySpecName);
            String[] propIds = entitySpec.getPropositionIds();
            Set<String> propNamesFromPropSpecs = new HashSet<>();
            PropertySpec[] propSpecs = entitySpec.getPropertySpecs();
            logger.finer("Checking for duplicate properties");
            for (PropertySpec propSpec : propSpecs) {
                String propSpecName = propSpec.getName();
                if (!propNamesFromPropSpecs.add(propSpecName)) {
                    throw new DataSourceBackendFailedConfigurationValidationException(
                            "Duplicate property name " + propSpecName
                            + " in entity spec " + entitySpecName);
                }
            }
            logger.finer("No duplicate properties found");
            logger.finer("Checking for invalid proposition ids and properties");
            Set<String> propNamesFromPropDefs = new HashSet<>();
            Set<String> invalidPropIds = new HashSet<>();
            for (String propId : propIds) {
                PropositionDefinition propDef = knowledgeSource
                        .readPropositionDefinition(propId);
                if (propDef == null) {
                    invalidPropIds.add(propId);
                } else {
                    PropertyDefinition[] propertyDefs = propDef
                            .getPropertyDefinitions();
                    for (PropertyDefinition propertyDef : propertyDefs) {
                        String propName = propertyDef.getId();
                        propNamesFromPropDefs.add(propName);
                    }
                }
            }
            if (!invalidPropIds.isEmpty()) {
                throw new DataSourceBackendFailedConfigurationValidationException(
                        "Invalid proposition id(s) named in entity spec "
                        + entitySpecName + ": '" + StringUtils.join(invalidPropIds, "', '") + "'");
            }
            if (!propNamesFromPropSpecs.removeAll(propNamesFromPropDefs)) {
                throw new DataSourceBackendFailedConfigurationValidationException(
                        "Data model entity spec " + entitySpec.getName()
                        + " has properties '" + StringUtils.join(propNamesFromPropSpecs, "', '")
                        + "' that are not in the knowledge source's corresponding proposition definitions");
            }
            logger.finer("No invalid proposition ids or properties found");
        }
    }

    @Override
    public void close() throws BackendCloseException {
        this.sqlGenerator = null;
        if (this.mappingsFactory != null) {
            try {
                this.mappingsFactory.closeAll();
            } catch (IOException ex) {
                throw new BackendCloseException(ex);
            }
        }
    }

    protected ConnectionSpec getConnectionSpecInstance()
            throws InvalidConnectionSpecArguments {
        return this.databaseAPI.newConnectionSpecInstance(
                this.databaseId, this.username, this.password, false);
    }

    protected abstract EntitySpec[] constantSpecs(String keyIdSchema, String keyIdTable, String keyIdColumn, String keyIdJoinKey) throws IOException;

    protected abstract EntitySpec[] eventSpecs(String keyIdSchema, String keyIdTable, String keyIdColumn, String keyIdJoinKey) throws IOException;

    protected abstract EntitySpec[] primitiveParameterSpecs(String keyIdSchema, String keyIdTable, String keyIdColumn, String keyIdJoinKey) throws IOException;

    private FromBackendRelationalDatabaseSpecBuilder createRelationalDatabaseSpecBuilder() {
        return new FromBackendRelationalDatabaseSpecBuilder();
    }

    private class FromBackendRelationalDatabaseSpecBuilder
            extends RelationalDatabaseSpecBuilder {

        private String keyIdSchema;
        private String keyIdTable;
        private String keyIdColumn;
        private String keyIdJoinKey;

        @Override
        public EntitySpec[] getPrimitiveParameterSpecs() {
            try {
                return primitiveParameterSpecs(this.keyIdSchema, this.keyIdTable, this.keyIdColumn, this.keyIdJoinKey);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public EntitySpec[] getEventSpecs() {
            try {
                return eventSpecs(this.keyIdSchema, this.keyIdTable, this.keyIdColumn, this.keyIdJoinKey);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public EntitySpec[] getConstantSpecs() {
            try {
                return constantSpecs(this.keyIdSchema, this.keyIdTable, this.keyIdColumn, this.keyIdJoinKey);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        RelationalDatabaseSpec build(QueryResultsHandler queryResultsHandler) {
            if (queryResultsHandler instanceof KeyLoaderQueryResultsHandler) {
                this.keyIdSchema = getSchemaName();
                this.keyIdTable = getDefaultKeyIdTable();
                this.keyIdColumn = getDefaultKeyIdColumn();
                this.keyIdJoinKey = getDefaultKeyIdJoinKey();
            } else {
                this.keyIdSchema = getKeyLoaderKeyIdSchema();
                this.keyIdTable = getKeyLoaderKeyIdTable();
                this.keyIdColumn = getKeyLoaderKeyIdColumn();
                this.keyIdJoinKey = getKeyLoaderKeyIdJoinKey();
            }

            return super.build();
        }

    }

    void fireProtempaEvent(ProtempaEvent evt) {
        this.fireEvent(evt);
    }
}
