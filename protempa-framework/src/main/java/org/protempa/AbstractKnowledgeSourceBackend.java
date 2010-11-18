package org.protempa;

import java.util.ArrayList;
import java.util.List;

import org.protempa.query.And;

/**
 * Skeletal implementation of the <code>KnowledgeSourceBackend</code> interface
 * to minimize the effort required to implement this interface.
 * 
 * @author Andrew Post
 */
public abstract class AbstractKnowledgeSourceBackend extends
        AbstractBackend<KnowledgeSourceBackendUpdatedEvent, KnowledgeSource>
        implements KnowledgeSourceBackend {

    /**
     * A default implementation that returns <code>null</code>.
     * 
     * @see org.protempa.KnowledgeSourceBackend#readAbstractionDefinition(java.lang.String,
     *      org.protempa.KnowledgeBase)
     */
    @Override
    public AbstractionDefinition readAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns <code>null</code>.
     * 
     * @see org.protempa.KnowledgeSourceBackend#readEventDefinition(java.lang.String,
     *      org.protempa.KnowledgeBase)
     */
    @Override
    public EventDefinition readEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns <code>null</code>.
     * 
     * @see org.protempa.KnowledgeSourceBackend#readPrimitiveParameterDefinition(java.lang.String,
     *      org.protempa.KnowledgeBase)
     */
    @Override
    public PrimitiveParameterDefinition readPrimitiveParameterDefinition(
            String id, KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public ConstantDefinition readConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public boolean hasPrimitiveParameterDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return readPrimitiveParameterDefinition(id, protempaKnowledgeBase) != null;
    }

    @Override
    public boolean hasEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return readEventDefinition(id, protempaKnowledgeBase) != null;
    }

    @Override
    public boolean hasAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return readAbstractionDefinition(id, protempaKnowledgeBase) != null;
    }

    @Override
    public boolean hasConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return readConstantDefinition(id, protempaKnowledgeBase) != null;
    }

    /**
     * A default implementation that returns an empty List.
     * 
     * @return a {@link List<String>}.
     */
    @Override
    public List<String> getPropositionsByTermSubsumption(And<TermSubsumption> termId)
            throws KnowledgeSourceReadException {
        return new ArrayList<String>();
    }
    

    /** 
     * A default implementation that returns an empty list.
     * 
     * @see org.protempa.KnowledgeSourceBackend#getPropositionsByTerm(java.lang.String)
     */
    @Override
    public List<String> getPropositionsByTerm(String termId)
            throws KnowledgeSourceReadException {
        return new ArrayList<String>();
    }

    /**
     * Implemented as a no-op.
     * 
     * @see org.protempa.KnowledgeSourceBackend#close()
     */
    @Override
    public void close() {
    }

    /**
     * Notifies registered listeners that the backend has been updated.
     * 
     * @see org.protempa.KnowledgeSourceBackendUpdatedEvent
     * @see org.protempa.KnowledgeSourceBackendListener
     */
    protected void fireKnowledgeSourceBackendUpdated() {
        fireBackendUpdated(new KnowledgeSourceBackendUpdatedEvent(this));
    }
}
