package org.protempa;

/**
 * Thrown when an error occurs translating abstraction definitions into rules.
 *
 * @author Andrew Post
 */
public abstract class RuleCreationException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2627168079306834270L;

	protected RuleCreationException() {
		super();
	}

	protected RuleCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	protected RuleCreationException(String message) {
		super(message);
	}

	protected RuleCreationException(Throwable cause) {
		super(cause);
	}
}
