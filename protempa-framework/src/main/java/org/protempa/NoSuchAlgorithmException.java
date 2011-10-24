package org.protempa;

/**
 *
 * @author Andrew Post
 */
public class NoSuchAlgorithmException extends AlgorithmSourceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3429985263421730125L;

	public NoSuchAlgorithmException(Throwable cause) {
        super(cause);
    }

    public NoSuchAlgorithmException(String message) {
        super(message);
    }

    public NoSuchAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchAlgorithmException() {
    }

}
