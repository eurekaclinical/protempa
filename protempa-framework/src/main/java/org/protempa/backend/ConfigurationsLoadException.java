package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public final class ConfigurationsLoadException extends ProtempaException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9180141565868689830L;

	public ConfigurationsLoadException(Throwable cause) {
        super(cause);
    }

    public ConfigurationsLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationsLoadException(String message) {
        super(message);
    }

    public ConfigurationsLoadException() {
    }

}
