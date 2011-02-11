package org.protempa.proposition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An abstract base class for implementing proposition visitors. Except for
 * {@link #visit(Collection)} and {@link #visit(Map)}, the default
 * implementations are no-ops. Override those methods to implement your
 * visitor's functionality.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractPropositionVisitor
        implements PropositionVisitor {

    @Override
    public void visit(Map<String, List<Proposition>> finderResult) {
        for (List<Proposition> listOfProps : finderResult.values()) {
            visit(listOfProps);
        }

    }

    @Override
    public void visit(Collection<? extends Proposition> propositions) {
        for (Proposition proposition : propositions) {
            proposition.accept(this);
        }
    }

    /**
     * Processes abstract parameters. This default implementation is a no-op.
     *
     * @param abstractParameter
     *            an {@link AbstractParameter}.
     */
    @Override
    public void visit(AbstractParameter abstractParameter) {
    }

    /**
     * Processes events. This default implementation is a no-op.
     *
     * @param event
     *            an {@link Event}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(Event event) {
    }

    /**
     * Processes primitive parameters. This default implementation is a no-op.
     *
     * @param primitiveParameter
     *            an {@link PrimitiveParameter}.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
    }

    /**
     * Processes constants. This default implementation is a no-op.
     *
     * @param primitiveParameter
     *            an {@link PrimitiveParameter}.
     */
    @Override
    public void visit(Constant constant) {
    }

    /**
     * Processes contexts. This default implementation is a no-op.
     *
     * @param context
     *            a {@link Context}.
     */
    @Override
    public void visit(Context context) {
    }
}
