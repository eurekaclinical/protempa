package org.protempa.bp.commons.dsb.sqlgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
class EventResultProcessorFactory 
        extends SQLGenResultProcessorFactory<Event> {

    @Override
    AbstractMainResultProcessor<Event> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec) {
        Map<String, List<Event>> results =
                new HashMap<String, List<Event>>();

        EventResultProcessor resultProcessor =
                new EventResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<Event> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec,
            Map<UniqueIdentifier,Event> cache) {
        EventRefResultProcessor resultProcessor =
                new EventRefResultProcessor();
        resultProcessor.setCache(cache);
        resultProcessor.setReferenceSpec(referenceSpec);
        resultProcessor.setDataSourceBackendId(dataSourceBackendId);
        resultProcessor.setEntitySpec(entitySpec);
        return resultProcessor;
    }

}
