package org.protempa.bp.commons.dsb.sqlgen;

import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
final class ConstantRefResultProcessor extends
        RefResultProcessor<Constant> {

    @Override
    void addReference(
            Constant constantParameter, String referenceName, UniqueIdentifier uid) {
        constantParameter.addReference(referenceName, uid);
    }
}
