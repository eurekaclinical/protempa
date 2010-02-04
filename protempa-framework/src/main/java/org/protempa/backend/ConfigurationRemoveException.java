package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class ConfigurationRemoveException extends ProtempaException {
    public ConfigurationRemoveException(Throwable cause) {
        super(cause);
    }

    public ConfigurationRemoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationRemoveException(String message) {
        super(message);
    }

    public ConfigurationRemoveException() {
    }
}
