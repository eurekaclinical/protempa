package org.protempa;

/**
 * Thrown when an error occurs in algorithm initialization or processing.
 * @author Andrew Post
 *
 */
public abstract class AlgorithmSourceException extends ProtempaException {
	
	private static final long serialVersionUID = 8105126411793215020L;

	AlgorithmSourceException() {
		super();
	}

	AlgorithmSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	AlgorithmSourceException(String message) {
		super(message);
	}

	AlgorithmSourceException(Throwable cause) {
		super(cause);
	}

}
