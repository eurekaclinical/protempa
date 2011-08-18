package org.protempa.bp.commons.dsb.relationaldb;

/**
 * Thrown when no SQL generator was found that is compatible with the database
 * and available drivers.
 * 
 * @author Andrew Post
 */
public class NoCompatibleSQLGeneratorException extends Exception {

    /**
     * Constructs a new exception with null as its detail message.
     */
    public NoCompatibleSQLGeneratorException() {
    }

    /**
     * Constructs a new exception with the specified cause and a detail message 
     * of (cause==null ? null : cause.toString()) (which typically contains the 
     * class and detail message of cause).
     * 
     * @param cause the cause. May be <code>null</code> if the cause is
     * nonexistent or unknown.
     */
    public NoCompatibleSQLGeneratorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message the detail message.
     * @param cause the cause. May be <code>null</code> if the cause is
     * nonexistent or unknown.
     */
    public NoCompatibleSQLGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
    public NoCompatibleSQLGeneratorException(String message) {
        super(message);
    }

    

}
