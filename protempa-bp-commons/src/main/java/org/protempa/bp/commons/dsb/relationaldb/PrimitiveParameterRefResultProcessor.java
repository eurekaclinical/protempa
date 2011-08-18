package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
final class PrimitiveParameterRefResultProcessor extends
        RefResultProcessor<PrimitiveParameter> {

    PrimitiveParameterRefResultProcessor(
            ResultCache<PrimitiveParameter> results, 
            ReferenceSpec referenceSpec, EntitySpec entitySpec, 
            String dataSourceBackendId) {
        super(results, referenceSpec, entitySpec, dataSourceBackendId);
    }

    @Override
    void addReferences(PrimitiveParameter primitiveParameter,
            List<UniqueId> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueId uid : uids) {
            primitiveParameter.addReference(referenceName, uid);
        }
    }
}
