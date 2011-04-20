package org.protempa.bp.commons.dsb.sqlgen;

import org.protempa.proposition.PrimitiveParameter;

/**
 * 
 * @author Andrew Post
 */
class PrimitiveParameterResultProcessorFactory extends
        SQLGenResultProcessorFactory<PrimitiveParameter> {

    @Override
    MainResultProcessor<PrimitiveParameter> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ResultCache<PrimitiveParameter> cache) {

        PrimitiveParameterResultProcessor resultProcessor = new PrimitiveParameterResultProcessor();
        resultProcessor.setResults(cache);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<PrimitiveParameter> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec, ResultCache<PrimitiveParameter> cache) {
        PrimitiveParameterRefResultProcessor resultProcessor = new PrimitiveParameterRefResultProcessor();
        resultProcessor.setCache(cache);
        resultProcessor.setReferenceSpec(referenceSpec);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

}
