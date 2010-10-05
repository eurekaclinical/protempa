package org.protempa.proposition;

import org.protempa.ProtempaException;

/**
 * A state of affairs that, when interpreted over a time interval, can change
 * the interpretation (abstraction) of one or more parameters within the scope
 * of that time interval.
 * 
 * @author Andrew Post
 */
public final class Context extends TemporalProposition {

    private static final long serialVersionUID = -836727551420756824L;

    Context(String id) {
        super(id);
    }

    @Override
    public void accept(PropositionVisitor propositionVisitor) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    @Override
    public void acceptChecked(PropositionCheckedVisitor propositionCheckedVisitor) throws ProtempaException {
        throw new UnsupportedOperationException("Unimplemented");
    }
}
