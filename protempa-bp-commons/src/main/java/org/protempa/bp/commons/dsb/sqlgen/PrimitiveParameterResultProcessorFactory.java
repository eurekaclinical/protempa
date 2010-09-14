package org.protempa.bp.commons.dsb.sqlgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
class PrimitiveParameterResultProcessorFactory 
        extends SQLGenResultProcessorFactory<PrimitiveParameter> {

    @Override
    AbstractMainResultProcessor<PrimitiveParameter> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec) {
        Map<String, List<PrimitiveParameter>> results =
                new HashMap<String, List<PrimitiveParameter>>();

        PrimitiveParameterResultProcessor resultProcessor =
                new PrimitiveParameterResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<PrimitiveParameter> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec,
            Map<UniqueIdentifier,PrimitiveParameter> cache) {
        PrimitiveParameterRefResultProcessor resultProcessor =
                new PrimitiveParameterRefResultProcessor();
        resultProcessor.setCache(cache);
        resultProcessor.setReferenceSpec(referenceSpec);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

}
