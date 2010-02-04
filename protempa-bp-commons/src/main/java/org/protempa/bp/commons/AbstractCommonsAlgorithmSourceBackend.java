package org.protempa.bp.commons;

import org.protempa.AbstractAlgorithmSourceBackend;
import org.protempa.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsAlgorithmSourceBackend
        extends AbstractAlgorithmSourceBackend {

    public void initialize(BackendInstanceSpec backendInstanceSpec)
            throws BackendInitializationException {
        CommonsUtil.initializeBackendProperties(this, backendInstanceSpec);
    }

}
