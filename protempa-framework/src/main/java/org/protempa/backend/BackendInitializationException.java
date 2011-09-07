package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public abstract class BackendInitializationException extends ProtempaException {
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
