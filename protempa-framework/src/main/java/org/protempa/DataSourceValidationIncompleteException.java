package org.protempa;

/**
 *
 * @author Andrew Post
 */
public class DataSourceValidationIncompleteException extends DataSourceException {
    private static final long serialVersionUID = -3628452535225862601L;

    public DataSourceValidationIncompleteException(Throwable cause) {
        super(cause);
    }

    public DataSourceValidationIncompleteException(String message) {
        super(message);
    }

    public DataSourceValidationIncompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceValidationIncompleteException() {
    }



}
