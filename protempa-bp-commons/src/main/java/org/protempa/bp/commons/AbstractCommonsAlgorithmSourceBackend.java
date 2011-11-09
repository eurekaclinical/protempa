package org.protempa.bp.commons;

import org.protempa.backend.asb.AbstractAlgorithmSourceBackend;
import org.protempa.backend.AlgorithmSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsAlgorithmSourceBackend
        extends AbstractAlgorithmSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec<?> backendInstanceSpec)
            throws AlgorithmSourceBackendInitializationException {
        CommonsBackend.initialize(this, backendInstanceSpec);
    }

    @Override
    public final String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }

    protected final String nameForErrors() {
        return CommonsBackend.nameForErrors(this);
    }

}
