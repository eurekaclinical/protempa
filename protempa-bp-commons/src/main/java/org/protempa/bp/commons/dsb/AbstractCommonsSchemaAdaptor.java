/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.protempa.bp.commons.dsb;

import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.SchemaAdaptorProperty;
import org.protempa.dsb.AbstractSchemaAdaptor;
import org.protempa.dsb.SchemaAdaptorInitializationException;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsSchemaAdaptor
        extends AbstractSchemaAdaptor implements CommonsSchemaAdaptor {

    public void initialize(BackendInstanceSpec backendInstanceSpec)
            throws SchemaAdaptorInitializationException {
        DSBUtil.initializeAdaptorFields(SchemaAdaptorProperty.class, this,
                backendInstanceSpec);
    }


}
