package org.protempa;

import java.util.EventListener;

/**
 * Listener interface for when the source changes.
 * 
 * @author Andrew Post
 */
public interface SourceListener<E extends SourceUpdatedEvent>
        extends EventListener {

    /**
     * The method that gets called when the source is updated.
     *
     * @param event
     *            a {@link SourceUpdatedEvent}.
     */
    void sourceUpdated(E event);

    /**
     * The method that gets called when the source is closed unexpectedly, like
     * when a connection to a server is lost.
     *
     * @param e a {@link SourceClosedUnexpectedlyEvent}.
     */
    void closedUnexpectedly(SourceClosedUnexpectedlyEvent e);
}
