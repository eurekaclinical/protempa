package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class InvalidPropertyNameException extends ProtempaException {
    public InvalidPropertyNameException(String name) {
        super(name);
    }
}
