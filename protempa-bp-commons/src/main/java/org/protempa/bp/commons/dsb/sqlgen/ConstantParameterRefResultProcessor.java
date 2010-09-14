package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
final class ConstantParameterRefResultProcessor extends 
        RefResultProcessor<ConstantParameter> {

    @Override
    void setReferencesForProposition(String referenceName, 
            ConstantParameter constantParameter, List<UniqueIdentifier> uids) {
        constantParameter.setReferences(referenceName, uids);
    }
}
