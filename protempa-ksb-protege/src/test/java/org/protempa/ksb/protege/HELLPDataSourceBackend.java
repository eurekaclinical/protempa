package org.protempa.ksb.protege;

import org.protempa.bp.commons.AbstractCommonsDataSourceBackend;
import org.protempa.bp.commons.BackendInfo;
import org.protempa.bp.commons.BackendProperty;

/**
 *
 * @author Andrew Post
 */
@BackendInfo (
    displayName="HELLP Data"
)
public class HELLPDataSourceBackend extends AbstractCommonsDataSourceBackend {
    
    private String driverClass;

    private String dbUrl;

    private String username;
    
    private String password;

    public String getDbUrl() {
        return dbUrl;
    }

    @BackendProperty
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDriverClass() {
        return driverClass;
    }

    @BackendProperty
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getPassword() {
        return password;
    }

    @BackendProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    @BackendProperty
    public void setUsername(String username) {
        this.username = username;
    }

    public HELLPDataSourceBackend() {
        super(new HELLPSchemaAdaptor());
    }

}
