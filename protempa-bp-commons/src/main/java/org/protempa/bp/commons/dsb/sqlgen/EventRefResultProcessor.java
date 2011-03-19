package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueIdentifier;

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
    void addReferences(Event event, List<UniqueIdentifier> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueIdentifier uid : uids) {
            event.addReference(referenceName, uid);
        }
    }
}
