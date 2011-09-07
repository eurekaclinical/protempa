package org.protempa.bp.commons;

import org.protempa.backend.tsb.AbstractTermSourceBackend;
import org.protempa.backend.TermSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

public abstract class AbstractCommonsTermSourceBackend extends
        AbstractTermSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws TermSourceBackendInitializationException {
        CommonsBackend.initialize(this, config);
    }

    @Override
    public String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }
    
    protected final String nameForErrors() {
        return CommonsBackend.nameForErrors(this);
    }

}
