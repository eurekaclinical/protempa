package org.protempa;

/**
 *
 * @author Andrew Post
 */
public abstract class BackendInitializationException extends Exception {
    protected BackendInitializationException(String reason) {
        super(reason);
    }

    protected BackendInitializationException(Throwable cause) {
        super(cause);
    }

    protected BackendInitializationException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
