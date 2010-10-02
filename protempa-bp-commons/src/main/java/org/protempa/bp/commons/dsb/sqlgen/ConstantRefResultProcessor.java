package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.Constant;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
final class ConstantRefResultProcessor extends
        RefResultProcessor<Constant> {

    @Override
    void setReferencesForProposition(String referenceName, 
            Constant constantParameter, List<UniqueIdentifier> uids) {
        constantParameter.setReferences(referenceName, uids);
    }
}
