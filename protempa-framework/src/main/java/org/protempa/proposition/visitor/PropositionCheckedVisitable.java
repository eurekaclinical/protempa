package org.protempa.proposition.visitor;

import org.protempa.ProtempaException;

/**
 * Interface for accepting a proposition visitor that might throw a checked
 * exception.
 * 
 * @author Andrew Post
 */
public interface PropositionCheckedVisitable {
    /**
     * Performs some processing on a proposition that might throw a
     * checked exception.
     *
     * @param propositionCheckedVisitor
     * @throws ProtempaException
     */
    void acceptChecked(PropositionCheckedVisitor propositionCheckedVisitor)
            throws ProtempaException;
}
