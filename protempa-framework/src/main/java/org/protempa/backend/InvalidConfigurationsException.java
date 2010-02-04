package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public class InvalidConfigurationsException extends Exception {

    public InvalidConfigurationsException(String message) {
        super(message);
    }

    public InvalidConfigurationsException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationsException() {
    }



}
