package org.protempa.backend;

/**
 * 
 * @author Michel Mansour
 */
public class TermSourceBackendInitializationException extends
        BackendInitializationException {

    private static final long serialVersionUID = -9111553864819639590L;

    public TermSourceBackendInitializationException(String reason) {
        super(reason);
    }

    public TermSourceBackendInitializationException(Throwable cause) {
        super(cause);
    }

    public TermSourceBackendInitializationException(String reason, Throwable cause) {
        super(reason, cause);
    }

}
