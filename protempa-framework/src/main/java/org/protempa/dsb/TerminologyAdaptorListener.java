package org.protempa.dsb;

import java.util.EventListener;

/**
 * Listener interface for when the terminology adaptor changes.
 * 
 * @author Andrew Post
 */
public interface TerminologyAdaptorListener extends EventListener {
	/**
	 * The method that gets called when the terminology adaptor changes.
	 * 
	 * @param event
	 *            a <code>TerminologyAdaptorUpdatedEvent</code>.
	 */
	void terminologyAdaptorUpdated(TerminologyAdaptorUpdatedEvent event);
}
