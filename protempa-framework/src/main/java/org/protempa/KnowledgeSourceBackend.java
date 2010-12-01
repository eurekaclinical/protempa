package org.protempa;

import java.util.List;

import org.protempa.query.And;

/**
 * Translates from an arbitrary knowledge base to a PROTEMPA knowledge base.
 * Users of <code>KnowledgeSourceBackend</code> implementations must first call
 * {@link #initialize(Properties)} with a set of configuration properties that
 * is specific to the <code>KnowledgeSourceBackend</code> implementation.
 * 
 * @author Andrew Post
 */
public interface KnowledgeSourceBackend extends
        Backend<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource> {

    /**
     * Reads a primitive parameter definition into the given PROTEMPA knowledge
     * base.
     * 
     * @param id
     *            a primitive parameter id {@link String}.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use.
     * @return the {@link PrimitiveParameterDefinition}, or <code>null</code> if
     *         none with the given id was found.
     */
    PrimitiveParameterDefinition readPrimitiveParameterDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Reads an abstraction definition into the given PROTEMPA knowledge base.
     * 
     * @param id
     *            an abstraction id {@link String}.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use.
     * @return the {@link AbstractionDefinition}, or <code>null</code> if none
     *         with the given id was found.
     */
    AbstractionDefinition readAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Reads an event definition into the given PROTEMPA knowledge base.
     * 
     * @param id
     *            an event id.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use.
     * @return the {@link EventDefinition}, or <code>null</code> if none with
     *         the given id was found.
     */
    EventDefinition readEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    boolean hasPrimitiveParameterDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    boolean hasEventDefinition(String id, KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    boolean hasAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Reads a constant definition into the given PROTEMPA knowledge base.
     * 
     * @param id
     *            an event id.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use.
     * @return the {@link ConstantDefinition}, or <code>null</code> if none with
     *         the given id was found.
     */
    ConstantDefinition readConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    boolean hasConstantDefinition(String id, KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Gets the proposition definitions that are associated with the given AND
     * clause of term subsumptions. A proposition matches if and only if at
     * least one term in every subsumption is related to that proposition.
     * 
     * @param termSubsumptions
     *            the term subsumptions to match in the knowledge source
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use
     * 
     * @return a {@link List} of proposition definitions associated with the
     *         given term IDs
     * @throws KnowledgeSourceReadException
     *             if there is a problem reading from the knowledge source
     */
    List<String> getPropositionsByTermSubsumption(
            And<TermSubsumption> termSubsumptions)
            throws KnowledgeSourceReadException;

    /**
     * Gets the proposition definitions for a given term.
     * 
     * @param termId
     *            the term ID to look up
     * @return a {@link List} of proposoition IDs related to the given term
     * @throws KnowledgeSourceReadException
     *             if there is a problem reading from the Knowledge source
     */
    List<String> getPropositionsByTerm(String termId)
            throws KnowledgeSourceReadException;
}
