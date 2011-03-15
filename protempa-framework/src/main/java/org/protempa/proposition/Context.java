package org.protempa.proposition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.protempa.ProtempaException;

/**
 * A state of affairs that, when interpreted over a time interval, can change
 * the interpretation (abstraction) of one or more parameters within the scope
 * of that time interval.
 * 
 * @author Andrew Post
 */
public final class Context extends TemporalProposition implements Serializable {

    private static final long serialVersionUID = -836727551420756824L;

    Context(String id) {
        super(id);
    }

    @Override
    public void setInterval(Interval interval) {
        super.setInterval(interval);
    }

    @Override
    public void accept(PropositionVisitor propositionVisitor) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    @Override
    public void acceptChecked(PropositionCheckedVisitor propositionCheckedVisitor) throws ProtempaException {
        throw new UnsupportedOperationException("Unimplemented");
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        writeAbstractProposition(s);
        writeTemporalProposition(s);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        readAbstractProposition(s);
        readTemporalProposition(s);
    }
}
