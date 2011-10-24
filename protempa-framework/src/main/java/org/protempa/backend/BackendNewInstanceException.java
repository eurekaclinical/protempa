package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class BackendNewInstanceException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1798629706550588890L;

	public BackendNewInstanceException() {
        super();
    }

    public BackendNewInstanceException(String message) {
        super(message);
    }

    public BackendNewInstanceException(Throwable throwable) {
        super(throwable);
    }

    public BackendNewInstanceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
