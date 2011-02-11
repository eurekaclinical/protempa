package org.protempa.bp.commons.dsb.sqlgen;

import org.protempa.proposition.Event;

/**
 * 
 * @author Andrew Post
 */
class EventResultProcessorFactory extends SQLGenResultProcessorFactory<Event> {

    @Override
    AbstractMainResultProcessor<Event> getInstance(String dataSourceBackendId,
            EntitySpec entitySpec, ResultCache<Event> cache) {

        EventResultProcessor resultProcessor = new EventResultProcessor();
        resultProcessor.setResults(cache);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<Event> getRefInstance(String dataSourceBackendId,
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            ResultCache<Event> cache) {
        EventRefResultProcessor resultProcessor = new EventRefResultProcessor();
        resultProcessor.setCache(cache);
        resultProcessor.setReferenceSpec(referenceSpec);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

}
