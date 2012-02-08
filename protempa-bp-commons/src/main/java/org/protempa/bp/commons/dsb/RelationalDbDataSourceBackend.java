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
package org.protempa.bp.commons.dsb;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;
import org.protempa.DataSourceReadException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.QuerySession;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.DataSourceBackendFailedValidationException;
import org.protempa.backend.DataSourceBackendInitializationException;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.bp.commons.AbstractCommonsDataSourceBackend;
import org.protempa.bp.commons.BackendProperty;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec;
import org.protempa.bp.commons.dsb.relationaldb.EntitySpec;
import org.protempa.bp.commons.dsb.relationaldb.NoCompatibleSQLGeneratorException;
import org.protempa.bp.commons.dsb.relationaldb.RelationalDatabaseSpec;
import org.protempa.bp.commons.dsb.relationaldb.SQLGenerator;
import org.protempa.bp.commons.dsb.relationaldb.SQLGeneratorFactory;
import org.protempa.bp.commons.dsb.relationaldb.SQLGeneratorLoadException;
import org.protempa.bp.commons.dsb.relationaldb.StagingSpec;
import org.protempa.bp.commons.dsb.relationaldb.TableSpec;
import org.protempa.proposition.Proposition;

/**
 * Implements access to relational databases. Backend properties set the
 * database connection information. It uses a {@link SQLGenerator} service
 * loadable using Java's {@link java.util.ServiceLoader} for generating SQL
 * that is appropriate for your database and JDBC driver. Service providers are
 * built-in for MySQL 4.1 and 5 using version 5 of the Connector/J JDBC driver,
 * and Oracle 10g using the ojdbc6 driver.
 *
 * Two system properties control the behavior of the backend and are
 * useful for debugging. The
 * <code>protempa.dsb.relationaldatabase.sqlgenerator</code> property can be
 * set with the full class name of a {@link SQLGenerator} service provider to
 * force its use. This circumvents the built-in algorithm for picking a
 * service provider to use. The
 * <code>protempa.dsb.relationaldatabase.skipexecution</code> property can be
 * set to <code>true</code> to cause the backend to generate SQL queries but
 * not execute them. Together with turning on logging to see the SQL queries,
 * this can be useful for debugging the generated SQL without having to wait
 * for them to execute.
 *
 * The backend and various classes that it invokes support extensive logging.
 * Logging at the FINE level on the
 * <code>org.protempa.bp.commons.dsb.sqlgen</code> package will activate
 * logging of the generated SQL queries.
 * 
 * @author Andrew Post
 */
public abstract class RelationalDbDataSourceBackend
        extends AbstractCommonsDataSourceBackend {

    private DatabaseAPI databaseAPI;
    private String databaseId;
    private String username;
    private String password;
    private RelationalDatabaseSpec relationalDatabaseSpec;
    private SQLGenerator sqlGenerator;

    public RelationalDbDataSourceBackend() {
        this(null);
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

    public abstract String getSchemaName();

    public String getKeyIdSchema() {
        return getSchemaName();
    }

    public abstract String getKeyIdTable();

    public abstract String getKeyIdColumn();

    public abstract String getKeyIdJoinKey();

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
    public final void initialize(BackendInstanceSpec config)
            throws DataSourceBackendInitializationException {
        super.initialize(config);
        
        this.relationalDatabaseSpec = createRelationalDatabaseSpec();
        
        try {
            ConnectionSpec connectionSpecInstance =
                    this.databaseAPI.newConnectionSpecInstance(
                    this.databaseId, this.username, this.password);
            this.sqlGenerator = new SQLGeneratorFactory(connectionSpecInstance,
                    this).newInstance();
        } catch (InvalidConnectionSpecArguments ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not initialize data source backend " +
                    nameForErrors(), ex);
        } catch (NoCompatibleSQLGeneratorException ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not initialize data source backend " +
                    nameForErrors(), ex);
        } catch (SQLGeneratorLoadException ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not initialize data source backend " +
                    nameForErrors(), ex);
        } catch (SQLException ex) {
            throw new DataSourceBackendInitializationException(
                    "Could not initialize data source backend " +
                    nameForErrors(), ex);
        }
    }

    public void setRelationalDatabaseSpec(RelationalDatabaseSpec spec) {
        if (this.sqlGenerator != null) {
            throw new IllegalStateException(
                    "cannot set this field after initialize has been called");
        }
        this.relationalDatabaseSpec = spec;
    }

    public RelationalDatabaseSpec getRelationalDatabaseSpec() {
        return this.relationalDatabaseSpec;
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
     * ({@link java.sql.DriverManager} or {@link javax.sql.DataSource}. If
     * <code>null</code>, the default is assigned
     * ({@link DatabaseAPI.DRIVERMANAGER}).
     *
     * @param databaseAPI a {@link DatabaseAPI}.
     */
    public void setDatabaseAPI(DatabaseAPI databaseAPI) {
        if (this.sqlGenerator != null) {
            throw new IllegalStateException(
                    "cannot set this field after initialize has been called");
        }
        if (databaseAPI == null) {
            databaseAPI = DatabaseAPI.DRIVERMANAGER;
        }
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
        if (this.sqlGenerator != null) {
            throw new IllegalStateException(
                    "cannot set this field after initialize has been called");
        }
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
        if (this.sqlGenerator != null) {
            throw new IllegalStateException(
                    "cannot set this field after initialize has been called");
        }
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
        if (this.sqlGenerator != null) {
            throw new IllegalStateException(
                    "cannot set this field after initialize has been called");
        }
        this.password = password;
    }

    @Override
    public Map<String, List<Proposition>> readPropositions(
            Set<String> keyIds, Set<String> paramIds,
            Filter filters,
            QuerySession qs)
            throws DataSourceReadException {
        return this.sqlGenerator.readPropositions(keyIds, paramIds,
                filters, null).getPatientCache();
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException,
            KnowledgeSourceReadException{
        this.relationalDatabaseSpec.validate(knowledgeSource);
    }

    @Override
    public void close() {
        this.sqlGenerator = null;
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
