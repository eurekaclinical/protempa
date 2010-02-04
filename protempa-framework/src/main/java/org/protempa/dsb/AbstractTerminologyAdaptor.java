package org.protempa.dsb;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeletal implementation of the <code>TerminologyAdaptor</code> interface to
 * minimize the effort required to implement this interface.
 * 
 * @author Andrew Post
 */
public abstract class AbstractTerminologyAdaptor implements TerminologyAdaptor {
	private final List<TerminologyAdaptorListener> listenerList =
            new ArrayList<TerminologyAdaptorListener>();

	/**
	 * Implemented as a no-op.
	 * 
	 * @see org.protempa.TerminologyAdaptor#close()
	 */
	public void close() {

	}
    
	public void addTerminologyAdaptorUpdatedListener(
			TerminologyAdaptorListener listener) {
		if (listener != null) {
			this.listenerList.add(listener);
		}
	}

	public void removeTerminologyAdaptorUpdatedListener(
			TerminologyAdaptorListener listener) {
		this.listenerList.remove(listener);
	}

	/**
	 * Notifies registered listeners that the terminology adaptor has been updated.
	 * 
	 * @see org.protempa.TerminologyAdaptorUpdatedEvent
	 * @see org.protempa.TerminologyAdaptorListener
	 */
	protected void fireTerminologyAdaptorUpdated() {
		TerminologyAdaptorUpdatedEvent e = new TerminologyAdaptorUpdatedEvent(
				this);
		for (int i = 0, n = this.listenerList.size(); i < n; i++) {
			TerminologyAdaptorListener l = this.listenerList.get(i);
			l.terminologyAdaptorUpdated(e);
		}
	}

}
