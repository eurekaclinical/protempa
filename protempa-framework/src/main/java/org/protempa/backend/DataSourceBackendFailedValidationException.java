package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class DataSourceBackendFailedValidationException 
        extends ProtempaException {
    private static final long serialVersionUID = 53055128452150166L;

    public DataSourceBackendFailedValidationException(Throwable cause) {
        super(cause);
    }

    public DataSourceBackendFailedValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceBackendFailedValidationException(String message) {
        super(message);
    }

    public DataSourceBackendFailedValidationException() {
    }

    

}
