package org.protempa;

/**
 * The event generated when the algorithm source backend is updated.
 * 
 * @author Andrew Post
 */
public final class AlgorithmSourceBackendUpdatedEvent extends
		BackendUpdatedEvent {

	private static final long serialVersionUID = 9064890260294835226L;

	/**
	 * Constructs an event with the source <code>AlgorithmSourceBackend</code>
	 * that generated the event.
	 * 
	 * @param algorithmSourceBackend
	 *            an <code>AlgorithmSourceBackend</code>.
	 */
	public AlgorithmSourceBackendUpdatedEvent(
			AlgorithmSourceBackend algorithmSourceBackend) {
		super(algorithmSourceBackend);
	}

}
