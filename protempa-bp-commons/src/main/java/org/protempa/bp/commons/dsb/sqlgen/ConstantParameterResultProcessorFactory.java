package org.protempa.bp.commons.dsb.sqlgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
class ConstantParameterResultProcessorFactory 
        extends SQLGenResultProcessorFactory<ConstantParameter> {

    @Override
    AbstractMainResultProcessor<ConstantParameter> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec) {
        Map<String, List<ConstantParameter>> results =
                new HashMap<String, List<ConstantParameter>>();

        ConstantParameterResultProcessor resultProcessor =
                new ConstantParameterResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<ConstantParameter> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec,
            Map<UniqueIdentifier,ConstantParameter> cache) {
        ConstantParameterRefResultProcessor resultProcessor =
                new ConstantParameterRefResultProcessor();
        resultProcessor.setCache(cache);
        resultProcessor.setReferenceSpec(referenceSpec);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

}
