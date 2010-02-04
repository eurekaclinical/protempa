package org.protempa.dsb;

import org.protempa.DataSourceBackendInitializationException;

/**
 * Thrown when the terminology adaptor could not be
 * initialized.
 * @author Andrew Post
 *
 */
public final class TerminologyAdaptorInitializationException extends
		DataSourceBackendInitializationException {

	TerminologyAdaptorInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	TerminologyAdaptorInitializationException(String message) {
		super(message);
	}

	TerminologyAdaptorInitializationException(Throwable cause) {
		super(cause);
	}

}
