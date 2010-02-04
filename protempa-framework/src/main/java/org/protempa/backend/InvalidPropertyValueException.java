package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class InvalidPropertyValueException extends ProtempaException {
    public InvalidPropertyValueException(String value) {
        super(value);
    }
}
