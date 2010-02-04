package org.protempa;

/**
 * Throws when an error occurs in algorithm processing.
 * 
 * @author Andrew Post
 * 
 */
public class AlgorithmProcessingException extends AlgorithmSourceException {

	private static final long serialVersionUID = 8399435107229374668L;

	public AlgorithmProcessingException() {
		super();
	}

	public AlgorithmProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlgorithmProcessingException(String message) {
		super(message);
	}

	public AlgorithmProcessingException(Throwable cause) {
		super(cause);
	}

}
