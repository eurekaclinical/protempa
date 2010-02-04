package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class BackendNewInstanceException extends ProtempaException {
    public BackendNewInstanceException() {
        super();
    }

    public BackendNewInstanceException(String message) {
        super(message);
    }

    public BackendNewInstanceException(Throwable throwable) {
        super(throwable);
    }

    public BackendNewInstanceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
