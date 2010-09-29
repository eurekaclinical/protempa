package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;

/**
 * @author Andrew Post
 */
public interface PropositionConverter {

    /**
     * Convert and add the given Protege parameter instance to the given
     * PROTEMPA knowledge base, if it hasn't already been added to the PROTEMPA
     * knowledge base.
     *
     * @param protegeProposition
     *            the Protege proposition {@link Instance}.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase}.
     * @param backend
     *            the Protege {@link KnowledgeSourceBackend}.
     */
    void convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException;

    /**
     * Checks if the given Protege parameter instance is already in the given
     * PROTEMPA knowledge base.
     *
     * @param protegeProposition
     *            the Protege proposition {@link Instance}.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase}.
     * @return <code>true</code> if is already in the knowledge base,
     *         <code>false</code> otherwise. Also returns <code>false</code>
     *         if either parameter is <code>null</code>.
     */
    boolean protempaKnowledgeBaseHasProposition(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase);
}
