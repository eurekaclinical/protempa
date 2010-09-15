package org.protempa.tsb.umls;

import org.arp.javautil.sql.DatabaseAPI;
import org.protempa.TermSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.AbstractCommonsTermSourceBackend;
import org.protempa.bp.commons.BackendProperty;

import edu.emory.cci.aiw.umls.UMLSDatabaseConnection;
import edu.emory.cci.aiw.umls.UMLSQueryExecutor;

public final class UMLSTermSourceBackend extends
        AbstractCommonsTermSourceBackend {

    private DatabaseAPI databaseAPI;
    private String databaseId;
    private String username;
    private String password;
    private UMLSQueryExecutor umls;

    @Override
    public void initialize(BackendInstanceSpec config)
            throws TermSourceBackendInitializationException {
        super.initialize(config);

        if (this.databaseAPI == null) {
            throw new TermSourceBackendInitializationException("Term source backend "
                    + nameForErrors()
                    + " requires a Java database API (DriverManager or "
                    + "DataSource) to be specified in its configuration");
        }
        umls = UMLSDatabaseConnection.getConnection(
                databaseAPI, getDatabaseId(), username, password);
    }

    /**
     * @return the databaseAPI
     */
    public DatabaseAPI getDatabaseAPI() {
        return databaseAPI;
    }

    /**
     * @param databaseAPI
     *            the databaseAPI to set
     */
    @BackendProperty
    public void setDatabaseAPI(DatabaseAPI databaseAPI) {
        this.databaseAPI = databaseAPI;
    }

    /**
     * @return the databaseId
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * @param databaseId
     *            the databaseId to set
     */
    @BackendProperty
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    @BackendProperty
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    @BackendProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
