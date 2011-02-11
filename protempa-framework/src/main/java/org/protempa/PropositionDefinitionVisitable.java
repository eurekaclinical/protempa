package org.protempa;

/**
 * Interface for accepting a proposition definition visitor.
 * 
 * @author Andrew Post
 */
public interface PropositionDefinitionVisitable {
    /**
     * Performs some processing on a proposition definition.
     *
     * @param visitor
     *            a {@link PropositionDefinitionVisitor}.
     */
    void accept(PropositionDefinitionVisitor propositionVisitor);
}
