package org.protempa.cli;

import org.protempa.ProtempaException;
import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class CLIException extends ProtempaException {
    private static final long serialVersionUID = -3691826649207635652L;

    public CLIException(Throwable cause) {
        super(cause);
    }

    public CLIException(String message) {
        super(message);
    }

    public CLIException(String message, Throwable cause) {
        super(message, cause);
    }

    public CLIException() {
    }

    

}
