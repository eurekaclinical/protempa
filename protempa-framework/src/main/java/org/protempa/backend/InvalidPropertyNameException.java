package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 * Thrown if a property name in a configuration is invalid.
 * 
 * @author Andrew Post
 */
public class InvalidPropertyNameException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8759461647512501542L;

	public InvalidPropertyNameException(String name) {
        super("Invalid property name in configuration: " + name);
    }
}
