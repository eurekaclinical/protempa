package org.protempa.query.handler.table;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class LinkValidationFailedException extends ProtempaException {
    private static final long serialVersionUID = 4497540048059435234L;

    public LinkValidationFailedException(Throwable cause) {
        super(cause);
    }

    public LinkValidationFailedException(String message) {
        super(message);
    }

    public LinkValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkValidationFailedException() {
    }
    
    
    
}
