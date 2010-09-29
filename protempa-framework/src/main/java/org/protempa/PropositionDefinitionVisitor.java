package org.protempa;

import java.util.Collection;

/**
 * Interface for classes that do processing on abstraction definitions.
 * 
 * @author Andrew Post
 * 
 */
public interface PropositionDefinitionVisitor {

    /**
     * Process a collection of proposition definitions.
     *
     * @param propositionDefinitions
     *            a {@link Collection<PropositionDefinition>}. Cannot be
     *            <code>null</code>.
     */
    void visit(Collection<PropositionDefinition> propositionDefinitions);

    /**
     * Process a low-level abstraction definition
     *
     * @param def
     *            a {@link LowLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(LowLevelAbstractionDefinition def);

    /**
     * Process a high-level abstraction definition
     *
     * @param def
     *            a {@link HighLevelAbstractionDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(HighLevelAbstractionDefinition def);

    /**
     * Process a slice definition.
     *
     * @param def
     *            a {@link SliceDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(SliceDefinition def);

    /**
     * Process an event definition.
     *
     * @param def
     *            an {@link EventDefinition}. Cannot be <code>null</code>.
     */
    void visit(EventDefinition def);

    /**
     * Process a primitive parameter definition.
     *
     * @param def
     *            a {@link PrimitiveParameterDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(PrimitiveParameterDefinition def);

    /**
     * Process a constant definition.
     *
     * @param def
     *            a {@link ConstantDefinition}. Cannot be
     *            <code>null</code>.
     */
    void visit(ConstantDefinition def);
}
