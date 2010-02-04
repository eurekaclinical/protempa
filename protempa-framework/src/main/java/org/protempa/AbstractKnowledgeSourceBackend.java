package org.protempa;

/**
 * Skeletal implementation of the <code>KnowledgeSourceBackend</code>
 * interface to minimize the effort required to implement this interface.
 * 
 * @author Andrew Post
 */
public abstract class AbstractKnowledgeSourceBackend extends
		AbstractBackend<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource>
		implements KnowledgeSourceBackend {

	/**
	 * A default implementation that returns <code>null</code>.
	 * 
	 * @see org.protempa.KnowledgeSourceBackend#readAbstractionDefinition(java.lang.String,
	 *      org.protempa.KnowledgeBase)
	 */
	public AbstractionDefinition readAbstractionDefinition(String id,
			KnowledgeBase protempaKnowledgeBase) throws KnowledgeSourceReadException {
		return null;
	}

	/**
	 * A default implementation that returns <code>null</code>.
	 * 
	 * @see org.protempa.KnowledgeSourceBackend#readEventDefinition(java.lang.String,
	 *      org.protempa.KnowledgeBase)
	 */
	public EventDefinition readEventDefinition(String id,
			KnowledgeBase protempaKnowledgeBase) throws KnowledgeSourceReadException {
		return null;
	}

	/**
	 * A default implementation that returns <code>null</code>.
	 * 
	 * @see org.protempa.KnowledgeSourceBackend#readPrimitiveParameterDefinition(java.lang.String,
	 *      org.protempa.KnowledgeBase)
	 */
	public PrimitiveParameterDefinition readPrimitiveParameterDefinition(
			String id, KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
		return null;
	}

	/**
	 * Implemented as a no-op.
	 * 
	 * @see org.protempa.KnowledgeSourceBackend#close()
	 */
	public void close() {

	}

	/**
	 * Notifies registered listeners that the backend has been updated.
	 * 
	 * @see org.protempa.KnowledgeSourceBackendUpdatedEvent
	 * @see org.protempa.KnowledgeSourceBackendListener
	 */
	protected void fireKnowledgeSourceBackendUpdated() {
		fireBackendUpdated(new KnowledgeSourceBackendUpdatedEvent(this));
	}

}
