package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public class AlgorithmSourceBackendInitializationException 
        extends BackendInitializationException {
    private static final long serialVersionUID = -5093906558236883800L;
    
    public AlgorithmSourceBackendInitializationException(String message) {
        super(message);
    }

    public AlgorithmSourceBackendInitializationException(Throwable cause) {
        super(cause);
    }

    public AlgorithmSourceBackendInitializationException(String message,
            Throwable cause) {
        super(message, cause);
    }
}
