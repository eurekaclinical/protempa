package org.protempa;

/**
 * Thrown when an unexpected error occurs when calling one
 * of Protempa's find methods.
 * 
 * @author Andrew Post
 *
 */
public final class FinderException extends ProtempaException {
	
	private static final long serialVersionUID = 7903820808353618290L;
	
	public FinderException() {
		super();
	}

	public FinderException(String message, Throwable cause) {
		super(message, cause);
	}

	public FinderException(String message) {
		super(message);
	}

	public FinderException(Throwable cause) {
		super(cause);
	}
	
	
	
}
