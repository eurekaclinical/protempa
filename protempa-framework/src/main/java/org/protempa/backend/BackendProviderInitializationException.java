package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public class BackendProviderInitializationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3198034171015889720L;

	BackendProviderInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    BackendProviderInitializationException(String message) {
        super(message);
    }
}
