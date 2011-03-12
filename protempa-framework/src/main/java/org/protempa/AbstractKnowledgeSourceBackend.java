package org.protempa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.protempa.proposition.value.ValueSet;
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
     * A default implementation that returns <code>null</code>. Override this
     * if your knowledge base contains abstraction definitions.
     *
     * @param id a proposition id {@link String}.
     * @param protempaKnowledgeBase  a PROTEMPA {@link KnowledgeBase}.
     * @return an {@link AbstractionDefinition}, always <code>null</code> in
     * this implementation.
     */
    @Override
    public AbstractionDefinition readAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns <code>null</code>. Override this
     * if your knowledge base contains event definitions.
     *
     * @param id a proposition id {@link String}.
     * @param protempaKnowledgeBase  a PROTEMPA {@link KnowledgeBase}.
     * @return an {@link EventDefinition}, always <code>null</code> in
     * this implementation.
     */
    @Override
    public EventDefinition readEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns <code>null</code>. Override this
     * if your knowledge base contains primitive parameter definitions.
     *
     * @param id a proposition id {@link String}.
     * @param protempaKnowledgeBase  a PROTEMPA {@link KnowledgeBase}.
     * @return a {@link PrimitiveParameterDefinition}, always
     * <code>null</code> in this implementation.
     */
    @Override
    public PrimitiveParameterDefinition readPrimitiveParameterDefinition(
            String id, KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns <code>null</code>. Override this
     * if your knowledge base contains constant definitions.
     *
     * @param id a proposition id {@link String}.
     * @param protempaKnowledgeBase  a PROTEMPA {@link KnowledgeBase}.
     * @return a {@link ConstantDefinition}, always <code>null</code> in
     * this implementation.
     */
    @Override
    public ConstantDefinition readConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns <code>null</code>. Override this
     * if your knowledge base contains value sets.
     *
     * @param id a proposition id {@link String}.
     * @param protempaKnowledgeBase  a PROTEMPA {@link KnowledgeBase}.
     * @return an {@link ValueSet}, always <code>null</code> in
     * this implementation.
     */
    @Override
    public ValueSet readValueSet(String id, KnowledgeBase kb)
            throws KnowledgeSourceReadException {
        return null;
    }

    /**
     * A default implementation that returns an empty List.
     * 
     * @return a {@link List<String>}.
     */
    @Override
    public List<String> getPropositionsByTermSubsumption(
            And<TermSubsumption> termId) throws KnowledgeSourceReadException {
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
     * A default implementation that calls
     * {@link #readPropositionDefinition(java.lang.String, org.protempa.KnowledgeBase).
     * You may want to override this if your knowledge base supports a more
     * efficient implementation.
     * 
     * @param abstractionDefinition an {@link AbstractionDefinition}, cannot
     * be <code>null</code>.
     * @param kb the PROTEMPA {@link KnowledgeBase}.
     * Guaranteed not <code>null</code>.
     * @return a {@link List<PropositionDefinition}s.
     * @throws KnowledgeSourceReadException if an error occurred when
     * performing this operation.
     */
    @Override
    public List<PropositionDefinition> readAbstractedFrom(
            AbstractionDefinition abstractionDefinition,
            KnowledgeBase kb) throws KnowledgeSourceReadException {
        if (abstractionDefinition == null) {
            throw new IllegalArgumentException("abstractionDefinition cannot be null");
        }
        Set<String> children = abstractionDefinition.getAbstractedFrom();
        List<PropositionDefinition> result =
                new ArrayList<PropositionDefinition>(children.size());
        readPropositionDefinitions(children, kb, result);
        return result;
    }

    /**
     * A default implementation that calls
     * {@link #readPropositionDefinition(java.lang.String, org.protempa.KnowledgeBase).
     * You may want to override this if your knowledge base supports a more
     * efficient implementation.
     *
     * @param abstractionDefinition a {@link PropositionDefinition},
     * guaranteed not to be <code>null</code>.
     * @param kb the PROTEMPA {@link KnowledgeBase}. Guaranteed not
     * <code>null</code>.
     * @return a {@link List<PropositionDefinition}s.
     * @throws KnowledgeSourceReadException if an error occurred when
     * performing this operation.
     */
    @Override
    public List<PropositionDefinition> readInverseIsA(
            PropositionDefinition propDef, KnowledgeBase kb)
            throws KnowledgeSourceReadException {
        String[] children = propDef.getInverseIsA();
        List<PropositionDefinition> result =
                new ArrayList<PropositionDefinition>(children.length);
        readPropositionDefinitions(children, kb, result);
        return result;
    }


    private void readPropositionDefinitions(Set<String> children,
            KnowledgeBase kb, List<PropositionDefinition> result)
            throws KnowledgeSourceReadException {
        for (String childId : children) {
            PropositionDefinition propDef = readPropositionDefinition(childId, kb);
            if (propDef != null) {
                result.add(propDef);
            }
        }
    }

    private void readPropositionDefinitions(String[] children,
            KnowledgeBase kb, List<PropositionDefinition> result)
            throws KnowledgeSourceReadException {
        for (String childId : children) {
            PropositionDefinition propDef = readPropositionDefinition(childId, kb);
            if (propDef != null) {
                result.add(propDef);
            }
        }
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String childId, KnowledgeBase kb)
            throws KnowledgeSourceReadException {
        PropositionDefinition def = readAbstractionDefinition(childId, kb);
        if (def == null) {
            def = readEventDefinition(childId, kb);
            if (def == null) {
                def = readConstantDefinition(childId, kb);
                if (def == null) {
                    def = readPrimitiveParameterDefinition(childId, kb);
                }
            }
        }
        return def;
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
