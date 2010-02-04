package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public interface BackendPropertyValidator {
    void validate(String name, Object value)
            throws InvalidPropertyValueException;
}
