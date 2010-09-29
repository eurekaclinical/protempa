package org.protempa.bp.commons.dsb.sqlgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.ConstantProposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
class ConstantResultProcessorFactory
        extends SQLGenResultProcessorFactory<ConstantProposition> {

    @Override
    AbstractMainResultProcessor<ConstantProposition> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec) {
        Map<String, List<ConstantProposition>> results =
                new HashMap<String, List<ConstantProposition>>();

        ConstantResultProcessor resultProcessor =
                new ConstantResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<ConstantProposition> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec,
            Map<UniqueIdentifier,ConstantProposition> cache) {
        ConstantRefResultProcessor resultProcessor =
                new ConstantRefResultProcessor();
        resultProcessor.setCache(cache);
        resultProcessor.setReferenceSpec(referenceSpec);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

}
