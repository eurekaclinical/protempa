package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.ConstantProposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
final class ConstantRefResultProcessor extends
        RefResultProcessor<ConstantProposition> {

    @Override
    void setReferencesForProposition(String referenceName, 
            ConstantProposition constantParameter, List<UniqueIdentifier> uids) {
        constantParameter.setReferences(referenceName, uids);
    }
}
