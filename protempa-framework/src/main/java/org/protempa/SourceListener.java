package org.protempa;

import java.util.EventListener;

/**
 * Listener interface for when the source changes.
 * 
 * @author Andrew Post
 */
public interface SourceListener<E extends SourceUpdatedEvent> extends EventListener {
	/**
	 * The method that gets called when the source is updated.
	 * 
	 * @param event
	 *            a {@link SourceUpdatedEvent}.
	 */
	void sourceUpdated(E event);
}
