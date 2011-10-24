package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 * Thrown if a property value in a configuration is invalid.
 * 
 * @author Andrew Post
 */
public class InvalidPropertyValueException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5960840541202474047L;

	public InvalidPropertyValueException(String value) {
        super("Invalid property value in configuration: " + value);
    }
}
