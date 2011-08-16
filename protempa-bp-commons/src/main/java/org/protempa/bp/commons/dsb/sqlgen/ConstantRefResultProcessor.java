package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
final class ConstantRefResultProcessor extends
        RefResultProcessor<Constant> {

//    @Override
//    void addReference(
//            Constant constantParameter, String referenceName, UniqueIdentifier uid) {
//        constantParameter.addReference(referenceName, uid);
//    }

    @Override
    void addReferences(Constant constant, List<UniqueId> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueId uid : uids) {
            constant.addReference(referenceName, uid);
        }
    }
}
