package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public final class ConfigurationsSaveException extends ProtempaException {

    public ConfigurationsSaveException(Throwable cause) {
        super(cause);
    }

    public ConfigurationsSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationsSaveException(String message) {
        super(message);
    }

    public ConfigurationsSaveException() {
    }
    
}
