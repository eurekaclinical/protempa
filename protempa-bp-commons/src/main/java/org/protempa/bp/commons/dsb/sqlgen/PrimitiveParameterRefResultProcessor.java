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

//    @Override
//    void addReference(
//            PrimitiveParameter primitiveParameter, String referenceName,
//            UniqueIdentifier uid) {
//        primitiveParameter.addReference(referenceName, uid);
//    }

    @Override
    void addReferences(PrimitiveParameter primitiveParameter,
            List<UniqueIdentifier> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueIdentifier uid : uids) {
            primitiveParameter.addReference(referenceName, uid);
        }
    }
}
