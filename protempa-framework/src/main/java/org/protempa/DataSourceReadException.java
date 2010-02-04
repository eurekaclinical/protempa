package org.protempa;

/**
 * Thrown when there is an error retrieving data from the data source.
 * To be raised from {@link SchemaAdaptor}s.
 * 
 * @author Andrew Post
 * 
 */
public class DataSourceReadException extends DataSourceException {

	private static final long serialVersionUID = -1607783133183868272L;

	public DataSourceReadException() {
		super();
	}

	public DataSourceReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataSourceReadException(String message) {
		super(message);
	}

	public DataSourceReadException(Throwable cause) {
		super(cause);
	}

}
