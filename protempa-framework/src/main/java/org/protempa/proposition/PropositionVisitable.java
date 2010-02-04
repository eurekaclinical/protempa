package org.protempa.proposition;

/**
 *
 * @author Andrew Post
 */
public interface PropositionVisitable {
    void accept(PropositionVisitor propositionVisitor);
}
