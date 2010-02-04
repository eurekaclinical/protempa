package org.protempa;

import java.util.Collection;

/**
 * An abstract base class for implementing proposition definition visitors that
 * may throw exceptions. Except for {@link #visit(Collection)}, the default
 * implementations throw {@link UnsupportedOperationException}. Override those
 * methods to implement your visitor's functionality.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractPropositionDefinitionCheckedVisitor implements
		PropositionDefinitionCheckedVisitor {

	/**
	 * Processes a collection of proposition definitions.
	 * 
	 * @param propositionDefinitions
	 *            a {@link Collection<PropositionDefinition>}.
     * @throws ProtempaException if an error occurs.
     * 
	 * @see org.protempa.PropositionDefinitionCheckedVisitor#visit(java.util.Collection)
	 */
	public void visit(Collection<PropositionDefinition> propositionDefinitions) 
            throws ProtempaException {
		for (PropositionDefinition def : propositionDefinitions) {
			def.acceptChecked(this);
		}
	}

	/**
	 * Processes event definitions. This default implementation throws an
	 * {@link UnsupportedOperationException).
	 * 
	 * @param eventDefinition
	 *            an {@link EventDefinition}.
     * @throws ProtempaException if an error occurs.
	 * @throws UnsupportedOperationException.
	 * @see org.protempa.PropositionDefinitionVisitor#visit(org.protempa.EventDefinition)
	 */
	public void visit(EventDefinition eventDefinition) 
            throws ProtempaException {
		throw new UnsupportedOperationException(
				"Visiting EventDefinitions is unsupported.");
	}

	/**
	 * Processes high-level abstraction definitions. This default implementation
	 * throws an {@link UnsupportedOperationException).
	 * 
	 * @param highLevelAbstractionDefinition
	 *            an {@link HighLevelAbstractionDefinition}.
     * @throws ProtempaException if an error occurs.
	 * @throws UnsupportedOperationException.
	 * @see org.protempa.PropositionDefinitionVisitor#visit(org.protempa.HighLevelAbstractionDefinition)
	 */
	public void visit(
			HighLevelAbstractionDefinition highLevelAbstractionDefinition) 
            throws ProtempaException {
		throw new UnsupportedOperationException(
				"Visiting HighLevelAbstractionDefinitions is unsupported.");

	}

	/**
	 * Processes low-level abstraction definitions. This default implementation
	 * throws an {@link UnsupportedOperationException).
	 * 
	 * @param lowLevelAbstractionDefinition
	 *            a {@link LowLevelAbstractionDefinition}.
     * @throws ProtempaException if an error occurs.
	 * @throws UnsupportedOperationException.
	 * @see org.protempa.PropositionDefinitionVisitor#visit(org.protempa.LowLevelAbstractionDefinition)
	 */
	public void visit(
			LowLevelAbstractionDefinition lowLevelAbstractionDefinition) 
            throws ProtempaException {
		throw new UnsupportedOperationException(
				"Visiting LowLevelAbstractionDefinitions is unsupported.");

	}

	/**
	 * Processes primitive parameter definitions. This default implementation
	 * throws an {@link UnsupportedOperationException).
	 * 
	 * @param primitiveParameterDefinition
	 *            a {@link PrimitiveParameterDefinition}.
	 * @throws UnsupportedOperationException.
	 * @see org.protempa.PropositionDefinitionVisitor#visit(org.protempa.PrimitiveParameterDefinition)
	 */
	public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) 
            throws ProtempaException {
		throw new UnsupportedOperationException(
				"Visiting PrimitiveParameterDefinitions is unsupported.");

	}

	/**
	 * Processes slice abstraction definitions. This default implementation
	 * throws an {@link UnsupportedOperationException).
	 * 
	 * @param sliceAbstractionDefinition
	 *            a {@link SliceDefinition}.
     * @throws ProtempaException if an error occurs.
	 * @throws UnsupportedOperationException.
	 * @see org.protempa.PropositionDefinitionVisitor#visit(org.protempa.SliceDefinition)
	 */
	public void visit(SliceDefinition sliceAbstractionDefinition)
            throws ProtempaException {
		throw new UnsupportedOperationException(
				"Visiting SliceAbstractionDefinitions is unsupported.");

	}

}
