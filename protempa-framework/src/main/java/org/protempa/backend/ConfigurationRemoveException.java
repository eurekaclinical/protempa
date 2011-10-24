package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class ConfigurationRemoveException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2329005766725093529L;

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
