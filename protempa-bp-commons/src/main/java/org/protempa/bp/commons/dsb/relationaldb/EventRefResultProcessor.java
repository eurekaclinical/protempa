package org.protempa.bp.commons.dsb.relationaldb;

import java.util.List;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
final class EventRefResultProcessor extends RefResultProcessor<Event> {

    EventRefResultProcessor(ResultCache<Event> results, 
            ReferenceSpec referenceSpec, EntitySpec entitySpec, 
            String dataSourceBackendId) {
        super(results, referenceSpec, entitySpec, dataSourceBackendId);
    }

    @Override
    void addReferences(Event event, List<UniqueId> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueId uid : uids) {
            event.addReference(referenceName, uid);
        }
    }
}
