package org.protempa.bp.commons.dsb.sqlgen;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
final class PrimitiveParameterRefResultProcessor extends
        RefResultProcessor<PrimitiveParameter> {

    @Override
    void addReferenceForProposition(String referenceName, 
            PrimitiveParameter primitiveParameter, 
            UniqueIdentifier uid) {
        primitiveParameter.addReference(referenceName, uid);
    }
}
