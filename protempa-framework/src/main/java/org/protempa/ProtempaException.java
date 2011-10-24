package org.protempa;

/**
 * Base class for PROTEMPA checked exceptions.
 * 
 * @author Andrew Post
 * 
 */
public abstract class ProtempaException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5229759417046161519L;

	protected ProtempaException() {
        super();
    }

    protected ProtempaException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ProtempaException(String message) {
        super(message);
    }

    protected ProtempaException(Throwable cause) {
        super(cause);
    }
}
