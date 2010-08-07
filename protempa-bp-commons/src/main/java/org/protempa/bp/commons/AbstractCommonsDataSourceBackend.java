package org.protempa.bp.commons;

import org.protempa.AbstractDataSourceBackend;
import org.protempa.DataSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsDataSourceBackend
        extends AbstractDataSourceBackend implements CommonsDataSourceBackend {

    public void initialize(BackendInstanceSpec config)
        throws DataSourceBackendInitializationException {
        BackendPropertyInitializer.initialize(this, config);
    }
}
