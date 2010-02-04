package org.protempa;

/**
 * The event generated when the knowledge source backend is updated.
 * 
 * @author Andrew Post
 */
public final class KnowledgeSourceBackendUpdatedEvent extends
		BackendUpdatedEvent {

	private static final long serialVersionUID = 2757359115821263581L;

	/**
	 * Constructs an event with the source <code>KnowledgeSourceBackend</code>
	 * that generated the event.
	 * 
	 * @param knowledgeSourceBackend
	 *            an <code>KnowledgeSourceBackend</code>.
	 */
	public KnowledgeSourceBackendUpdatedEvent(
			KnowledgeSourceBackend knowledgeSourceBackend) {
		super(knowledgeSourceBackend);
	}
}
