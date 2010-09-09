package org.protempa;

public abstract class TermSourceException extends ProtempaException {

    public TermSourceException() {
    }

    public TermSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TermSourceException(String message) {
        super(message);
    }

    public TermSourceException(Throwable cause) {
        super(cause);
    }

}
