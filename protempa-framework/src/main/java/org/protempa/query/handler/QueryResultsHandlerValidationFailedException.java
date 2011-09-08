package org.protempa.query.handler;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class QueryResultsHandlerValidationFailedException 
        extends ProtempaException {
    private static final long serialVersionUID = -8852646763046186366L;

    public QueryResultsHandlerValidationFailedException(Throwable cause) {
        super(cause);
    }

    public QueryResultsHandlerValidationFailedException(String message) {
        super(message);
    }

    public QueryResultsHandlerValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryResultsHandlerValidationFailedException() {
    }
    
    
    
}
