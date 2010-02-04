package org.protempa;

/**
 *
 * @author Andrew Post
 */
public class KnowledgeSourceReadException extends KnowledgeSourceException {
    private static final long serialVersionUID = -7105219436003670348L;

    public KnowledgeSourceReadException() {
    }

    public KnowledgeSourceReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public KnowledgeSourceReadException(String message) {
        super(message);
    }

    public KnowledgeSourceReadException(Throwable cause) {
        super(cause);
    }

    

}
