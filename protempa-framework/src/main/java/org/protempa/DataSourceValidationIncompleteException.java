package org.protempa;

/**
 * Thrown if an error occurred during validation that prevented its
 * completion.
 * 
 * @author Andrew Post
 */
public class DataSourceValidationIncompleteException
        extends DataSourceException {
    private static final long serialVersionUID = -3628452535225862601L;

    /**
     * Instantiates the exception with another nested {@link Throwable}.
     *
     * @param cause a {@link Throwable}.
     */
    public DataSourceValidationIncompleteException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates the exception with an error message.
     *
     * @param message a {@link String}.
     */
    public DataSourceValidationIncompleteException(String message) {
        super(message);
    }

    /**
     * Instantiates the exception with an error message and a nested
     * {@link Throwable}.
     *
     * @param message an error message {@link String}.
     * @param cause a {@link Throwable}.
     */
    public DataSourceValidationIncompleteException(String message,
            Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates the exception with no information.
     */
    public DataSourceValidationIncompleteException() {
    }



}
