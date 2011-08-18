package org.protempa.bp.commons.dsb.relationaldb;

import org.protempa.proposition.Event;

/**
 * 
 * @author Andrew Post
 */
class EventResultProcessorFactory extends SQLGenResultProcessorFactory<Event> {

    @Override
    MainResultProcessor<Event> getInstance(String dataSourceBackendId,
            EntitySpec entitySpec, ResultCache<Event> cache) {

        EventResultProcessor resultProcessor = 
                new EventResultProcessor(cache, entitySpec, 
                        dataSourceBackendId);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<Event> getRefInstance(String dataSourceBackendId,
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            ResultCache<Event> cache) {
        EventRefResultProcessor resultProcessor = 
                new EventRefResultProcessor(cache, referenceSpec,
                        entitySpec, dataSourceBackendId);
        return resultProcessor;
    }

}
