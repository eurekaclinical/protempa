package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
final class ConstantRefResultProcessor extends
        RefResultProcessor<Constant> {

    ConstantRefResultProcessor(ResultCache<Constant> results, 
            ReferenceSpec referenceSpec, EntitySpec entitySpec, 
            String dataSourceBackendId) {
        super(results, referenceSpec, entitySpec, dataSourceBackendId);
    }

    @Override
    void addReferences(Constant constant, List<UniqueId> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueId uid : uids) {
            constant.addReference(referenceName, uid);
        }
    }
}
