package org.protempa;


/**
 * The event generated when the knowledge source is updated.
 * 
 * @author Andrew Post
 */
public final class KnowledgeSourceUpdatedEvent extends
		SourceUpdatedEvent {

	private static final long serialVersionUID = 2757359115821263581L;

	/**
	 * Constructs an event with the source <code>KnowledgeSource</code> that
	 * generated the event.
	 * 
	 * @param knowledgeSourceBackend
	 *            an <code>KnowledgeSourceBackend</code>.
	 */
	public KnowledgeSourceUpdatedEvent(KnowledgeSource knowledgeSource) {
		super(knowledgeSource);
	}
}
