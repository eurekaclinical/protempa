package org.arp.javautil.io;

/**
 * Adds support for automatically retrying an operation whose failure is 
 * expected under defined circumstances. This enables fault tolerance for
 * data access operations.
 *
 * @param <E> the type of error code or exception that occurs when the
 * operation fails.
 * 
 * @author Andrew Post
 */
public interface Retryable<E> {

    /**
     * Attempts to execute an operation that may fail under defined
     * circumstances.
     *
     * @return an error code or exception, or <code>null</code> if the attempt 
     * is successful. Exceptions are returned not thrown so that the type
     * of error can be specified using generics (<code>throw E</code> causes
     * a compile-time error).
     */
    E attempt();

    /**
     * Performs some operation between attempts (e.g., wait for a few seconds).
     * This will not be called after the last attempt. Use the 
     * {@link #attempt() } method for resource cleanup.
     */
    void recover();
    
}
