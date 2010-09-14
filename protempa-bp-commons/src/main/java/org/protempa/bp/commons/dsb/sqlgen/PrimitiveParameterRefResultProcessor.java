package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
final class PrimitiveParameterRefResultProcessor extends
        RefResultProcessor<PrimitiveParameter> {

    @Override
    void setReferencesForProposition(String referenceName, 
            PrimitiveParameter primitiveParameter, 
            List<UniqueIdentifier> uids) {
        primitiveParameter.setReferences(referenceName, uids);
    }
}
