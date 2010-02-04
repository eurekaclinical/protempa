package org.protempa;


/**
 * The event generated when the algorithm source is updated.
 * 
 * @author Andrew Post
 */
public final class AlgorithmSourceUpdatedEvent extends SourceUpdatedEvent {
	
	private static final long serialVersionUID = 2757359115821263581L;

	/**
	 * Constructs an event with the source <code>AlgorithmSource</code> that
	 * generated the event.
	 * 
	 * @param algorithmSourceBackend
	 *            an <code>AlgorithmSourceBackend</code>.
	 */
	public AlgorithmSourceUpdatedEvent(AlgorithmSource algorithmSource) {
		super(algorithmSource);
	}
}
