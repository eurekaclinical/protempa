package org.protempa.dsb;

import org.protempa.DataSourceBackendInitializationException;

/**
 * Thrown when the schema adaptor could not be
 * initialized.
 * @author Andrew Post
 *
 */
public final class SchemaAdaptorInitializationException extends
		DataSourceBackendInitializationException {

	public SchemaAdaptorInitializationException(String message,
            Throwable cause) {
		super(message, cause);
	}

	public SchemaAdaptorInitializationException(String message) {
		super(message);
	}

	public SchemaAdaptorInitializationException(Throwable cause) {
		super(cause);
	}

}
