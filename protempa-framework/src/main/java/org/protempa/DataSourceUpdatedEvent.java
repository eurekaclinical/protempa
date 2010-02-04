package org.protempa;


/**
 * The event generated when a data source is updated.
 * 
 * @author Andrew Post
 */
public final class DataSourceUpdatedEvent extends SourceUpdatedEvent {
	
	private static final long serialVersionUID = 9064890260294835226L;

	/**
	 * Constructs an event with the source <code>DataSource</code> that
	 * generated the event.
	 * 
	 * @param dataSource
	 *            a <code>DataSource</code>.
	 */
	public DataSourceUpdatedEvent(DataSource dataSource) {
		super(dataSource);
	}

}
