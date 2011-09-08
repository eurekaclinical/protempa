package org.protempa.query.handler.table;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class TableColumnSpecValidationFailedException 
        extends ProtempaException {
    private static final long serialVersionUID = 293690855068370166L;

    public TableColumnSpecValidationFailedException(Throwable cause) {
        super(cause);
    }

    public TableColumnSpecValidationFailedException(String message) {
        super(message);
    }

    public TableColumnSpecValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableColumnSpecValidationFailedException() {
    }
    
    
    
}
