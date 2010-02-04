package org.protempa.dsb;

import java.util.EventObject;

/**
 * The event generated when the terminology adaptor changes.
 * 
 * @author Andrew Post
 */
public final class TerminologyAdaptorUpdatedEvent extends EventObject {
	
	private static final long serialVersionUID = 9064890260294835226L;

	/**
	 * Constructs an event with the source <code>TerminologyAdaptor</code>
	 * that generated the event.
	 * 
	 * @param terminologyAdaptor
	 *            an <code>TerminologyAdaptor</code>.
	 */
	public TerminologyAdaptorUpdatedEvent(TerminologyAdaptor terminologyAdaptor) {
		super(terminologyAdaptor);
	}

	/**
	 * Returns the source <code>DadtaSourceBackend</code> (the same as what
	 * <code>getSource()</code> returns).
	 * 
	 * @return an <code>DataSourceBackend</code>.
	 */
	public TerminologyAdaptor getTerminologyAdaptor() {
		return (TerminologyAdaptor) this.getSource();
	}

}
