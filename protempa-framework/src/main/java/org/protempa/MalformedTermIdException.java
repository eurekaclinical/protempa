package org.protempa;

public class MalformedTermIdException extends Exception {

    private static final long serialVersionUID = -2614315975916422007L;

    public MalformedTermIdException() {
    }

    public MalformedTermIdException(String message) {
        super(message);
    }

    public MalformedTermIdException(Throwable cause) {
        super(cause);
    }

    public MalformedTermIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
