package org.protempa;

/**
 * Thrown when an error occurs translating abstraction definitions into rules.
 *
 * @author Andrew Post
 */
public abstract class RuleCreationException extends ProtempaException {
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
