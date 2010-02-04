package org.protempa;

import java.util.Collection;

/**
 * Interface for classes that do processing on abstraction definitions and
 * may throw an exception.
 *
 * @author Andrew Post
 *
 * @see PropositionDefinitionVisitor
 *
 */
public interface PropositionDefinitionCheckedVisitor {
    /**
	 * Process a collection of proposition definitions.
	 *
	 * @param propositionDefinitions
	 *            a {@link Collection<PropositionDefinition>}. Cannot be
	 *            <code>null</code>.
     * @throws ProtempaException if an error occurs.
	 */
	void visit(Collection<PropositionDefinition> propositionDefinitions)
            throws ProtempaException;

	/**
	 * Process a low-level abstraction definition
	 *
	 * @param def
	 *            a {@link LowLevelAbstractionDefinition}. Cannot be
	 *            <code>null</code>.
     * @throws ProtempaException if an error occurs.
	 */
	void visit(LowLevelAbstractionDefinition def) throws ProtempaException;

	/**
	 * Process a high-level abstraction definition
	 *
	 * @param def
	 *            a {@link HighLevelAbstractionDefinition}. Cannot be
	 *            <code>null</code>.
     * @throws ProtempaException if an error occurs.
	 */
	void visit(HighLevelAbstractionDefinition def) throws ProtempaException;

	/**
	 * Process a slice definition.
	 *
	 * @param def
	 *            a {@link SliceDefinition}. Cannot be
	 *            <code>null</code>.
     * @throws ProtempaException if an error occurs.
	 */
	void visit(SliceDefinition def) throws ProtempaException;

	/**
	 * Process an event definition.
	 *
	 * @param def
	 *            an {@link EventDefinition}. Cannot be <code>null</code>.
     * @throws ProtempaException if an error occurs.
	 */
	void visit(EventDefinition def) throws ProtempaException;

	/**
	 * Process a primitive parameter definition.
	 *
	 * @param def
	 *            a {@link PrimitiveParameterDefinition}. Cannot be
	 *            <code>null</code>.
     * @throws ProtempaException if an error occurs.
	 */
	void visit(PrimitiveParameterDefinition def) throws ProtempaException;
}
