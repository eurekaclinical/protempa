package org.protempa;

/**
 * Abstract class for exceptions that are thrown when an error occurs calling
 * the data source.
 * 
 * @author Andrew Post
 * 
 */
public abstract class DataSourceException extends ProtempaException {

    DataSourceException() {
    }

    DataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    DataSourceException(String message) {
        super(message);
    }

    DataSourceException(Throwable cause) {
        super(cause);
    }
}
