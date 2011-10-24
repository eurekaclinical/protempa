package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public class KnowledgeSourceBackendInitializationException 
        extends BackendInitializationException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6992398463459710254L;

	public KnowledgeSourceBackendInitializationException(String message) {
        super(message);
    }

    public KnowledgeSourceBackendInitializationException(Throwable cause) {
        super(cause);
    }

    public KnowledgeSourceBackendInitializationException(String message,
            Throwable cause) {
        super(message, cause);
    }
}
