package org.arp.javautil.io;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Retries a data access operation whose failure is expected under defined 
 * circumstances. This enables fault tolerance.
 * 
 * @param <E> the type of error code or exception that occurs when the
 * operation fails.
 * 
 * @author Andrew Post
 */
public class Retryer<E> {
    
    private final int retries;
    private final List<E> errors;
    private int attempts;
    
    /**
     * Constructs a retryer with a number of retries.
     * 
     * @param retries the number of retries.
     */
    public Retryer(int retries) {
        if (retries < 0) {
            throw new IllegalArgumentException("retries must be >= 0");
        }
        this.retries = retries;
        this.errors = new ArrayList<E>();
    }
    
    /**
     * Gets the number of times that 
     * {@link #execute(org.arp.javautil.io.Retryable)  } will retry the
     * operation.
     * 
     * @return the number of retries.
     */
    public int getRetries() {
        return this.retries;
    }
    
    /**
     * Gets the number of attempts that have been made to execute the
     * {@link Retryable} from the most recent call to 
     * {@link #execute(org.arp.javautil.io.Retryable) }.
     * 
     * @return the number of attempts.
     */
    public int getAttempts() {
        return this.attempts;
    }
    
    /**
     * Returns any error codes or exceptions from the most recent call to
     * {@link #execute(org.arp.javautil.io.Retryable) }. The number of codes is
     * equal to the number of failures, and the codes are returned in 
     * order.
     * 
     * @return a newly created {@link List} of errors.
     */
    public List<E> getErrors() {
        return new ArrayList<E>(this.errors);
    }
    
    /**
     * Executes a command with <code>retries</code> retries. This can be
     * executed multiple times.
     * 
     * @param operation the operation to attempt.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     * if not. Consult {@link #getErrors()} for any error codes or exceptions 
     * that the operation returned.
     */
    public boolean execute(Retryable<E> operation) {
        Logger logger = IOUtil.logger();
        int i = this.retries + 1;
        this.attempts = 0;
        this.errors.clear();
        
        E error;
        do {
            error = operation.attempt();
            this.attempts++;
            if (error != null) {
                this.errors.add(error);
                --i;
                logError(logger, error, i);
                if (i > 0) {
                    logger.log(Level.WARNING, "Recovering...");
                    operation.recover();
                }
            }
        } while (error != null && i > 0);
        
        return error == null;
    }

    private void logError(Logger logger, E error, int i) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING, 
                "Error occurred: {0}; {1} attempt(s) left", 
                new Object[]{error.toString(), i});
        }
    }
}
