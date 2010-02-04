package org.protempa;


/**
 * The event generated when the algorithm source backend is updated.
 * 
 * @author Andrew Post
 */
public final class DataSourceBackendUpdatedEvent extends BackendUpdatedEvent {
    private static final long serialVersionUID = -2553743716414678296L;

	/**
	 * Constructs an event with the source <code>AlgorithmSourceBackend</code>
	 * that generated the event.
	 * 
	 * @param dataSourceBackend
	 *            {@link AbstractDataSourceBackend}.
	 */
	public DataSourceBackendUpdatedEvent(
			DataSourceBackend dataSourceBackend) {
		super(dataSourceBackend);
	}
	
}
