package org.protempa;

/**
 *
 * @author Andrew Post
 */
public abstract class KnowledgeSourceException extends ProtempaException {

    KnowledgeSourceException(Throwable cause) {
        super(cause);
    }

    KnowledgeSourceException(String message) {
        super(message);
    }

    KnowledgeSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    KnowledgeSourceException() {
    }



}
