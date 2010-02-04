package org.protempa.dsb;

import java.util.EventObject;

/**
 * The event generated when the schema adaptor is updated.
 * 
 * @author Andrew Post
 */
public final class SchemaAdaptorUpdatedEvent extends EventObject {
	
	private static final long serialVersionUID = 9064890260294835226L;

	/**
	 * Constructs an event with the source <code>SchemaAdaptor</code> that
	 * generated the event.
	 * 
	 * @param schemaAdaptor
	 *            an <code>SchemaAdaptor</code>.
	 */
	public SchemaAdaptorUpdatedEvent(SchemaAdaptor schemaAdaptor) {
		super(schemaAdaptor);
	}

	/**
	 * Returns the source <code>SchemaAdaptor</code> (the same as what
	 * <code>getSource()</code> returns).
	 * 
	 * @return a <code>SchemaAdaptor</code>.
	 */
	public SchemaAdaptor getSchemaAdaptor() {
		return (SchemaAdaptor) this.getSource();
	}

}
