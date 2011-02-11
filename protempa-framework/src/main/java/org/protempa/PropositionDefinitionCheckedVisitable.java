package org.protempa;

/**
 * Interface for accepting a proposition definition visitor.
 * 
 * @author Andrew Post
 */
public interface PropositionDefinitionCheckedVisitable {
    /**
     * Performs some processing on this proposition definition.
     *
     * @param visitor
     *            a {@link PropositionDefinitionVisitor}, cannot be
     *            <code>null</code>.
     */
    void acceptChecked(PropositionDefinitionCheckedVisitor propositionVisitor)
            throws ProtempaException;
}
