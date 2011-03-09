package org.protempa;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates the direct children of proposition definitions.
 *
 * @author Andrew Post
 */
public final class DirectChildrenVisitor extends AbstractPropositionDefinitionCheckedVisitor {

    private final List<PropositionDefinition> propDefs;
    private final KnowledgeSource knowledgeSource;

    public DirectChildrenVisitor(KnowledgeSource knowledgeSource) {
        this.knowledgeSource = knowledgeSource;
        this.propDefs = new ArrayList<PropositionDefinition>();
    }

    @Override
    public void visit(EventDefinition eventDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(eventDefinition));
    }

    @Override
    public void visit(HighLevelAbstractionDefinition highLevelAbstractionDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(highLevelAbstractionDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(highLevelAbstractionDefinition));
    }

    @Override
    public void visit(LowLevelAbstractionDefinition lowLevelAbstractionDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(lowLevelAbstractionDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(lowLevelAbstractionDefinition));
    }

    @Override
    public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(primitiveParameterDefinition));
    }

    @Override
    public void visit(SliceDefinition sliceAbstractionDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(sliceAbstractionDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(sliceAbstractionDefinition));
    }

    @Override
    public void visit(ConstantDefinition constantDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(constantDefinition));
    }

    @Override
    public void visit(PairDefinition pairDefinition) throws KnowledgeSourceReadException {
        this.propDefs.addAll(knowledgeSource.readInverseIsA(pairDefinition));
        this.propDefs.addAll(knowledgeSource.readAbstractedFrom(pairDefinition));
    }

    /**
     * Gets the direct children.
     *
     * @return a {@link List<PropositionDefinition>} of the direct children.
     */
    public List<PropositionDefinition> getDirectChildren() {
        return this.propDefs;
    }

    public void clear() {
        this.propDefs.clear();
    }
}
