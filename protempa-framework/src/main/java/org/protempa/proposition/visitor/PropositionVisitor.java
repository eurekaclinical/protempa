package org.protempa.proposition.visitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;

/**
 * Interface for classes that do processing on propositions.
 * 
 * @author Andrew Post
 * 
 */
public interface PropositionVisitor {
	/**
	 * Processes results from a PROTEMPA finder method.
	 * 
	 * @param finderResult
	 *            a {@link Map<String, List<Proposition>>}.
	 */
	void visit(Map<String, List<Proposition>> finderResult);

	/**
	 * Processes a collection of propositions.
	 * 
	 * @param propositions
	 *            a {@link Collection<Proposition>}. Cannot be
	 *            <code>null</code>.
	 */
	void visit(Collection<? extends Proposition> propositions);

	/**
	 * Processes a primitive parameter.
	 * 
	 * @param primitiveParameter
	 *            a {@link PrimitiveParameter}. Cannot be <code>null</code>.
	 */
	void visit(PrimitiveParameter primitiveParameter);

	/**
	 * Processes an event.
	 * 
	 * @param event
	 *            an {@link Event}. Cannot be <code>null</code>.
	 */
	void visit(Event event);

	/**
	 * Processes an abstract parameter.
	 * 
	 * @param abstractParameter
	 *            an {@link AbstractParameter}. Cannot be <code>null</code>.
	 */
	void visit(AbstractParameter abstractParameter);

	/**
	 * Processes a constant.
	 * 
	 * @param constant
	 *            an {@link Constant}. Cannot be <code>null</code>.
	 */
	void visit(Constant constant);

	/**
	 * Processes a context.
	 * 
	 * @param context
	 *            a {@link Context}. Cannot be <code>null</code>.
	 */
	void visit(Context context);
}
