package org.protempa.proposition;

/**
 * Interface for accepting a proposition visitor.
 * 
 * @author Andrew Post
 */
public interface PropositionVisitable {
    /**
     * Performs some processing on a proposition.
     *
     * @param visitor
     *            a {@link PropositionVisitor}.
     */
    void accept(PropositionVisitor propositionVisitor);
}
