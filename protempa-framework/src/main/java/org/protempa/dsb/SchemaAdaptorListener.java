package org.protempa.dsb;

import java.util.EventListener;

/**
 * Listener interface for when the schema adaptor changes.
 * 
 * @author Andrew Post
 */
public interface SchemaAdaptorListener extends EventListener {
	/**
	 * The method that gets called when the schema adaptor is updated.
	 * 
	 * @param event
	 *            a <code>SchemaAdaptorUpdatedEvent</code>.
	 */
	void schemaAdaptorUpdated(SchemaAdaptorUpdatedEvent event);
}
