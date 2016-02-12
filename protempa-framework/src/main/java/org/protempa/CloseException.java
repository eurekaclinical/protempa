package org.protempa;

/**
 *
 * @author Andrew Post
 */
public class CloseException extends ProtempaException {

    public CloseException() {
    }

    public CloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloseException(String message) {
        super(message);
    }

    public CloseException(Throwable cause) {
        super(cause);
    }
    
}
