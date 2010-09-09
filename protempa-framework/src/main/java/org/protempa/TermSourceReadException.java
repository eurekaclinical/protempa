package org.protempa;

public class TermSourceReadException extends TermSourceException {
    private static final long serialVersionUID = 8077016847182651783L;

    public TermSourceReadException() {
    }

    public TermSourceReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public TermSourceReadException(String message) {
        super(message);
    }

    public TermSourceReadException(Throwable cause) {
        super(cause);
    }

}
