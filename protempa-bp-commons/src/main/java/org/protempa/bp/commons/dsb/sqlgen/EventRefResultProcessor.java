package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
final class EventRefResultProcessor extends RefResultProcessor<Event> {

//    @Override
//    void addReference( Event event, String referenceName,
//            UniqueIdentifier uid) {
//        event.addReference(referenceName, uid);
//    }

    @Override
    void addReferences(Event event, List<UniqueId> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueId uid : uids) {
            event.addReference(referenceName, uid);
        }
    }
}
