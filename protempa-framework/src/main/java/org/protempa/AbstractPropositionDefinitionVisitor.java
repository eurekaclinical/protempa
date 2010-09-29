package org.protempa;

import java.util.Collection;

/**
 * An abstract base class for implementing proposition definition visitors.
 * Except for {@link #visit(Collection)}, the default implementations throw
 * {@link UnsupportedOperationException}. Override those methods to implement
 * your visitor's functionality.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractPropositionDefinitionVisitor implements
        PropositionDefinitionVisitor {

    /**
     * Processes a collection of proposition definitions.
     *
     * @param propositionDefinitions
     *            a {@link Collection<PropositionDefinition>}.
     */
    @Override
    public void visit(Collection<PropositionDefinition> propositionDefinitions) {
        for (PropositionDefinition def : propositionDefinitions) {
            def.accept(this);
        }
    }

    /**
     * Processes event definitions. This default implementation throws an
     * {@link UnsupportedOperationException).
     *
     * @param eventDefinition
     *            an {@link EventDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(EventDefinition eventDefinition) {
        throw new UnsupportedOperationException(
                "Visiting EventDefinitions is unsupported");
    }

    /**
     * Processes high-level abstraction definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param highLevelAbstractionDefinition
     *            an {@link HighLevelAbstractionDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(
            HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
        throw new UnsupportedOperationException(
                "Visiting HighLevelAbstractionDefinitions is unsupported");

    }

    /**
     * Processes low-level abstraction definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param lowLevelAbstractionDefinition
     *            a {@link LowLevelAbstractionDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(
            LowLevelAbstractionDefinition lowLevelAbstractionDefinition) {
        throw new UnsupportedOperationException(
                "Visiting LowLevelAbstractionDefinitions is unsupported");

    }

    /**
     * Processes primitive parameter definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param primitiveParameterDefinition
     *            a {@link PrimitiveParameterDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) {
        throw new UnsupportedOperationException(
                "Visiting PrimitiveParameterDefinitions is unsupported");

    }

    /**
     * Processes slice abstraction definitions. This default implementation
     * throws an {@link UnsupportedOperationException).
     *
     * @param sliceAbstractionDefinition
     *            a {@link SliceDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(SliceDefinition sliceAbstractionDefinition) {
        throw new UnsupportedOperationException(
                "Visiting SliceAbstractionDefinitions is unsupported");

    }

    /**
     * Processes constant definitions. This default implementation throws an
     * {@link UnsupportedOperationException).
     *
     * @param constantDefinition
     *            a {@link ConstantDefinition}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(ConstantDefinition constantDefinition) {
        throw new UnsupportedOperationException(
                "Visiting ConstantDefinitions is unsupported");
    }
}
