package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 * Thrown if a property value in a configuration is invalid.
 * 
 * @author Andrew Post
 */
public class InvalidPropertyValueException extends ProtempaException {
    public InvalidPropertyValueException(String value) {
        super("Invalid property value in configuration: " + value);
    }
}
