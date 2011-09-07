package org.protempa.bp.commons;

import org.protempa.backend.ksb.AbstractKnowledgeSourceBackend;
import org.protempa.backend.KnowledgeSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsKnowledgeSourceBackend
        extends AbstractKnowledgeSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws KnowledgeSourceBackendInitializationException {
        CommonsBackend.initialize(this, config);
    }

    @Override
    public final String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }

    protected final String nameForErrors() {
        return CommonsBackend.nameForErrors(this);
    }

}
