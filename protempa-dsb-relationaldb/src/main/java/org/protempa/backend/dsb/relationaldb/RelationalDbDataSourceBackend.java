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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;
import org.protempa.*;
import org.protempa.backend.AbstractCommonsDataSourceBackend;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.DataSourceBackendFailedConfigurationValidationException;
import org.protempa.backend.DataSourceBackendFailedDataValidationException;
import org.protempa.backend.DataSourceBackendInitializationException;
import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.dsb.DataValidationEvent;
import org.protempa.backend.dsb.filter.Filter;
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
 * debugging. The
 * <code>protempa.dsb.relationaldatabase.sqlgenerator</code> property can be set
 * with the full class name of a {@link SQLGenerator} service provider to force
 * its use. This circumvents the built-in algorithm for picking a service
 * provider to use. The
 * <code>protempa.dsb.relationaldatabase.skipexecution</code> property can be
 * set to
 * <code>true</code> to cause the backend to generate SQL queries but not
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

    private static final DataValidationEvent[] EMPTY_VALIDATION_EVENT_ARRAY =
            new DataValidationEvent[0];
    private DatabaseAPI databaseAPI;
    private String databaseId;
    protected String username;
    private String password;
    private RelationalDatabaseSpec relationalDatabaseSpec;
    private SQLGenerator sqlGenerator;
    private Integer queryTimeout;
    private boolean dryRun;
    private String schemaName;
    private String keyIdTable;
    private String keyIdColumn;
    private String keyIdSchema;
    private String keyIdJoinKey;

    public RelationalDbDataSourceBackend() {
        this(null);
        
        this.dryRun = 
                Boolean.getBoolean(SQLGenUtil.SYSTEM_PROPERTY_SKIP_EXECUTION);
    }

    /**
     * Instantiates the backend with the specification of a mapping from
     * propositions to where they are stored in the database.
     *
     * @param relationalDatabaseSpec a {@link RelationalDatabaseSpec}, the
     * specification of where propositions are stored in the database.
     */
    public RelationalDbDataSourceBackend(
            RelationalDatabaseSpec relationalDatabaseSpec) {
        this(relationalDatabaseSpec, null);
    }

    public RelationalDbDataSourceBackend(
            RelationalDatabaseSpec relationalDatabaseSpec,
            DatabaseAPI databaseAPI) {
        this.relationalDatabaseSpec = relationalDatabaseSpec;
        if (databaseAPI == null) {
            databaseAPI = DatabaseAPI.DRIVERMANAGER;
        }
        this.databaseAPI = databaseAPI;
    }

    @BackendProperty
    public final void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
    
    public final String getSchemaName() {
        return this.schemaName;
    }

    @BackendProperty
    public final void setKeyIdTable(String keyIdTable) {
        this.keyIdTable = keyIdTable;
    }
    
    public final String getKeyIdTable() {
        return keyIdTable;
    }

    @BackendProperty
    public final void setKeyIdColumn(String keyIdColumn) {
        this.keyIdColumn = keyIdColumn;
    }
    
    public final String getKeyIdColumn() {
        return keyIdColumn;
    }

    @BackendProperty
    public final void setKeyIdSchema(String keyIdSchema) {
        this.keyIdSchema = keyIdSchema;
    }

    public final String getKeyIdSchema() {
        if (this.keyIdSchema != null) {
            return this.keyIdSchema;
        } else {
            return this.schemaName;
        }
    }
    
    @BackendProperty
    public final void setKeyIdJoinKey(String keyIdJoinKey) {
        this.keyIdJoinKey = keyIdJoinKey;
    }

    public final String getKeyIdJoinKey() {
        return keyIdJoinKey;
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

        this.relationalDatabaseSpec = createRelationalDatabaseSpec();


    }

    public void setRelationalDatabaseSpec(RelationalDatabaseSpec spec) {
        this.sqlGenerator = null;
        this.relationalDatabaseSpec = spec;
    }

    public RelationalDatabaseSpec getRelationalDatabaseSpec() {
        return this.relationalDatabaseSpec;
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
     * or {@link javax.sql.DataSource}. If
     * <code>null</code>, the default is assigned
     * ({@link DatabaseAPI.DRIVERMANAGER}).
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
     * Sets the query timeout. If
     * <code>null</code>, no timeout will be set (the default).
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
     * number of seconds. A value of
     * <code>0</code> or a negative number (the default) means that no query
     * timeout is set.
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

    @Override
    public DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds, Set<String> propIds, Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        if (this.sqlGenerator == null) {
            try {
                ConnectionSpec connectionSpecInstance =
                        getConnectionSpecInstance();
                this.sqlGenerator = new SQLGeneratorFactory(connectionSpecInstance,
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
        if (this.keyIdTable != null && this.keyIdColumn != null) {
            try {
                ConnectionSpec connectionSpecInstance =
                            getConnectionSpecInstance();
                try (Connection con = connectionSpecInstance.getOrCreate()) {
                    try (Statement stmt = con.createStatement()) {
                        StringBuilder stmtBuilder = new StringBuilder();
                        stmtBuilder.append("DELETE FROM ");
                        if (this.keyIdSchema != null) {
                            stmtBuilder.append(this.keyIdSchema);
                            stmtBuilder.append('.');
                        }
                        stmtBuilder.append(this.keyIdTable);
                        stmt.execute(stmtBuilder.toString());
                    }
                }
            } catch (InvalidConnectionSpecArguments | SQLException ex) {
                throw new DataSourceWriteException("Could not delete all key ids in data source backend " + nameForErrors(), ex);
            }
        } else {
            SQLGenUtil.logger().log(Level.FINER, 
                    "Unable to delete all keys in keyIdSchema{0}, keyIdTable={1} and keyIdColumn={2}", 
                    new Object[]{this.keyIdSchema, this.keyIdTable, this.keyIdColumn});
        }
    }

    @Override
    public void writeKeys(List<Proposition> propositions) throws DataSourceWriteException {
        if (this.keyIdSchema != null && this.keyIdTable != null && this.keyIdColumn != null) {
            int batchSize = 1000;
            try {
                ConnectionSpec connectionSpecInstance =
                        getConnectionSpecInstance();
                String stmt = buildWriteKeysInsertStmt();
                try (Connection con = connectionSpecInstance.getOrCreate();
                        PreparedStatement prepareStatement = con.prepareStatement(stmt)) {
                    int i = 0;
                    for (Proposition proposition : propositions) {
                        if (proposition.getId().equals(getKeyType())) {
                            String[] dbIds = ((SQLGenUniqueId) proposition.getUniqueId().getLocalUniqueId()).getDbIds();
                            if (dbIds.length != 1) {
                                throw new DataSourceWriteException("Only one database key is allowed for " + this.getKeyTypePluralDisplayName());
                            }
                            prepareStatement.setObject(0, dbIds[0]);
                            prepareStatement.addBatch();
                            i++;
                            if (i % batchSize == 0) {
                                prepareStatement.executeBatch();
                            }
                        }
                    }
                    if (i % batchSize == 0) {
                        prepareStatement.executeBatch();
                    }
                }
            } catch (InvalidConnectionSpecArguments | SQLException ex) {
                throw new DataSourceWriteException("Could not write key ids in data source backend " + nameForErrors(), ex);
            }
        } else {
            SQLGenUtil.logger().log(Level.FINER, 
                    "Unable to write keys to keyIdSchema{0}, keyIdTable={1} and keyIdColumn={2}", 
                    new Object[]{this.keyIdSchema, this.keyIdTable, this.keyIdColumn});
        }
    }

    private String buildWriteKeysInsertStmt() {
        StringBuilder stmtBuilder = new StringBuilder();
        stmtBuilder.append("INSERT INTO ");
        if (this.keyIdSchema != null) {
            stmtBuilder.append(this.keyIdSchema);
            stmtBuilder.append('.');
        }
        stmtBuilder.append(this.keyIdTable);
        stmtBuilder.append('(');
        stmtBuilder.append(this.keyIdColumn);
        stmtBuilder.append(") VALUES (?)");
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
        this.relationalDatabaseSpec.validate(knowledgeSource);
    }

    @Override
    public void close() throws BackendCloseException {
        this.sqlGenerator = null;
    }

    protected ConnectionSpec getConnectionSpecInstance()
            throws InvalidConnectionSpecArguments {
        return this.databaseAPI.newConnectionSpecInstance(
                this.databaseId, this.username, this.password);
    }

    protected abstract EntitySpec[] constantSpecs() throws IOException;

    protected abstract EntitySpec[] eventSpecs() throws IOException;

    protected abstract EntitySpec[] primitiveParameterSpecs() throws IOException;

    protected abstract StagingSpec[] stagedSpecs() throws IOException;

    private RelationalDatabaseSpec createRelationalDatabaseSpec() {
        RelationalDatabaseSpec result = new RelationalDatabaseSpec();
        result.setUnits(getUnitFactory());
        result.setGranularities(getGranularityFactory());
        try {
            EntitySpec[] constantSpecs = constantSpecs();
            result.setConstantSpecs(constantSpecs);

            EntitySpec[] eventSpecs = eventSpecs();
            result.setEventSpecs(eventSpecs);

            EntitySpec[] primParamSpecs = primitiveParameterSpecs();
            result.setPrimitiveParameterSpecs(primParamSpecs);

            StagingSpec[] stagedSpecs = stagedSpecs();
            result.setStagedSpecs(stagedSpecs);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
}
