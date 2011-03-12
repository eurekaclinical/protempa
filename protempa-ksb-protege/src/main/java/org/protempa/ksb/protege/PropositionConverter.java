package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;

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
    PropositionDefinition convert(Instance protegeProposition,
            KnowledgeBase protempaKnowledgeBase,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException;

    PropositionDefinition readPropositionDefinition(
            Instance protegeProposition, KnowledgeBase protempaKnowledgeBase);
}
