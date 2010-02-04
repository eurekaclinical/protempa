package org.protempa;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Common interface for backend listeners.
 * 
 * @author Andrew Post
 * 
 * @param <E>
 *            an {@link EventObject}
 */
public interface BackendListener<E extends BackendUpdatedEvent> extends
		EventListener {
	/**
	 * Notifies a listener when it needs to reread data in the backend.
	 * 
	 * @param evt
	 *            an {@link EventObject}
	 */
	void backendUpdated(E evt);
}
