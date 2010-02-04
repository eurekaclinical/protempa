package org.protempa;

/**
 * Base class for PROTEMPA checked exceptions.
 * 
 * @author Andrew Post
 * 
 */
public abstract class ProtempaException extends Exception {

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
