package org.protempa;

/**
 * Thrown when an error occurs in algorithm initialization.
 * 
 * @author Andrew Post
 * 
 */
public class AlgorithmInitializationException extends
		AlgorithmSourceException {
	
	private static final long serialVersionUID = 6908418323975777420L;

	public AlgorithmInitializationException() {
		super();
	}

	public AlgorithmInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlgorithmInitializationException(String message) {
		super(message);
	}

	public AlgorithmInitializationException(Throwable cause) {
		super(cause);
	}

}
