package org.protempa;

/**
 * Translates from an arbitrary knowledge base to a PROTEMPA knowledge base.
 * Users of <code>KnowledgeSourceBackend</code> implementations must first
 * call {@link #initialize(Properties)} with a set of configuration properties
 * that is specific to the <code>KnowledgeSourceBackend</code> implementation.
 * 
 * @author Andrew Post
 */
public interface KnowledgeSourceBackend extends
        Backend<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource> {

    /**
     * Read a primitive parameter definition into the given PROTEMPA knowledge
     * base.
     *
     * @param id
     *            a primitive parameter id {@link String}.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use.
     * @return the {@link PrimitiveParameterDefinition}, or <code>null</code>
     *         if none with the given id was found.
     */
    PrimitiveParameterDefinition readPrimitiveParameterDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Read an abstraction definition into the given PROTEMPA knowledge base.
     *
     * @param id
     *            an abstraction id {@link String}.
     * @param protempaKnowledgeBase
     *            the PROTEMPA {@link KnowledgeBase} to use.
     * @return the {@link AbstractionDefinition}, or <code>null</code> if
     *         none with the given id was found.
     */
    AbstractionDefinition readAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    /**
     * Read an event definition into the given PROTEMPA knowledge base.
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

    boolean hasEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    boolean hasAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    ConstantDefinition readConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;

    boolean hasConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException;
}
