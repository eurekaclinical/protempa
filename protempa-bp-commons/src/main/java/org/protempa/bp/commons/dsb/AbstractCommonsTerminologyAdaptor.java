package org.protempa.bp.commons.dsb;

import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.TerminologyAdaptorProperty;
import org.protempa.dsb.AbstractTerminologyAdaptor;
import org.protempa.dsb.TerminologyAdaptorInitializationException;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsTerminologyAdaptor
        extends AbstractTerminologyAdaptor 
        implements CommonsTerminologyAdaptor {
    public void initialize(BackendInstanceSpec config)
            throws TerminologyAdaptorInitializationException {
        DSBUtil.initializeAdaptorFields(TerminologyAdaptorProperty.class,
                this, config);
    }
}
