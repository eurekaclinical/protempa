package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
final class EventRefResultProcessor extends RefResultProcessor<Event> {

    @Override
    void setReferencesForProposition(String referenceName, Event event,
            List<UniqueIdentifier> uids) {
        event.setReferences(referenceName, uids);
    }
}
