package org.protempa.bp.commons.dsb.relationaldb;

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

        PrimitiveParameterResultProcessor resultProcessor = 
                new PrimitiveParameterResultProcessor(cache, entitySpec,
                        dataSourceBackendId);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<PrimitiveParameter> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec, ResultCache<PrimitiveParameter> cache) {
        PrimitiveParameterRefResultProcessor resultProcessor = 
                new PrimitiveParameterRefResultProcessor(cache, 
                        referenceSpec, entitySpec, dataSourceBackendId);
        return resultProcessor;
    }

}
