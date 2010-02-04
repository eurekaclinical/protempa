package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public class BackendProviderInitializationException extends RuntimeException {
    BackendProviderInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    BackendProviderInitializationException(String message) {
        super(message);
    }
}
