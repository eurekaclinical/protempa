package org.protempa.query;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class QueryBuildException extends ProtempaException {
    private static final long serialVersionUID = -1002342517364987788L;

    public QueryBuildException(Throwable cause) {
        super(cause);
    }

    public QueryBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryBuildException(String message) {
        super(message);
    }

    public QueryBuildException() {
    }
}
