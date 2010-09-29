package org.protempa;

/**
 *
 * @author Andrew Post
 */
public class DataSourceFailedValidationException extends DataSourceException {
    private static final long serialVersionUID = 4682398146182167907L;

    public DataSourceFailedValidationException(Throwable cause) {
        super(cause);
    }

    public DataSourceFailedValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceFailedValidationException(String message) {
        super(message);
    }

    public DataSourceFailedValidationException() {
    }

    

}
