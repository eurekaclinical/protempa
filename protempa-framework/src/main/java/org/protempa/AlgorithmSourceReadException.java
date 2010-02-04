package org.protempa;

/**
 * Thrown when an error occurs reading an algorithm.
 * 
 * @author Andrew Post
 *
 * @see AlgorithmSourceBackend#readAlgorithm(java.lang.String, org.protempa.Algorithms)
 * @see AlgorithmSourceBackend#readAlgorithms(org.protempa.Algorithms) 
 */
public class AlgorithmSourceReadException extends AlgorithmSourceException {
    private static final long serialVersionUID = 3566789142394841800L;
    
    public AlgorithmSourceReadException() {
		super();
	}

	public AlgorithmSourceReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlgorithmSourceReadException(String message) {
		super(message);
	}

	public AlgorithmSourceReadException(Throwable cause) {
		super(cause);
	}
}
