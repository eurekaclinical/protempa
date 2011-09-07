package org.protempa.backend;

/**
 *
 * @author Andrew Post
 */
public class DataSourceBackendInitializationException 
        extends BackendInitializationException {
    private static final long serialVersionUID = 5081943706618112904L;
    
    public DataSourceBackendInitializationException(String message) {
        super(message);
    }

    public DataSourceBackendInitializationException(Throwable cause) {
        super(cause);
    }

    public DataSourceBackendInitializationException(String message,
            Throwable cause) {
        super(message, cause);
    }
}
