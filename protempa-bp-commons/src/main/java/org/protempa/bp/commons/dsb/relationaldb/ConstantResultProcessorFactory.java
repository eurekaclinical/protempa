package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.proposition.Constant;

/**
 *
 * @author Andrew Post
 */
class ConstantResultProcessorFactory
        extends SQLGenResultProcessorFactory<Constant> {

    @Override
    MainResultProcessor<Constant> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec, 
            ResultCache<Constant> cache) {

        ConstantResultProcessor resultProcessor =
                new ConstantResultProcessor(cache, entitySpec, 
                        dataSourceBackendId);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<Constant> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec,
            ResultCache<Constant> cache) {
        ConstantRefResultProcessor resultProcessor =
                new ConstantRefResultProcessor(cache, referenceSpec,
                        entitySpec, dataSourceBackendId);
        return resultProcessor;
    }

}
