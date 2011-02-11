package org.protempa.proposition.value;

/**
 * Interface for accepting a value.
 * 
 * @author Andrew Post
 */
public interface ValueVisitable {
    /**
     * Performs some processing on a value.
     *
     * @param visitor
     *            a {@link ValueVisitor}.
     */
    void accept(ValueVisitor valueVisitor);
}
