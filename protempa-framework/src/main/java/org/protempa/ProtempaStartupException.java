package org.protempa;

/**
 * Exception for errors that occur when PROTEMPA is being initialized.
 *
 * @author Andrew Post
 */
public class ProtempaStartupException extends ProtempaException {
    private static final long serialVersionUID = -4669769258067806905L;

    ProtempaStartupException(Throwable cause) {
        super(cause);
    }

    ProtempaStartupException(String message) {
        super(message);
    }

    ProtempaStartupException(String message, Throwable cause) {
        super(message, cause);
    }

    ProtempaStartupException() {
    }

}
