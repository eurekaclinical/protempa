package org.protempa.bp.commons;

import org.protempa.AbstractKnowledgeSourceBackend;
import org.protempa.KnowledgeSourceBackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsKnowledgeSourceBackend
        extends AbstractKnowledgeSourceBackend {

    public void initialize(BackendInstanceSpec config)
            throws KnowledgeSourceBackendInitializationException {
        BackendPropertyInitializer.initialize(this, config);
    }

}
