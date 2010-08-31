package org.protempa.bp.commons;

import org.protempa.AbstractDataSourceBackend;
import org.protempa.DataSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsDataSourceBackend
        extends AbstractDataSourceBackend  {

    private String dataSourceBackendId;

    @Override
    public void initialize(BackendInstanceSpec config)
        throws DataSourceBackendInitializationException {
        CommonsBackend.initialize(this, config);
        if (this.dataSourceBackendId == null)
            throw new DataSourceBackendInitializationException(
                    "dataSourceBackendId is not set");
    }

    @Override
    public final String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }

    protected final String nameForErrors() {
        return CommonsBackend.nameForErrors(this);
    }

    @BackendProperty
    public final void setDataSourceBackendId(String id) {
        this.dataSourceBackendId = id;
    }

    public final String getDataSourceBackendId () {
        return this.dataSourceBackendId;
    }
}
