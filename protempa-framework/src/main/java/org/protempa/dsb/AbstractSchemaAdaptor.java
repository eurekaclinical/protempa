package org.protempa.dsb;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeletal implementation of the <code>SchemaAdaptor</code> interface to
 * minimize the effort required to implement this interface.
 * 
 * @author Andrew Post
 */
public abstract class AbstractSchemaAdaptor implements SchemaAdaptor {
	private final List<SchemaAdaptorListener> listenerList =
            new ArrayList<SchemaAdaptorListener>();



	/**
	 * Implemented as a no-op.
	 * 
	 * @see org.protempa.SchemaAdaptor#close()
	 */
	public void close() {

	}

	public void addSchemaAdaptorListener(SchemaAdaptorListener listener) {
		if (listener != null) {
			this.listenerList.add(listener);
		}
	}
    
	public void removeSchemaAdaptorListener(SchemaAdaptorListener listener) {
		this.listenerList.remove(listener);
	}

	/**
	 * Notifies registered listeners that the schema adaptor has been updated.
	 * 
	 * @see org.protempa.SchemaAdaptorUpdatedEvent
	 * @see org.protempa.SchemaAdaptorListener
	 */
	protected void fireSchemaAdaptorUpdated() {
		SchemaAdaptorUpdatedEvent e = new SchemaAdaptorUpdatedEvent(this);
		for (int i = 0, n = this.listenerList.size(); i < n; i++) {
			this.listenerList.get(i).schemaAdaptorUpdated(e);
		}
	}

}
