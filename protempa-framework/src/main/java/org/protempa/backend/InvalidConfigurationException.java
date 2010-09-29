package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public class InvalidConfigurationException extends Exception {
    private static final long serialVersionUID = 849376696935805014L;

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationException() {
    }



}
