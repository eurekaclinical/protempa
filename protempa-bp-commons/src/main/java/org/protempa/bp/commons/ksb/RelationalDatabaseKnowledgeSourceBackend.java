package org.protempa.bp.commons.ksb;

import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.EventDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceBackendInitializationException;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.AbstractCommonsKnowledgeSourceBackend;
import org.protempa.bp.commons.BackendProperty;
import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;

/**
 * Implements using database tables containing a value set as metadata.
 * 
 * @author Andrew Post
 */
public abstract class RelationalDatabaseKnowledgeSourceBackend
        extends AbstractCommonsKnowledgeSourceBackend {

    private DatabaseAPI databaseAPI;
    private String databaseId;
    private String username;
    private String password;
    private ConnectionSpec connectionSpecInstance;

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
     * Gets the databaseId for a database.
     *
     * @return a databaseId {@link String}.
     */
    public String getDatabaseId() {
        return this.databaseId;
    }

    /**
     * Sets the database id for a database. This must be set to something
     * not <code>null</code>.
     *
     * @param databaseId a database id {@link String}.
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
    public void initialize(BackendInstanceSpec config)
            throws KnowledgeSourceBackendInitializationException {
        super.initialize(config);

        if (this.databaseAPI == null) {
            throw new KnowledgeSourceBackendInitializationException(
                    "A Java database API (DriverManager or DataSource) must "
                    + "be specified in this backend's configuration");
        }

        try {
            this.connectionSpecInstance =
                    this.databaseAPI.newConnectionSpecInstance(
                    this.databaseId, this.username, this.password);
        } catch (InvalidConnectionSpecArguments ex) {
            throw new KnowledgeSourceBackendInitializationException(
                    "Could not initialize database connection information",
                    ex);
        }
    }

    @Override
    public void close() {
        
    }

    @Override
    public EventDefinition readEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

}
