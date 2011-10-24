package org.protempa;

public abstract class TermSourceException extends ProtempaException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8483966301758608297L;

	public TermSourceException() {
    }

    public TermSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TermSourceException(String message) {
        super(message);
    }

    public TermSourceException(Throwable cause) {
        super(cause);
    }

}
